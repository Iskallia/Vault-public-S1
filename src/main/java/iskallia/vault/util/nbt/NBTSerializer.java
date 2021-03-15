package iskallia.vault.util.nbt;

import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class NBTSerializer {

    public static final <T extends INBTSerializable> CompoundNBT serialize(T object) throws IllegalAccessException, UnserializableClassException {

        CompoundNBT t = new CompoundNBT();

        Class<?> definition = object.getClass();
        Field[] df = definition.getDeclaredFields();
        for (Field f : df) {
            if (f.isAnnotationPresent(NBTSerialize.class)) {

                f.setAccessible(true);

                Object fv = f.get(object);
                if (fv == null) continue;

                String tn = f.getAnnotation(NBTSerialize.class).name();
                if (tn.equals("")) tn = f.getName();

                Class fc = f.getType();

                if (fc.isAssignableFrom(byte.class)) t.putByte(tn, (Byte) fv);
                else if (fc.isAssignableFrom(boolean.class)) t.putBoolean(tn, (Boolean) fv);
                else if (fc.isAssignableFrom(short.class)) t.putShort(tn, (Short) fv);
                else if (fc.isAssignableFrom(int.class)) t.putInt(tn, (Integer) fv);
                else if (fc.isAssignableFrom(long.class)) t.putLong(tn, (Long) fv);
                else if (fc.isAssignableFrom(float.class)) t.putFloat(tn, (Float) fv);
                else if (fc.isAssignableFrom(double.class)) t.putDouble(tn, (Double) fv);

                else t.put(tn, objectToTag(fc, fv));
            }
        }

        return t;
    }

    private static final <T, U extends T> INBT objectToTag(Class<T> clazz, U obj) throws IllegalAccessException, UnserializableClassException {

        if (obj == null) return null;

        if (clazz.isAssignableFrom(Byte.class)) return ByteNBT.valueOf((Byte) obj);
        else if (clazz.isAssignableFrom(Boolean.class)) return ByteNBT.valueOf(((Boolean) obj) ? (byte) 1 : (byte) 0);
        else if (clazz.isAssignableFrom(Short.class)) return ShortNBT.valueOf((Short) obj);
        else if (clazz.isAssignableFrom(Integer.class)) return IntNBT.valueOf((Integer) obj);
        else if (clazz.isAssignableFrom(Long.class)) return LongNBT.valueOf((Long) obj);
        else if (clazz.isAssignableFrom(Float.class)) return FloatNBT.valueOf((Float) obj);
        else if (clazz.isAssignableFrom(Double.class)) return DoubleNBT.valueOf((Double) obj);
        else if (clazz.isAssignableFrom(byte[].class)) return new ByteArrayNBT((byte[]) obj);
        else if (clazz.isAssignableFrom(Byte[].class)) return new ByteArrayNBT(ArrayUtils.toPrimitive((Byte[]) obj));
        else if (clazz.isAssignableFrom(String.class)) return StringNBT.valueOf((String) obj);
        else if (clazz.isAssignableFrom(int[].class)) return new IntArrayNBT((int[]) obj);
        else if (clazz.isAssignableFrom(Integer[].class))
            return new IntArrayNBT(ArrayUtils.toPrimitive((Integer[]) obj));

        if (INBTSerializable.class.isAssignableFrom(clazz)) return serialize((INBTSerializable) obj);
        else if (Collection.class.isAssignableFrom(clazz)) return serializeCollection((Collection) obj);

        else if (Map.Entry.class.isAssignableFrom(clazz)) return serializeEntry((Map.Entry) obj);
        else if (Map.class.isAssignableFrom(clazz)) return serializeCollection(((Map) obj).entrySet());

        else throw new UnserializableClassException(clazz);
    }

    private static final <T> ListNBT serializeCollection(Collection<T> col) throws IllegalAccessException, UnserializableClassException {

        ListNBT c = new ListNBT();

        if (col.size() <= 0) return c;
        Class<T> subclass = (Class<T>) col.iterator().next().getClass();

        for (T element : col) {

            INBT tag = objectToTag(subclass, element);
            if (tag != null) {

                c.add(tag);
            }
        }

        return c;
    }

    private static final <K, V> CompoundNBT serializeEntry(Map.Entry<K, V> entry) throws UnserializableClassException, IllegalAccessException {

        Class<K> keyClass = (Class<K>) entry.getKey().getClass();
        Class<V> valueClass = (Class<V>) entry.getValue().getClass();

        return serializeEntry(entry, keyClass, valueClass);
    }

    private static final <K, V> CompoundNBT serializeEntry(Map.Entry<? extends K, ? extends V> entry, Class<K> keyClass, Class<V> valueClass) throws IllegalAccessException, UnserializableClassException {

        CompoundNBT te = new CompoundNBT();

        if (entry.getKey() != null) {
            INBT keyTag = objectToTag(keyClass, entry.getKey());
            te.put("k", keyTag);
        }
        if (entry.getValue() != null) {
            INBT valueTag = objectToTag(valueClass, entry.getValue());
            te.put("v", valueTag);
        }

        return te;
    }

    public static final <T extends INBTSerializable> T deserialize(Class<T> definition, CompoundNBT data) throws IllegalAccessException, InstantiationException, UnserializableClassException {

        T instance = definition.newInstance();

        deserialize(instance, data, true);

        return instance;
    }

    public static final <T extends INBTSerializable> void deserialize(T instance, CompoundNBT data, boolean interpretMissingFieldValuesAsNull) throws IllegalAccessException, InstantiationException, UnserializableClassException {

        Field[] df = instance.getClass().getDeclaredFields();
        for (Field f : df) {
            if (f.isAnnotationPresent(NBTSerialize.class)) {

                String tn = f.getAnnotation(NBTSerialize.class).name();
                if (tn.equals("")) tn = f.getName();

                if (!data.contains(tn)) {
                    if (interpretMissingFieldValuesAsNull) {

                        f.setAccessible(true);
                        if(f.getType().equals(boolean.class)) {
                            f.set(instance, false);
                        } else if(f.getType().equals(int.class)) {
                            f.set(instance, 0);
                        } else {
                            f.set(instance, null);
                        }
                    }

                    continue;
                }

                f.setAccessible(true);

                Class<?> fc;
                Class<?> forceInstantiateAs = f.getAnnotation(NBTSerialize.class).typeOverride();

                if (forceInstantiateAs.isAssignableFrom(Object.class)) {
                    fc = f.getType();
                } else {
                    fc = forceInstantiateAs;
                }

                if (fc.isAssignableFrom(byte.class)) f.setByte(instance, data.getByte(tn));
                else if (fc.isAssignableFrom(boolean.class)) f.setBoolean(instance, data.getBoolean(tn));
                else if (fc.isAssignableFrom(short.class)) f.setShort(instance, data.getShort(tn));
                else if (fc.isAssignableFrom(int.class)) f.setInt(instance, data.getInt(tn));
                else if (fc.isAssignableFrom(long.class)) f.setLong(instance, data.getLong(tn));
                else if (fc.isAssignableFrom(float.class)) f.setFloat(instance, data.getFloat(tn));
                else if (fc.isAssignableFrom(double.class)) f.setDouble(instance, data.getDouble(tn));

                else f.set(instance, tagToObject(data.get(tn), fc, f.getGenericType()));
            }
        }
    }

    private static <T> Collection<T> deserializeCollection(ListNBT list, Class<? extends Collection> colClass, Class<T> subclass, Type subtype) throws InstantiationException, IllegalAccessException, UnserializableClassException {

        Collection<T> c = colClass.newInstance();

        for (int i = 0; i < list.size(); i++) {
            c.add(tagToObject(list.get(i), subclass, subtype));
        }

        return c;
    }

    private static <K, V> Map<K, V> deserializeMap(ListNBT map, Class<? extends Map> mapClass, Class<K> keyClass, Type keyType, Class<V> valueClass, Type valueType) throws InstantiationException, IllegalAccessException, UnserializableClassException {

        Map<K, V> e = mapClass.newInstance();

        for (int i = 0; i < map.size(); i++) {

            K key;
            V value;
            CompoundNBT kvp = (CompoundNBT) map.get(i);

            if (kvp.contains("k")) {
                key = tagToObject(kvp.get("k"), keyClass, keyType);
            } else {
                key = null;
            }
            if (kvp.contains("v")) {
                value = tagToObject(kvp.get("v"), valueClass, valueType);
            } else {
                value = null;
            }

            e.put(key, value);
        }

        return e;
    }

    private static <T> T tagToObject(INBT tag, Class<T> clazz, Type subtype) throws IllegalAccessException, InstantiationException, UnserializableClassException {

        if (clazz.isAssignableFrom(Object.class) || clazz.isAssignableFrom(Number.class)
                || clazz.isAssignableFrom(CharSequence.class) || clazz.isAssignableFrom(Serializable.class)
                || clazz.isAssignableFrom(Comparable.class))

            throw new UnserializableClassException(clazz);

        if (clazz.isAssignableFrom(Byte.class)) return (T) Byte.valueOf(((ByteNBT) tag).getByte());
        else if (clazz.isAssignableFrom(Boolean.class)) return (T) Boolean.valueOf(((ByteNBT) tag).getByte() != 0);
        else if (clazz.isAssignableFrom(Short.class)) return (T) Short.valueOf(((ShortNBT) tag).getShort());
        else if (clazz.isAssignableFrom(Integer.class)) return (T) Integer.valueOf(((IntNBT) tag).getInt());
        else if (clazz.isAssignableFrom(Long.class)) return (T) Long.valueOf(((LongNBT) tag).getLong());
        else if (clazz.isAssignableFrom(Float.class)) return (T) Float.valueOf(((FloatNBT) tag).getFloat());
        else if (clazz.isAssignableFrom(Double.class)) return (T) Double.valueOf(((DoubleNBT) tag).getDouble());
        else if (clazz.isAssignableFrom(byte[].class)) return (T) ((ByteArrayNBT) tag).getByteArray();
        else if (clazz.isAssignableFrom(Byte[].class))
            return (T) ArrayUtils.toObject(((ByteArrayNBT) tag).getByteArray());
        else if (clazz.isAssignableFrom(String.class)) return (T) ((StringNBT) tag).getString();
        else if (clazz.isAssignableFrom(int[].class)) return (T) ((IntArrayNBT) tag).getIntArray();
        else if (clazz.isAssignableFrom(Integer[].class))
            return (T) ArrayUtils.toObject(((IntArrayNBT) tag).getIntArray());

        else if (INBTSerializable.class.isAssignableFrom(clazz)) {

            CompoundNBT ntc = (CompoundNBT) tag;
            return (T) deserialize((Class<? extends INBTSerializable>) clazz, ntc);
        } else if (Collection.class.isAssignableFrom(clazz)) {

            Type listType = ((ParameterizedType) subtype).getActualTypeArguments()[0];

            Class<?> lct;

            if (listType instanceof ParameterizedType) {
                lct = (Class<?>) ((ParameterizedType) listType).getRawType();
            } else {
                lct = (Class<?>) listType;
            }

            ListNBT ntl = (ListNBT) tag;

            Collection c2 = deserializeCollection(ntl, (Class<? extends Collection>) clazz, lct, listType);
            return (T) c2;
        } else if (Map.class.isAssignableFrom(clazz)) {

            Type[] types = ((ParameterizedType) subtype).getActualTypeArguments();
            Type keyType = types[0];
            Type valueType = types[1];

            Class<?> keyClass;
            Class<?> valueClass;

            if (keyType instanceof ParameterizedType) {
                keyClass = (Class<?>) ((ParameterizedType) keyType).getRawType();
            } else {
                keyClass = (Class<?>) keyType;
            }
            if (valueType instanceof ParameterizedType) {
                valueClass = (Class<?>) ((ParameterizedType) valueType).getRawType();
            } else {
                valueClass = (Class<?>) valueType;
            }

            ListNBT ntl = (ListNBT) tag;

            Map c2 = deserializeMap(ntl, (Class<? extends Map>) clazz, keyClass, keyType, valueClass, valueType);
            return (T) c2;
        } else throw new UnserializableClassException(clazz);
    }

    private static int getIDFromClass(Class<?> clazz) {

        if (clazz.isAssignableFrom(byte.class) || clazz.isAssignableFrom(Byte.class) ||
                clazz.isAssignableFrom(boolean.class) || clazz.isAssignableFrom(Boolean.class)) {
            return Constants.NBT.TAG_BYTE;
        } else if (clazz.isAssignableFrom(short.class) || clazz.isAssignableFrom(Short.class))
            return Constants.NBT.TAG_SHORT;
        else if (clazz.isAssignableFrom(int.class) || clazz.isAssignableFrom(Integer.class))
            return Constants.NBT.TAG_INT;
        else if (clazz.isAssignableFrom(long.class) || clazz.isAssignableFrom(Long.class))
            return Constants.NBT.TAG_LONG;
        else if (clazz.isAssignableFrom(float.class) || clazz.isAssignableFrom(Float.class))
            return Constants.NBT.TAG_FLOAT;
        else if (clazz.isAssignableFrom(double.class) || clazz.isAssignableFrom(Double.class))
            return Constants.NBT.TAG_DOUBLE;
        else if (clazz.isAssignableFrom(byte[].class) || clazz.isAssignableFrom(Byte[].class))
            return Constants.NBT.TAG_BYTE_ARRAY;
        else if (clazz.isAssignableFrom(String.class)) return Constants.NBT.TAG_STRING;
        else if (clazz.isAssignableFrom(int[].class) || clazz.isAssignableFrom(Integer[].class))
            return Constants.NBT.TAG_INT_ARRAY;
        else if (INBTSerializable.class.isAssignableFrom(clazz)) return Constants.NBT.TAG_COMPOUND;
        else if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz))
            return Constants.NBT.TAG_LIST;
        return Constants.NBT.TAG_COMPOUND;
    }
}

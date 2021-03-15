package iskallia.vault.util.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.Constants;

import java.util.*;
import java.util.function.Function;

public class NBTHelper {

    public static <T, N extends INBT> Map<UUID, T> readMap(CompoundNBT nbt, String name, Class<N> nbtType, Function<N, T> mapper) {
        Map<UUID, T> res = new HashMap<>();
        ListNBT uuidList = nbt.getList(name + "Keys", Constants.NBT.TAG_STRING);
        ListNBT valuesList = (ListNBT) nbt.get(name + "Values");

        if (uuidList.size() != valuesList.size()) {
            throw new IllegalStateException("Map doesn't have the same amount of keys as values");
        }

        for (int i = 0; i < uuidList.size(); i++) {
            res.put(UUID.fromString(uuidList.get(i).getString()), mapper.apply((N) valuesList.get(i)));
        }

        return res;
    }

    public static <T, N extends INBT> void writeMap(CompoundNBT nbt, String name, Map<UUID, T> map, Class<N> nbtType, Function<T, N> mapper) {
        ListNBT uuidList = new ListNBT();
        ListNBT valuesList = new ListNBT();
        map.forEach((key, value) -> {
            uuidList.add(StringNBT.valueOf(key.toString()));
            valuesList.add(mapper.apply(value));
        });
        nbt.put(name + "Keys", uuidList);
        nbt.put(name + "Values", valuesList);
    }

    public static <T, N extends INBT> List<T> readList(CompoundNBT nbt, String name, Class<N> nbtType, Function<N, T> mapper) {
        List<T> res = new LinkedList<>();
        ListNBT listNBT = (ListNBT) nbt.get(name);

        for (int i = 0; i < listNBT.size(); i++) {
            res.add(mapper.apply((N)listNBT.get(i)));
        }

        return res;
    }

    public static <T, N extends INBT> void writeList(CompoundNBT nbt, String name, Collection<T> list, Class<N> nbtType, Function<T, N> mapper) {
        ListNBT listNBT = new ListNBT();
        list.forEach(item -> listNBT.add(mapper.apply(item)));
        nbt.put(name, listNBT);
    }

}

package iskallia.vault.item.gear.attribute;

import com.google.gson.annotations.Expose;
import net.minecraft.nbt.CompoundNBT;

import java.util.Optional;
import java.util.Random;

public class EnumAttribute<E extends Enum<E>> extends PooledAttribute<E> {

	private final Class<E> enumClass;

	public EnumAttribute(Class<E> enumClass) {
		this.enumClass = enumClass;
	}

	public EnumAttribute(Class<E> enumClass, ItemAttribute.Modifier<E> modifier) {
		super(modifier);
		this.enumClass = enumClass;
	}

	public Class<E> getEnumClass() {
		return this.enumClass;
	}

	@Override
	public void write(CompoundNBT nbt) {
		nbt.putString("BaseValue", this.getBaseValue().name());
	}

	@Override
	public void read(CompoundNBT nbt) {
		this.setBaseValue(this.getEnumConstant(nbt.getString("BaseValue")));
	}

	public E getEnumConstant(String value) {
		try {
			return Enum.valueOf(this.getEnumClass(), value);
		} catch (Exception e) {
			E[] enumConstants = this.getEnumClass().getEnumConstants();
			return enumConstants.length == 0 ? null : enumConstants[0];
		}
	}

	public static <E extends Enum<E>> EnumAttribute.Generator<E> generator(Class<E> enumClass) {
		return new EnumAttribute.Generator<>();
	}

	public static <E extends Enum<E>> EnumAttribute.Generator.Operator<E> of(Type type) {
		return new EnumAttribute.Generator.Operator<>(type);
	}

	public static class Generator<E extends Enum<E>> extends PooledAttribute.Generator<E, Generator.Operator<E>> {
		@Override
		public E getDefaultValue(Random random) {
			return null;
		}

		public static class Operator<E extends Enum<E>> extends PooledAttribute.Generator.Operator<E> {
			@Expose
			protected String type;

			public Operator(Type type) {
				this.type = type.name();
			}

			public Type getType() {
				return Type.getByName(this.type).orElseThrow(() -> new IllegalStateException("Unknown type \"" + this.type + "\""));
			}

			@Override
			public E apply(E value, E modifier) {
				if(this.getType() == Type.SET) {
					return modifier;
				}

				return value;
			}
		}
	}

	public enum Type {
		SET;

		public static Optional<Type> getByName(String name) {
			for(Type value : Type.values()) {
				if(value.name().equalsIgnoreCase(name)) {
					return Optional.of(value);
				}
			}

			return Optional.empty();
		}
	}

}

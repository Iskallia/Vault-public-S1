package iskallia.vault.item.gear.attribute;

import com.google.gson.annotations.Expose;
import net.minecraft.nbt.CompoundNBT;

import java.util.Optional;
import java.util.Random;

public class BooleanAttribute extends PooledAttribute<Boolean> {

	public BooleanAttribute() {

	}

	public BooleanAttribute(ItemAttribute.Modifier<Boolean> modifier) {
		super(modifier);
	}

	@Override
	public void write(CompoundNBT nbt) {
		nbt.putBoolean("BaseValue", this.getBaseValue());
	}

	@Override
	public void read(CompoundNBT nbt) {
		this.setBaseValue(nbt.getBoolean("BaseValue"));
	}

	public static class Generator extends PooledAttribute.Generator<Boolean, Generator.Operator> {
		@Override
		public Boolean getDefaultValue(Random random) {
			return false;
		}

		public static class Operator extends PooledAttribute.Generator.Operator<Boolean> {
			@Expose protected String type;

			public Operator(Type type) {
				this.type = type.name();
			}

			public Type getType() {
				return Type.getByName(this.type).orElseThrow(() -> new IllegalStateException("Unknown type \"" + this.type + "\""));
			}

			@Override
			public Boolean apply(Boolean value, Boolean modifier) {
				if(this.getType() == Type.SET) {
					return modifier;
				} else if(this.getType() == Type.AND) {
					return value & modifier;
				} else if(this.getType() == Type.OR) {
					return value | modifier;
				} else if(this.getType() == Type.XOR) {
					return value ^ modifier;
				} else if(this.getType() == Type.NAND) {
					return !(value & modifier);
				} else if(this.getType() == Type.NOR) {
					return !(value | modifier);
				} else if(this.getType() == Type.XNOR) {
					return value == modifier;
				}

				return value;
			}
		}
	}

	public enum Type {
		SET, AND, OR, XOR, NAND, NOR, XNOR;

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

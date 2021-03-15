package iskallia.vault.item.gear.attribute;

import net.minecraft.nbt.CompoundNBT;

import java.util.Random;

public class IntegerAttribute extends NumberAttribute<Integer> {

	public IntegerAttribute() {

	}

	public IntegerAttribute(ItemAttribute.Modifier<Integer> modifier) {
		super(modifier);
	}

	@Override
	public void write(CompoundNBT nbt) {
		nbt.putInt("BaseValue", this.getBaseValue());
	}

	@Override
	public void read(CompoundNBT nbt) {
		this.setBaseValue(nbt.getInt("BaseValue"));
	}

	public static Generator generator() {
		return new Generator();
	}

	public static Generator.Operator of(NumberAttribute.Type type) {
		return new Generator.Operator(type);
	}

	public static class Generator extends NumberAttribute.Generator<Integer, Generator.Operator> {
		@Override
		public Integer getDefaultValue(Random random) {
			return 0;
		}

		public static Operator of(Type type) {
			return new Operator(type);
		}

		public static class Operator extends NumberAttribute.Generator.Operator<Integer> {
			public Operator(Type type) {
				super(type);
			}

			@Override
			public Integer apply(Integer value, Integer modifier) {
				if(this.getType() == Type.SET) {
					return modifier;
				} else if(this.getType() == Type.ADD) {
					return value + modifier;
				} else if(this.getType() == Type.MULTIPLY) {
					return value * modifier;
				}

				return value;
			}
		}
	}

}

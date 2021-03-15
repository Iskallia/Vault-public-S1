package iskallia.vault.item.gear.attribute;

import net.minecraft.nbt.CompoundNBT;

import java.util.Random;

public class DoubleAttribute extends NumberAttribute<Double> {

	public DoubleAttribute() {

	}

	public DoubleAttribute(ItemAttribute.Modifier<Double> modifier) {
		super(modifier);
	}

	@Override
	public void write(CompoundNBT nbt) {
		nbt.putDouble("BaseValue", this.getBaseValue());
	}

	@Override
	public void read(CompoundNBT nbt) {
		this.setBaseValue(nbt.getDouble("BaseValue"));
	}

	public static Generator generator() {
		return new Generator();
	}

	public static Generator.Operator of(NumberAttribute.Type type) {
		return new Generator.Operator(type);
	}

	public static class Generator extends NumberAttribute.Generator<Double, Generator.Operator> {
		@Override
		public Double getDefaultValue(Random random) {
			return 0.0D;
		}

		public static class Operator extends NumberAttribute.Generator.Operator<Double> {
			public Operator(Type type) {
				super(type);
			}

			@Override
			public Double apply(Double value, Double modifier) {
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

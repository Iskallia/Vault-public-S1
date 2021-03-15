package iskallia.vault.item.gear.attribute;

import net.minecraft.nbt.CompoundNBT;

import java.util.Random;

public class FloatAttribute extends NumberAttribute<Float> {

	public FloatAttribute() {

	}

	public FloatAttribute(ItemAttribute.Modifier<Float> modifier) {
		super(modifier);
	}

	@Override
	public void write(CompoundNBT nbt) {
		nbt.putFloat("BaseValue", this.getBaseValue());
	}

	@Override
	public void read(CompoundNBT nbt) {
		this.setBaseValue(nbt.getFloat("BaseValue"));
	}

	public static Generator generator() {
		return new Generator();
	}

	public static Generator.Operator of(NumberAttribute.Type type) {
		return new Generator.Operator(type);
	}

	public static class Generator extends NumberAttribute.Generator<Float, Generator.Operator> {
		@Override
		public Float getDefaultValue(Random random) {
			return 0.0F;
		}

		public static class Operator extends NumberAttribute.Generator.Operator<Float> {
			public Operator(Type type) {
				super(type);
			}

			@Override
			public Float apply(Float value, Float modifier) {
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

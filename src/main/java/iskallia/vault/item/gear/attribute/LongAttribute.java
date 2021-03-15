package iskallia.vault.item.gear.attribute;

import net.minecraft.nbt.CompoundNBT;

import java.util.Random;

public class LongAttribute extends NumberAttribute<Long> {

	public LongAttribute() {

	}

	public LongAttribute(ItemAttribute.Modifier<Long> modifier) {
		super(modifier);
	}

	@Override
	public void write(CompoundNBT nbt) {
		nbt.putLong("BaseValue", this.getBaseValue());
	}

	@Override
	public void read(CompoundNBT nbt) {
		this.setBaseValue(nbt.getLong("BaseValue"));
	}

	public static Generator generator() {
		return new Generator();
	}

	public static Generator.Operator of(NumberAttribute.Type type) {
		return new Generator.Operator(type);
	}

	public static class Generator extends NumberAttribute.Generator<Long, Generator.Operator> {
		@Override
		public Long getDefaultValue(Random random) {
			return 0L;
		}

		public static Operator of(Type type) {
			return new Operator(type);
		}

		public static class Operator extends NumberAttribute.Generator.Operator<Long> {
			public Operator(Type type) {
				super(type);
			}

			@Override
			public Long apply(Long value, Long modifier) {
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

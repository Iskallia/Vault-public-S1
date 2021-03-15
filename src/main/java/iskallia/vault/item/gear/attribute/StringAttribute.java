package iskallia.vault.item.gear.attribute;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import iskallia.vault.util.gson.IgnoreEmpty;
import net.minecraft.nbt.CompoundNBT;

import java.util.Optional;
import java.util.Random;

public class StringAttribute extends PooledAttribute<String> {

	public StringAttribute() {

	}

	public StringAttribute(ItemAttribute.Modifier<String> modifier) {
		super(modifier);
	}

	@Override
	public void write(CompoundNBT nbt) {
		nbt.putString("BaseValue", this.getBaseValue());
	}

	@Override
	public void read(CompoundNBT nbt) {
		this.setBaseValue(nbt.getString("BaseValue"));
	}

	public static class Generator extends PooledAttribute.Generator<String, Generator.Operator> {
		@Override
		public String getDefaultValue(Random random) {
			return "";
		}

		public static class Operator extends PooledAttribute.Generator.Operator<String> {
			@Expose protected String type;
			@Expose @JsonAdapter(IgnoreEmpty.StringAdapter.class)protected String delimiter;
			@Expose @JsonAdapter(IgnoreEmpty.StringAdapter.class)protected String regex;

			public Operator(Type type) {
				this.type = type.name();
			}

			public Type getType() {
				return Type.getByName(this.type).orElseThrow(() -> new IllegalStateException("Unknown type \"" + this.type + "\""));
			}

			@Override
			public String apply(String value, String modifier) {
				if(this.getType() == Type.SET) {
					return modifier;
				} else if(this.getType() == Type.APPEND) {
					return value + modifier;
				} else if(this.getType() == Type.JOIN) {
					return value + this.delimiter + modifier;
				} else if(this.getType() == Type.REPLACE_FIRST) {
					return value.replaceFirst(this.regex, modifier);
				} else if(this.getType() == Type.REPLACE_ALL) {
					return value.replaceAll(this.regex, modifier);
				}

				return value;
			}
		}
	}

	public enum Type {
		SET, APPEND, JOIN, REPLACE_FIRST, REPLACE_ALL;

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

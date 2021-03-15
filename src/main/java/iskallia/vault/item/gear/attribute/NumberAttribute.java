package iskallia.vault.item.gear.attribute;

import com.google.gson.annotations.Expose;

import java.util.Optional;

public abstract class NumberAttribute<T> extends PooledAttribute<T> {

	protected NumberAttribute() {

	}

	protected NumberAttribute(ItemAttribute.Modifier<T> modifier) {
		super(modifier);
	}

	public static abstract class Generator<T, O extends Generator.Operator<T>> extends PooledAttribute.Generator<T, O> {

		public static abstract class Operator<T> extends PooledAttribute.Generator.Operator<T> {
			@Expose protected String type;

			public Operator(Type type) {
				this.type = type.name();
			}

			public Type getType() {
				return Type.getByName(this.type).orElseThrow(() -> new IllegalStateException("Unknown type \"" + this.type + "\""));
			}
		}
	}

	public enum Type {
		SET, ADD, MULTIPLY;

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

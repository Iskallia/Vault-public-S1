package iskallia.vault.item.gear.attribute;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import iskallia.vault.util.gson.IgnoreEmpty;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.ToIntBiFunction;
import java.util.stream.IntStream;

public abstract class PooledAttribute<T> extends ItemAttribute.Instance<T> {

	protected PooledAttribute() {

	}

	protected PooledAttribute(ItemAttribute.Modifier<T> modifier) {
		super(modifier);
	}

	public static abstract class Generator<T, O extends Generator.Operator<T>> implements ItemAttribute.Instance.Generator<T> {
		@Expose public List<Pool<T, O>> pools = new ArrayList<>();
		@Expose public O collector;

		public Generator<T, O> add(T base, Rolls rolls, Consumer<Pool<T, O>> pool) {
			if(this.pools == null) {
				this.pools = new ArrayList<>();
			}

			Pool<T, O> generated = new Pool<>(base, rolls);
			this.pools.add(generated);
			pool.accept(generated);
			return this;
		}

		public Generator<T, O> collect(O collector) {
			this.collector = collector;
			return this;
		}

		public abstract T getDefaultValue(Random random);

		@Override
		public T generate(ItemStack stack, Random random) {
			if(this.pools.size() == 0) {
				return this.getDefaultValue(random);
			}

			T value = this.pools.get(0).generate(random);

			for(int i = 1; i < this.pools.size(); i++) {
				value = this.collector.apply(value, this.pools.get(i).generate(random));
			}

			return value;
		}

		public static abstract class Operator<T> extends Pool.Operator<T> {

		}
	}

	public static class Pool<T, O extends Pool.Operator<T>> {
		@Expose public T base;
		@Expose public Rolls rolls;
		@Expose public List<Pool.Entry<T, O>> entries = new ArrayList<>();

		private int totalWeight;

		public Pool(T base, Rolls rolls) {
			this.base = base;
			this.rolls = rolls;
		}

		public Pool<T, O> add(T value, O operator, int weight) {
			if(this.entries == null) {
				this.entries = new ArrayList<>();
			}

			Pool.Entry<T, O> entry = new Pool.Entry<>(value, operator, weight);
			this.entries.add(entry);
			return this;
		}

		public T generate(Random random) {
			if(this.entries.isEmpty() || this.rolls.type.equals(Rolls.Type.EMPTY.name)) {
				return this.base;
			}

			int roll = this.rolls.getRolls(random);
			T value = this.base;

			for(int i = 0; i < roll; i++) {
				Pool.Entry<T, O> entry = this.getRandom(random);
				value = entry.operator.apply(value, entry.value);
			}

			return value;
		}

		public Pool.Entry<T, O> getRandom(Random random) {
			if(this.entries.size() == 0)return null;
			return this.getWeightedAt(random.nextInt(this.getTotalWeight()));
		}

		public Pool.Entry<T, O> getWeightedAt(int index) {
			Pool.Entry<T, O> current = null;

			for(Pool.Entry<T, O> entry: this.entries) {
				current = entry;
				index -= current.weight;
				if(index < 0) break;
			}

			return current;
		}

		private int getTotalWeight() {
			if(this.totalWeight == 0) {
				this.entries.forEach(entry -> this.totalWeight += entry.weight);
			}

			return this.totalWeight;
		}

		public static class Entry<T, O extends Pool.Operator<T>> {
			@Expose public final T value;
			@Expose public final O operator;
			@Expose public final int weight;

			public Entry(T value, O operator, int weight) {
				this.value = value;
				this.operator = operator;
				this.weight = weight;
			}
		}

		public static abstract class Operator<T> {
			public abstract T apply(T value, T modifier);
		}
	}

	public static class Rolls {
		@Expose public String type;

		@Expose @JsonAdapter(IgnoreEmpty.IntegerAdapter.class) public int value;

		@Expose @JsonAdapter(IgnoreEmpty.IntegerAdapter.class) public int min;
		@Expose @JsonAdapter(IgnoreEmpty.IntegerAdapter.class) public int max;

		@Expose @JsonAdapter(IgnoreEmpty.DoubleAdapter.class) public double chance;

		@Expose @JsonAdapter(IgnoreEmpty.IntegerAdapter.class) public int trials;
		@Expose @JsonAdapter(IgnoreEmpty.DoubleAdapter.class) public double probability;

		public static Rolls ofEmpty() {
			Rolls rolls = new Rolls();
			rolls.type = Rolls.Type.EMPTY.name;
			return rolls;
		}

		public static Rolls ofConstant(int value) {
			Rolls rolls = new Rolls();
			rolls.type = Rolls.Type.CONSTANT.name;
			rolls.value = value;
			return rolls;
		}

		public static Rolls ofUniform(int min, int max) {
			Rolls rolls = new Rolls();
			rolls.type = Rolls.Type.UNIFORM.name;
			rolls.min = min;
			rolls.max = max;
			return rolls;
		}

		public static Rolls ofChance(double chance, int value) {
			Rolls rolls = new Rolls();
			rolls.type = Rolls.Type.CHANCE.name;
			rolls.value = value;
			rolls.chance = chance;
			return rolls;
		}

		public static Rolls ofBinomial(int trials, double probability) {
			Rolls rolls = new Rolls();
			rolls.type = Rolls.Type.BINOMIAL.name;
			rolls.trials = trials;
			rolls.probability = probability;
			return rolls;
		}

		public int getRolls(Random random) {
			Rolls.Type type = Rolls.Type.getByName(this.type);

			if(type == null) {
				throw new IllegalStateException("Unknown rolls type \"" + this.type + "\"");
			}

			return type.function.applyAsInt(this, random);
		}

		public enum Type {
			EMPTY("empty", (rolls, random) -> 0),
			CONSTANT("constant", (rolls, random) -> rolls.value),
			UNIFORM("uniform", (rolls, random) -> random.nextInt(rolls.max - rolls.min + 1) + rolls.min),
			CHANCE("chance", (rolls, random) -> random.nextDouble() < rolls.chance ? rolls.value : 0),
			BINOMIAL("binomial", (rolls, random) -> (int) IntStream.range(0, rolls.trials).filter(i -> random.nextDouble() < rolls.probability).count());

			public final String name;
			private final ToIntBiFunction<Rolls, Random> function;

			Type(String name, ToIntBiFunction<Rolls, Random> function) {
				this.name = name;
				this.function = function;
			}

			public static Rolls.Type getByName(String name) {
				for(Rolls.Type value : Rolls.Type.values()) {
					if(value.name.equals(name))return value;
				}

				return null;
			}
		}
	}
	
}

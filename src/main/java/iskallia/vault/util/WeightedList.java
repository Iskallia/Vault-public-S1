package iskallia.vault.util;

import com.google.gson.annotations.Expose;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WeightedList<T> extends AbstractList<WeightedList.Entry<T>> implements List<WeightedList.Entry<T>> {

	@Expose private Entry<T>[] entries;
	private int size;

	public WeightedList() {
		this.entries = new Entry[8];
		this.size = -1;
	}

	@Override
	public int size() {
		this.ensureCapacity();
		return this.size;
	}

	@Override
	public Entry<T> get(int index) {
		return this.entries[index];
	}

	@Override
	public boolean add(Entry<T> entry) {
		this.ensureCapacity();
		this.entries[this.size++] = entry;
		return true;
	}

	public WeightedList<T> add(T value, int weight) {
		this.add(new Entry<>(value, weight));
		return this;
	}

	@Override
	public Entry<T> remove(int index) {
		this.ensureCapacity();
		Entry<T> e = this.get(index);

		for(int i = index + 1; i < this.size; i++) {
			this.entries[i - 1] = this.entries[i];
		}

		this.entries[--this.size] = null;
		return e;
	}
	private void ensureCapacity() {
		if(this.size == -1) {
			for(int i = 0; i < this.entries.length; i++) {
				if(this.entries[i] == null) {
					this.size = i;
					break;
				}
			}
		}

		if(this.size != this.entries.length) {
			return;
		}

		int newLength = this.entries.length << 1;

		if(newLength - (Integer.MAX_VALUE - 8) > 0) {
			throw new OutOfMemoryError();
		}

		this.entries = Arrays.copyOf(this.entries, newLength);
	}

	public WeightedList<T> strip() {
		this.entries = Arrays.copyOf(this.entries, this.size);
		return this;
	}

	public int getTotalWeight() {
		int totalWeight = 0;

		for(int i = 0; i < this.size(); i++) {
			totalWeight += this.get(i).weight;
		}

		return totalWeight;
	}

	public T getWeightedAt(int index) {
		T current = null;

		for(int i = 0; i < this.size(); i++) {
			Entry<T> e = this.get(i);
			current = e.value;
			index -= e.weight;
			if(index < 0)break;
		}

		return current;
	}

	public T getRandom(Random random) {
		int totalWeight = this.getTotalWeight();
		if(totalWeight == 0)return null;
		return this.getWeightedAt(random.nextInt(totalWeight));
	}

	public static class Entry<T> {
		@Expose public final T value;
		@Expose public final int weight;

		public Entry(T value, int weight) {
			this.value = value;
			this.weight = weight;
		}
	}

}

package iskallia.vault.item.gear.attribute;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;
import java.util.function.Supplier;

public class ItemAttribute<T, I extends ItemAttribute.Instance<T>> {

	private final ResourceLocation id;
	private final Supplier<I> instance;
	private final List<ItemAttribute<T, I>> modifiers;

	public ItemAttribute(ResourceLocation id, Supplier<I> instance, ItemAttribute<T, I>... modifiers) {
		this.id = id;
		this.instance = instance;
		this.modifiers = new ArrayList<>(Arrays.asList(modifiers));
	}

	public ResourceLocation getId() {
		return this.id;
	}

	protected String getTagKey() {
		return "Attributes";
	}

	public Optional<I> get(ItemStack stack) {
		CompoundNBT nbt = stack.getTagElement("Vault");
		if(nbt == null || !nbt.contains(this.getTagKey(), Constants.NBT.TAG_LIST))return Optional.empty();

		ListNBT attributesList = nbt.getList(this.getTagKey(), Constants.NBT.TAG_COMPOUND);

		for(INBT element: attributesList) {
			CompoundNBT tag = (CompoundNBT)element;

			if(tag.getString("Id").equals(this.getId().toString())) {
				I instance = this.instance.get();
				instance.parent = this;
				instance.delegate = tag;
				instance.read(tag);
				return Optional.of(instance);
			}
		}

		return Optional.empty();
	}

	public boolean exists(ItemStack stack) {
		return this.get(stack).isPresent();
	}

	public I getOrDefault(ItemStack stack, T value) {
		return this.getOrDefault(stack, () -> value);
	}

	public I getOrDefault(ItemStack stack, Random random, Instance.Generator<T> generator) {
		return this.getOrDefault(stack, () -> generator.generate(stack, random));
	}

	public I getOrDefault(ItemStack stack, Supplier<T> value) {
		return this.get(stack).orElse((I)this.instance.get().setBaseValue(value.get()));
	}

	public I getOrCreate(ItemStack stack, T value) {
		return this.getOrCreate(stack, () -> value);
	}

	public I getOrCreate(ItemStack stack, Random random, Instance.Generator<T> generator) {
		return this.getOrCreate(stack, () -> generator.generate(stack, random));
	}

	public I getOrCreate(ItemStack stack, Supplier<T> value) {
		return this.get(stack).orElseGet(() -> this.create(stack, value));
	}

	public I create(ItemStack stack, T value) {
		return this.create(stack, () -> value);
	}

	public I create(ItemStack stack, Random random, Instance.Generator<T> generator) {
		return this.create(stack, () -> generator.generate(stack, random));
	}

	public I create(ItemStack stack, Supplier<T> value) {
		CompoundNBT nbt = stack.getOrCreateTagElement("Vault");
		if(!nbt.contains(this.getTagKey(), Constants.NBT.TAG_LIST))nbt.put(this.getTagKey(), new ListNBT());
		ListNBT attributesList = nbt.getList(this.getTagKey(), Constants.NBT.TAG_COMPOUND);

		CompoundNBT attributeNBT = attributesList.stream()
				.map(element -> (CompoundNBT)element)
				.filter(tag -> tag.getString("Id").equals(this.getId().toString()))
				.findFirst()
				.orElseGet(() -> {
					CompoundNBT tag = new CompoundNBT();
					attributesList.add(tag);
					return tag;
				});

		I instance = this.instance.get();
		instance.parent = this;
		instance.delegate = attributeNBT;
		instance.setBaseValue(value.get());
		return instance;
	}

	@FunctionalInterface
	public interface Modifier<T> {
		T apply(ItemStack stack, ItemAttribute.Instance<T> parent, T value);
	}

	public static abstract class Instance<T> implements INBTSerializable<CompoundNBT>, Modifier<T> {
		protected ItemAttribute<T, ? extends Instance<T>> parent;

		protected T baseValue;
		private Modifier<T> modifier;

		protected CompoundNBT delegate;

		protected Instance() {

		}

		protected Instance(Modifier<T> modifier) {
			this.modifier = modifier;
		}

		@Override
		public final CompoundNBT serializeNBT() {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putString("Id", this.parent.id.toString());
			this.write(nbt);
			return nbt;
		}

		@Override
		public final void deserializeNBT(CompoundNBT nbt) {
			this.read(nbt);
		}

		public abstract void write(CompoundNBT nbt);

		public abstract void read(CompoundNBT nbt);

		public T getBaseValue() {
			return this.baseValue;
		}

		public Instance<T> setBaseValue(T baseValue) {
			this.baseValue = baseValue;
			this.updateNBT();
			return this;
		}

		public T getValue(ItemStack stack) {
			T value = this.getBaseValue();
			if(this.parent == null)return value;

			for(ItemAttribute<T, ? extends Instance<T>> modifier: this.parent.modifiers) {
				Optional<? extends Instance<T>> instance = modifier.get(stack);

				if(instance.isPresent()) {
					value = instance.get().apply(stack, instance.get(), value);
				}
			}

			return value;
		}

		@Override
		public T apply(ItemStack stack, Instance<T> parent, T value) {
			return this.modifier == null ? value : this.modifier.apply(stack, parent, value);
		}

		protected void updateNBT() {
			if(this.delegate == null)return;
			CompoundNBT nbt = this.serializeNBT();

			for(String key: nbt.getAllKeys()) {
				INBT value = nbt.get(key);

				if(value != null) {
					this.delegate.put(key, value);
				}
			}
		}

		@FunctionalInterface
		public interface Generator<T> {
			T generate(ItemStack stack, Random random);
		}
	}

}

package iskallia.vault.item.gear.modifier;

import iskallia.vault.item.gear.attribute.ItemAttribute;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public abstract class ItemModifier<T, I extends ItemAttribute.Instance<T>> extends ItemAttribute<T, I> {

	public ItemModifier(ResourceLocation id, Supplier<I> instance) {
		super(id, instance);
	}

	@Override
	protected String getTagKey() {
		return "Modifiers";
	}

	public abstract T apply(ItemAttribute.Instance<T> attribute, T value);

}

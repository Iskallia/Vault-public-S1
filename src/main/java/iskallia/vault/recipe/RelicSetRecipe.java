package iskallia.vault.recipe;

import iskallia.vault.block.item.RelicStatueBlockItem;
import iskallia.vault.init.ModRecipes;
import iskallia.vault.item.RelicPartItem;
import iskallia.vault.util.RelicSet;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RelicSetRecipe extends SpecialRecipe {

	public RelicSetRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingInventory inv, World world) {
		RelicSet set = null;
		Set<RelicPartItem> parts = new HashSet<>();

		for(int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);

			if(stack.getItem() instanceof RelicPartItem) {
				if(set != null && ((RelicPartItem)stack.getItem()).getRelicSet() != set) {
					return false;
				}

				set = ((RelicPartItem)stack.getItem()).getRelicSet();
				parts.add((RelicPartItem)stack.getItem());
			} else if(!stack.isEmpty()) {
				return false;
			}
		}

		return set != null && parts.size() == set.getItemSet().size();
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		for(int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);

			if(stack.getItem() instanceof RelicPartItem) {
				RelicSet set = ((RelicPartItem)stack.getItem()).getRelicSet();
				return RelicStatueBlockItem.withRelicSet(set);
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height) {
		Optional<RelicSet> min = RelicSet.getAll().stream().min(Comparator.comparingInt(o -> o.getItemSet().size()));
		return min.isPresent() && width * height >= min.get().getItemSet().size();
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.Serializer.CRAFTING_SPECIAL_RELIC_SET;
	}

}

package iskallia.vault.recipe;

import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModRecipes;
import iskallia.vault.item.RelicPartItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class UnidentifiedRelicRecipe extends SpecialRecipe {

	public UnidentifiedRelicRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingInventory inv, World world) {
		RelicPartItem relic = null;
		int diamondCount = 0;

		for(int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);

			if(stack.getItem() == ModItems.VAULT_DIAMOND) {
				if(diamondCount++ == 8)return false;
			} else if(stack.getItem() instanceof RelicPartItem) {
				if(relic != null)return false;
				relic = (RelicPartItem)stack.getItem();
			} else {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		return new ItemStack(ModItems.UNIDENTIFIED_RELIC);
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 9;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.Serializer.CRAFTING_SPECIAL_UNIDENTIFIED_RELIC;
	}

}
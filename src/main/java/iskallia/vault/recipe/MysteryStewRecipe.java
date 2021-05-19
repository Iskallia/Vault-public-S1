package iskallia.vault.recipe;

import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModRecipes;
import iskallia.vault.item.ItemVaultGem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MysteryStewRecipe extends SpecialRecipe {

	public MysteryStewRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingInventory inv, World world) {
		int gemCount = 0;
		boolean hasBowl = false, hasDiamond = false, hasEye = false, hasPizza = false, hasSchroom = false, hasKiwi = false;

		for(int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);

			if(stack.getItem() instanceof ItemVaultGem) {
				if(++gemCount > 3)return false;
			} else if(stack.getItem() == Items.BOWL) {
				if(hasBowl)return false;
				hasBowl = true;
			} else if(stack.getItem() == ModItems.VAULT_DIAMOND) {
				if(hasDiamond)return false;
				hasDiamond = true;
			} else if(stack.getItem() == ModItems.HUNTER_EYE) {
				if(hasEye)return false;
				hasEye = true;
			} else if(stack.getItem() == ModItems.OOZING_PIZZA) {
				if(hasPizza)return false;
				hasPizza = true;
			} else if(stack.getItem() == ModItems.POISONOUS_MUSHROOM) {
				if(hasSchroom)return false;
				hasSchroom = true;
			} else if(stack.getItem() == ModItems.SWEET_KIWI) {
				if(hasKiwi)return false;
				hasKiwi = true;
			}
		}

		return gemCount == 3 && hasBowl && hasDiamond && hasEye && hasPizza && hasSchroom && hasKiwi;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		return new ItemStack(ModItems.VAULT_STEW_MYSTERY);
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 9;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.Serializer.CRAFTING_SPECIAL_RELIC_SET;//CRAFTING_SPECIAL_MYSTERY_STEW;
	}

}

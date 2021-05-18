package iskallia.vault.recipe;

import com.google.gson.JsonObject;

import iskallia.vault.init.ModRecipes;
import iskallia.vault.item.ItemVaultCrystal;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.*;


/**
 * This class manages crystal upgrade recipes.
 * The custom processing is necessary to keep boss names in the crystals.
 */
public class UpgradeCrystalRecipe extends ShapedRecipe
{
	/**
	 * Class serializer instance.
	 */
	public static final Serializer SERIALIZER = new Serializer();


	/**
	 * Default constructor.
	 */
	public UpgradeCrystalRecipe(ResourceLocation idIn,
		String groupIn,
		int recipeWidthIn,
		int recipeHeightIn,
		NonNullList<Ingredient> recipeItemsIn,
		ItemStack recipeOutputIn)
	{
		super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
	}


	/**
	 * This method creates crafting result based on input crystals.
	 * If crystal has a boss name assigned to it, it randomly chooses one name based on used crystal names.
	 */
	@Override
	public ItemStack getCraftingResult(CraftingInventory inventory)
	{
		List<String> bossNames = new ArrayList<>(inventory.getSizeInventory());

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack.getItem() instanceof ItemVaultCrystal)
			{
				if (stack.hasTag() && stack.getTag().contains("playerBossName", Constants.NBT.TAG_STRING))
				{
					bossNames.add(stack.getTag().getString("playerBossName"));
				}
			}
		}

		ItemStack returnCrystal = super.getCraftingResult(inventory);

		if (!bossNames.isEmpty())
		{
			Collections.shuffle(bossNames);
			returnCrystal.getOrCreateTag().putString("playerBossName", bossNames.get(0));
		}

		return returnCrystal;
	}


	/**
	 * Custom shaped recipe serializer that allows to save and load Upgrade Crystal Recipes.
	 */
	public static class Serializer extends ShapedRecipe.Serializer
	{
		/**
		 * Default constructor.
		 */
		Serializer()
		{
			this.setRegistryName(ModRecipes.UPGRADE_CRYSTAL_RECIPE.toString());
		}


		/**
		 * Reader that transforms given shaped recipe to UpgradeCrystalRecipe.
		 */
		public ShapedRecipe read(ResourceLocation recipeId, JsonObject json) {
			ShapedRecipe shapedRecipe = super.read(recipeId, json);

			return new UpgradeCrystalRecipe(shapedRecipe.getId(),
				shapedRecipe.getGroup(),
				shapedRecipe.getRecipeWidth(),
				shapedRecipe.getRecipeHeight(),
				shapedRecipe.getIngredients(),
				shapedRecipe.getRecipeOutput());
		}


		/**
		 * Reader that transforms given shaped recipe to UpgradeCrystalRecipe.
		 */
		public ShapedRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			ShapedRecipe shapedRecipe = super.read(recipeId, buffer);

			return new UpgradeCrystalRecipe(shapedRecipe.getId(),
				shapedRecipe.getGroup(),
				shapedRecipe.getRecipeWidth(),
				shapedRecipe.getRecipeHeight(),
				shapedRecipe.getIngredients(),
				shapedRecipe.getRecipeOutput());
		}
	}
}
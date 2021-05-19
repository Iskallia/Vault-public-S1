package iskallia.vault.recipe;

import com.google.gson.JsonObject;

import iskallia.vault.container.KeyPressContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModRecipes;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;


/**
 * Key Press Recipes class.
 */
public class KeyPressRecipe implements IRecipe<IInventory>
{
	/**
	 * Class serializer.
	 */
	public static final Serializer SERIALIZER = new Serializer();

	/**
	 * Key slot ingredient.
	 */
	private final Ingredient key;

	/**
	 * Cluster slot ingredient.
	 */
	private final Ingredient cluster;

	/**
	 * Result from crafting.
	 */
	private final ItemStack result;

	/**
	 * Recipe resource ID.
	 */
	private final ResourceLocation recipeId;


	// ---------------------------------------------------------------------
	// Section: Constructor
	// ---------------------------------------------------------------------


	/**
	 * Default key recipe constructor.
	 * @param recipeId Resource ID.
	 * @param key Key Ingredient
	 * @param cluster Cluster Ingredient
	 * @param result ItemStuck from recipe.
	 */
	public KeyPressRecipe(ResourceLocation recipeId, Ingredient key, Ingredient cluster, ItemStack result)
	{
		this.recipeId = recipeId;
		this.key = key;
		this.cluster = cluster;
		this.result = result;
	}


	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(IInventory inv, World worldIn)
	{
		return this.key.test(inv.getStackInSlot(KeyPressContainer.KEY_SLOT)) &&
			this.cluster.test(inv.getStackInSlot(KeyPressContainer.CLUSTER_SLOT));
	}


	/**
	 * Returns an Item that is the result of this recipe
	 */
	public ItemStack getCraftingResult(IInventory inv)
	{
		ItemStack itemstack = this.result.copy();
		CompoundNBT compoundnbt = inv.getStackInSlot(KeyPressContainer.RESULT_SLOT).getTag();
		if (compoundnbt != null)
		{
			itemstack.setTag(compoundnbt.copy());
		}

		return itemstack;
	}


	/**
	 * Used to determine if this recipe can fit in a grid of the given width/height
	 */
	public boolean canFit(int width, int height)
	{
		return width * height >= 2;
	}


	/**
	 * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
	 * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
	 */
	public ItemStack getRecipeOutput()
	{
		return this.result;
	}


	/**
	 * This method returns if given item is valid for cluster ingredient.
	 * @param itemStack ItemStack that must be checked.
	 * @return {@code true} if given item is valid for cluster recipe, {@code false} otherwise.
	 */
	public boolean isValidCluster(ItemStack itemStack)
	{
		return this.cluster.test(itemStack);
	}

	/**
	 * This method returns if given item is valid for key ingredient.
	 * @param itemStack ItemStack that must be checked.
	 * @return {@code true} if given item is valid for key recipe, {@code false} otherwise.
	 */
	public boolean isValidBlankKey(ItemStack itemStack)
	{
		return this.key.test(itemStack);
	}


	/**
	 * This method returns icon for recipe.
	 * @return KeyPress icon.
	 */
	public ItemStack getIcon()
	{
		return new ItemStack(ModBlocks.KEY_PRESS);
	}


	/**
	 * This method returns recipe Id.
	 */
	public ResourceLocation getId()
	{
		return this.recipeId;
	}


	/**
	 * This method returns serializer.
	 */
	public IRecipeSerializer<?> getSerializer()
	{
		return KeyPressRecipe.SERIALIZER;
	}


	/**
	 * This method returns recipe type.
	 */
	public IRecipeType<?> getType()
	{
		return SERIALIZER.getRecipeType();
	}


	/**
	 * This method returns recipes ingredient list.
	 */
	@Override
	public NonNullList<Ingredient> getIngredients()
	{
		NonNullList<Ingredient> list = NonNullList.create();
		list.add(this.key);
		list.add(this.cluster);
		return list;
	}


	// ---------------------------------------------------------------------
	// Section: Serializer
	// ---------------------------------------------------------------------


	/**
	 * This class manages recipes serialization and deserialization.
	 */
	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<KeyPressRecipe>
	{

		// This registry name is what people will specify in their json files.
		Serializer()
		{
			this.setRegistryName(ModRecipes.KEY_PRESS_RECIPE.toString());
		}


		/**
		 * This method reads KeyPressRecipe from given json object.
		 */
		public KeyPressRecipe read(ResourceLocation recipeId, JsonObject json)
		{
			Ingredient key = Ingredient.deserialize(JSONUtils.getJsonObject(json, "key"));
			Ingredient cluster = Ingredient.deserialize(JSONUtils.getJsonObject(json, "cluster"));
			ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
			return new KeyPressRecipe(recipeId, key, cluster, result);
		}


		/**
		 * This method reads KeyPressRecipe from given buffer.
		 */
		public KeyPressRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
		{
			Ingredient key = Ingredient.read(buffer);
			Ingredient cluster = Ingredient.read(buffer);
			ItemStack result = buffer.readItemStack();
			return new KeyPressRecipe(recipeId, key, cluster, result);
		}


		/**
		 * This method serializes KeyPressRecipe to given buffer.
		 */
		public void write(PacketBuffer buffer, KeyPressRecipe recipe)
		{
			recipe.key.write(buffer);
			recipe.cluster.write(buffer);
			buffer.writeItemStack(recipe.result);
		}


		public IRecipeType<?> getRecipeType()
		{
			return ModRecipes.KEY_PRESS_RECIPE;
		}
	}
}
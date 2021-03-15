package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.Vault;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.config.entry.SingleItemEntry;
import iskallia.vault.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class KeyPressRecipesConfig extends Config {

    @Expose private List<Recipe> RECIPES;

    @Override

    public String getName() {
        return "key_press_recipes";
    }

    public List<Recipe> getRecipes() {
        return RECIPES;
    }

    public Recipe getRecipeFor(Item keyItem, Item clusterItem) {
        ResourceLocation keyID = keyItem.getRegistryName();
        ResourceLocation clusterID = clusterItem.getRegistryName();

        if (keyID == null || clusterID == null)
            return null;

        for (Recipe recipe : getRecipes()) {
            if (recipe.KEY_ITEM.ITEM.equals(keyID.toString())) {
                if (recipe.CLUSTER_ITEM.ITEM.equals(clusterID.toString())) {
                    return recipe;
                }
            }
        }

        return null;
    }

    public ItemStack getResultFor(Item keyItem, Item clusterItem) {
        Recipe recipe = getRecipeFor(keyItem, clusterItem);

        if (recipe == null) return ItemStack.EMPTY;

        ResourceLocation resultID = new ResourceLocation(recipe.RESULT_ITEM.ITEM);
        Item item = ForgeRegistries.ITEMS.getValue(resultID);

        if (item == null) {
            Vault.LOGGER.warn("Invalid Key Press recipe result -> {}", recipe.RESULT_ITEM.ITEM);
            return ItemStack.EMPTY;
        }

        try {
            ItemStack resultStack = new ItemStack(item, recipe.RESULT_ITEM.AMOUNT);

            if (recipe.RESULT_ITEM.NBT != null && !recipe.RESULT_ITEM.NBT.isEmpty()) {
                resultStack.setTag(JsonToNBT.getTagFromJson(recipe.RESULT_ITEM.NBT));
            }

            return resultStack;

        } catch (CommandSyntaxException e) {
            Vault.LOGGER.warn("Malformed NBT at Key Press recipe result -> {} NBT: {}",
                    recipe.RESULT_ITEM.ITEM, recipe.RESULT_ITEM.NBT);
            return ItemStack.EMPTY;
        }
    }

    @Override
    protected void reset() {
        this.RECIPES = new ArrayList<>();

        Recipe recipe;

        recipe = new Recipe();
        recipe.KEY_ITEM = new SingleItemEntry(ModItems.BLANK_KEY.getRegistryName().toString(), "");
        recipe.CLUSTER_ITEM = new SingleItemEntry(ModItems.SPARKLETINE_CLUSTER.getRegistryName().toString(), "");
        recipe.RESULT_ITEM = new ItemEntry(ModItems.SPARKLETINE_KEY.getRegistryName().toString(), 1, "");
        this.RECIPES.add(recipe);
    }

    public static class Recipe {
        @Expose private SingleItemEntry KEY_ITEM;
        @Expose private SingleItemEntry CLUSTER_ITEM;
        @Expose private ItemEntry RESULT_ITEM;
    }

}

//
// Created by BONNe
// Copyright - 2021
//


package iskallia.vault.hook.jei;


import iskallia.vault.Vault;
import iskallia.vault.client.gui.screen.KeyPressScreen;
import iskallia.vault.container.KeyPressContainer;
import iskallia.vault.hook.jei.category.KeyPressCategory;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModRecipes;
import iskallia.vault.recipe.KeyPressRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.SmithingTableScreen;
import net.minecraft.inventory.container.SmithingTableContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;


@JeiPlugin
public class JEIVaultHuntersPlugin implements IModPlugin
{
    private IRecipeCategory<KeyPressRecipe> keyPressCategory;


    @Override
    public ResourceLocation getPluginUid()
    {
        return Vault.id(Vault.MOD_ID);
    }


    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration)
    {
        // Do not have item subtypes yet.
    }


    @Override
    public void registerIngredients(IModIngredientRegistration registration)
    {
        // Do not have a custom ingredients yet.
    }


    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration)
    {
        // Do not have a custom vanilla extensions
    }


    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(
            keyPressCategory = new KeyPressCategory(guiHelper)
        );
    }


    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        registration.addRecipes(
            Minecraft.getInstance().world.getRecipeManager().getRecipesForType(ModRecipes.KEY_PRESS_RECIPE),
            Vault.id("key_press_recipe"));
    }


    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        registration.addRecipeClickArea(KeyPressScreen.class, 102, 48, 22, 15, Vault.id("key_press_recipe"));
    }


    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
    {
        registration.addRecipeTransferHandler(KeyPressContainer.class, Vault.id("key_press_recipe"), 0, 2, 3, 36);
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.KEY_PRESS), Vault.id("key_press_recipe"));
    }
}
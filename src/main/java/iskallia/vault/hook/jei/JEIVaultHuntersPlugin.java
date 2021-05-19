//
// Created by BONNe
// Copyright - 2021
//


package iskallia.vault.hook.jei;


import iskallia.vault.Vault;
import iskallia.vault.hook.jei.anvil.AnvilRecipeMaker;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import net.minecraft.util.ResourceLocation;


@JeiPlugin
public class JEIVaultHuntersPlugin implements IModPlugin
{
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
        // Do not have a custom categories
    }


    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        IVanillaRecipeFactory vanillaRecipeFactory = registration.getVanillaRecipeFactory();
        registration.addRecipes(AnvilRecipeMaker.getAnvilRecipes(vanillaRecipeFactory), VanillaRecipeCategoryUid.ANVIL);
    }


    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        // Do not have a custom handlers
    }


    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
    {
        // Do not have a custom transfer handlers
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        // Do not have a custom catalysts
    }
}
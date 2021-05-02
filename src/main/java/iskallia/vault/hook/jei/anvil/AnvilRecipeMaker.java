package iskallia.vault.hook.jei.anvil;


import com.google.common.collect.Lists;
import java.util.*;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.CrystalData;
import iskallia.vault.item.ItemVaultCrystal;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.item.gear.VaultGear;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.item.*;


/**
 * This class creates Vault Plugin Anvil recipes.
 */
public final class AnvilRecipeMaker
{
	/**
	 * Random number generator.
	 */
	private static final Random random = new Random(0);


	/**
	 * This method generates Vault Plugin Anvil recipes in JEI.
	 */
	public static Collection<Object> getAnvilRecipes(IVanillaRecipeFactory vanillaRecipeFactory)
	{
		Collection<Object> recipeList = Lists.newArrayList();

		List<List<ItemStack>> vaultArmorList = Lists.newArrayList();

		// Populate different armors
		vaultArmorList.add(AnvilRecipeMaker.generateRandomArmorList(ModItems.HELMET));
		vaultArmorList.add(AnvilRecipeMaker.generateRandomArmorList(ModItems.CHESTPLATE));
		vaultArmorList.add(AnvilRecipeMaker.generateRandomArmorList(ModItems.LEGGINGS));
		vaultArmorList.add(AnvilRecipeMaker.generateRandomArmorList(ModItems.BOOTS));

		ArrayList<ItemStack> vaultWeapons = Lists.newArrayList(
			new ItemStack(ModItems.SWORD),
			new ItemStack(ModItems.AXE),
			new ItemStack(ModItems.DAGGER)
		);

		ArrayList<ItemStack> omegaPog = Lists.newArrayList(new ItemStack(ModItems.OMEGA_POG));

		// Repair Vault Armor
		for (List<ItemStack> armorItemList : vaultArmorList)
		{
			List<ItemStack> inputList = new ArrayList<>(armorItemList.size());
			List<ItemStack> resultList = new ArrayList<>(armorItemList.size());

			for (ItemStack armorItem : armorItemList)
			{
				// Create result item stack.
				ItemStack input = armorItem.copy();
				input.setDamage(100);

				ItemStack result = armorItem.copy();
				ModAttributes.CURRENT_REPAIRS.create(result, 1);

				inputList.add(input);
				resultList.add(result);
			}

			// Create anvil recipe.
			recipeList.add(vanillaRecipeFactory.createAnvilRecipe(inputList, omegaPog, resultList));
		}

		// Repair Vault Weapons
		for (ItemStack weapon : vaultWeapons)
		{
			// Create item model.
			ModAttributes.GEAR_RARITY.create(weapon, VaultGear.Rarity.EPIC);
			ModAttributes.GEAR_MODEL.create(weapon, 0);
			ModAttributes.GEAR_COLOR.create(weapon, VaultGear.randomBaseColor(new Random(0)));
			ModAttributes.DURABILITY.create(weapon, 100);
			ModAttributes.MAX_REPAIRS.create(weapon, 1);
			weapon.setDamage(100);

			// Create result item stack.
			ItemStack result = weapon.copy();
			ModAttributes.CURRENT_REPAIRS.create(result, 1);
			result.setDamage(0);

			// Create anvil recipe.
			recipeList.add(vanillaRecipeFactory.createAnvilRecipe(weapon, omegaPog, Lists.newArrayList(result)));
		}

		// Etching Recipes
		List<ItemStack> randomEtchingList = AnvilRecipeMaker.generateRandomEtchingList();

		for (List<ItemStack> armorItemList : vaultArmorList)
		{
			List<ItemStack> resultList = new ArrayList<>(armorItemList.size());

			for (ItemStack armorItem : armorItemList)
			{
				for (ItemStack etchingSet : randomEtchingList)
				{
					ItemStack etchingResult = armorItem.copy();

					// Apply etching
					VaultGear.Set set =
						ModAttributes.GEAR_SET.getOrDefault(etchingSet, VaultGear.Set.NONE).getValue(etchingSet);
					ModAttributes.GEAR_SET.create(etchingResult, set);

					// Create anvil recipe
					resultList.add(etchingResult);
				}
			}

			recipeList.add(vanillaRecipeFactory.createAnvilRecipe(armorItemList, randomEtchingList, resultList));
		}

		// Artifact recipe
		List<ItemStack> artifacts = Lists.newArrayList(
			new ItemStack(ModBlocks.ARTIFACT_1),
			new ItemStack(ModBlocks.ARTIFACT_2),
			new ItemStack(ModBlocks.ARTIFACT_3),
			new ItemStack(ModBlocks.ARTIFACT_4),
			new ItemStack(ModBlocks.ARTIFACT_5),
			new ItemStack(ModBlocks.ARTIFACT_6),
			new ItemStack(ModBlocks.ARTIFACT_7),
			new ItemStack(ModBlocks.ARTIFACT_8),
			new ItemStack(ModBlocks.ARTIFACT_9),
			new ItemStack(ModBlocks.ARTIFACT_10),
			new ItemStack(ModBlocks.ARTIFACT_11),
			new ItemStack(ModBlocks.ARTIFACT_12),
			new ItemStack(ModBlocks.ARTIFACT_13),
			new ItemStack(ModBlocks.ARTIFACT_14),
			new ItemStack(ModBlocks.ARTIFACT_15),
			new ItemStack(ModBlocks.ARTIFACT_16)
		);

		List<ItemStack> unidentifiedArtifact = Lists.newArrayList(new ItemStack(ModItems.UNIDENTIFIED_ARTIFACT));
		recipeList.add(vanillaRecipeFactory.createAnvilRecipe(artifacts, omegaPog, unidentifiedArtifact));


		// Unlock crystal
		List<ItemStack> voidCore = Lists.newArrayList(new ItemStack(ModItems.VOID_CORE));

		List<ItemStack> vaultCrystalList = Lists.newArrayList(
			new ItemStack(ModItems.VAULT_CRYSTAL_NORMAL),
			new ItemStack(ModItems.VAULT_CRYSTAL_RARE),
			new ItemStack(ModItems.VAULT_CRYSTAL_EPIC),
			new ItemStack(ModItems.VAULT_CRYSTAL_OMEGA)
		);

		for (ItemStack crystal : vaultCrystalList)
		{
			ItemStack result = crystal.copy();
			ItemVaultCrystal.getData(result).addModifier("Locked", CrystalData.Modifier.Operation.REMOVE, 1.0F);
			recipeList.add(vanillaRecipeFactory.createAnvilRecipe(crystal, voidCore, Lists.newArrayList(result)));
		}

		return recipeList;
	}


	/**
	 * This method generates and returns random list of given vault gear item with different rarities and
	 * types.
	 */
	private static List<ItemStack> generateRandomArmorList(VaultArmorItem gear)
	{
		List<ItemStack> returnList = new ArrayList<>();

// 		JEI sometimes do not handle item main ingredient changing.
//		for (int i = 0; i < gear.getModelsFor(VaultGear.Rarity.OMEGA); i++)
		{
			ItemStack itemStack = new ItemStack(gear);

			ModAttributes.GEAR_STATE.create(itemStack, VaultGear.State.IDENTIFIED);
			ModAttributes.GEAR_RARITY.create(itemStack, VaultGear.Rarity.EPIC);
				//randomEnum(VaultGear.Rarity.values()));
			ModAttributes.GEAR_MODEL.create(itemStack, 0);

			ModAttributes.DURABILITY.create(itemStack, 100);
			ModAttributes.MAX_REPAIRS.create(itemStack, random.nextInt(2) + 1);

			returnList.add(itemStack);
		}

		return returnList;
	}


	/**
	 * This method generates and returns random list of etching sets.
	 */
	private static List<ItemStack> generateRandomEtchingList()
	{
		List<ItemStack> returnList = new ArrayList<>();

		Arrays.stream(VaultGear.Set.values()).forEach(set -> {
			ItemStack itemStack = new ItemStack(ModItems.ETCHING);
			ModAttributes.GEAR_STATE.create(itemStack, VaultGear.State.IDENTIFIED);
			ModAttributes.GEAR_SET.create(itemStack, set);
			returnList.add(itemStack);
		});

		return returnList;
	}


	/**
	 * This method returns random element from the given array.
	 */
	private static <T extends Object> T randomEnum(T[] array)
	{
		return array[random.nextInt(array.length)];
	}
}

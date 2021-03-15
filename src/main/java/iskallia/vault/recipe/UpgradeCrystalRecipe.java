package iskallia.vault.recipe;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModRecipes;
import iskallia.vault.item.ItemVaultCrystal;
import iskallia.vault.util.VaultRarity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class UpgradeCrystalRecipe extends SpecialRecipe {

	public UpgradeCrystalRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingInventory inv, World world) {
		VaultRarity rarity = null;
		boolean hasSpark = false;
		int count = 0;

		for(int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);

			if(stack.getItem() instanceof ItemVaultCrystal) {
				if(rarity != null && ((ItemVaultCrystal)stack.getItem()).getRarity() != rarity) {
					return false;
				}

				rarity = ((ItemVaultCrystal)stack.getItem()).getRarity();
				count++;
			} else if(!hasSpark && stack.getItem() == ModItems.SPARK) {
				hasSpark = true;
			} else if(!stack.isEmpty()) {
				return false;
			}
		}

		int targetCount = Integer.MAX_VALUE;

		if(rarity == VaultRarity.COMMON) {
			targetCount = ModConfigs.CRYSTAL_UPGRADE.COMMON_TO_RARE;
		} else if(rarity == VaultRarity.RARE) {
			targetCount = ModConfigs.CRYSTAL_UPGRADE.RARE_TO_EPIC;
		} else if(rarity == VaultRarity.EPIC) {
			targetCount = ModConfigs.CRYSTAL_UPGRADE.EPIC_TO_OMEGA;
		}

		return rarity != null && hasSpark && count == targetCount;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		List<ItemStack> crystals = new ArrayList<>();
		VaultRarity rarity = null;

		for(int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);

			if(stack.getItem() instanceof ItemVaultCrystal) {
				rarity = ((ItemVaultCrystal)stack.getItem()).getRarity();
				crystals.add(stack);
			}
		}

		List<String> bossNames = crystals.stream()
				.filter(ItemStack::hasTag)
				.filter(stack -> stack.getTag().contains("playerBossName", Constants.NBT.TAG_STRING))
				.map(stack -> stack.getTag().getString("playerBossName"))
				.sorted(String::compareToIgnoreCase)
				.collect(Collectors.toList());

		if(!bossNames.isEmpty()) {
			return ItemVaultCrystal.getCrystalWithBoss(VaultRarity.values()[rarity.ordinal() + 1],
					bossNames.get(new Random(bossNames.hashCode()).nextInt(bossNames.size())));
		}

		return ItemVaultCrystal.getCrystal(VaultRarity.values()[rarity.ordinal() + 1]);
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= Math.min(Math.min(ModConfigs.CRYSTAL_UPGRADE.COMMON_TO_RARE, ModConfigs.CRYSTAL_UPGRADE.RARE_TO_EPIC), ModConfigs.CRYSTAL_UPGRADE.EPIC_TO_OMEGA);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.Serializer.CRAFTING_SPECIAL_UPGRADE_CRYSTAL;
	}

}

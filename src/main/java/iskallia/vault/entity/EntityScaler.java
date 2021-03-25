package iskallia.vault.entity;

import iskallia.vault.config.VaultMobsConfig;
import iskallia.vault.init.ModConfigs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EntityScaler {

	public static void scaleVault(LivingEntity entity, int level, Random random, Type type) {
		VaultMobsConfig.Level overrides = ModConfigs.VAULT_MOBS.getForLevel(level);

		for(EquipmentSlotType slot: EquipmentSlotType.values()) {
			if(slot.getType() == EquipmentSlotType.Group.HAND) {
				if(!entity.getItemBySlot(slot).isEmpty())continue;
			}

			ItemStack loot = new ItemStack(type.loot.apply(overrides, slot));

			for(int i = 0; i < type.trials.apply(overrides); i++) {
				EnchantmentHelper.enchantItem(random, loot,
						EnchantmentHelper.getEnchantmentCost(random, type.level.apply(overrides), 15, loot), true);
			}

			entity.setItemSlot(slot, loot);
		}
	}

    public enum Type {
        MOB(VaultMobsConfig.Level::getForMob, level -> level.MOB_MISC.ENCH_TRIALS, level -> level.MOB_MISC.ENCH_LEVEL),
        BOSS(VaultMobsConfig.Level::getForBoss, level -> level.BOSS_MISC.ENCH_TRIALS, level -> level.BOSS_MISC.ENCH_LEVEL);

		private final BiFunction<VaultMobsConfig.Level, EquipmentSlotType, Item> loot;
		private final Function<VaultMobsConfig.Level, Integer> trials;
		private final Function<VaultMobsConfig.Level, Integer> level;

		Type(BiFunction<VaultMobsConfig.Level, EquipmentSlotType, Item> loot,
		     Function<VaultMobsConfig.Level, Integer> trials,
		     Function<VaultMobsConfig.Level, Integer> level) {
			this.loot = loot;
			this.trials = trials;
			this.level = level;
		}
	}

}

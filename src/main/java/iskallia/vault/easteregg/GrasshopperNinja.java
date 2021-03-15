package iskallia.vault.easteregg;

import iskallia.vault.Vault;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.skill.set.PlayerSet;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class GrasshopperNinja {

    public static void achieve(ServerPlayerEntity playerEntity) {
        Advancement advancement = playerEntity.getServer().getAdvancementManager()
                .getAdvancement(Vault.id("grasshopper_ninja"));
        playerEntity.getAdvancements()
                .grantCriterion(advancement, "hopped");
    }

    public static boolean isGrasshopperShape(PlayerEntity playerEntity) {
        return PlayerSet.allMatch(playerEntity,
                (slotType, itemStack) -> ModAttributes.GEAR_MODEL.getOrDefault(itemStack, -1).getValue(itemStack) == 0
                        && isGrasshopperGreen(((VaultArmorItem) itemStack.getItem()).getColor(itemStack)),
                EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET
        );
    }

    /**
     * Masterpiece of Grandmaster Ninja Wu-wu
     */
    public static boolean isGrasshopperGreen(int color) {
        float grasshopperGreenR = 0x95 / 255f;
        float grasshopperGreenG = 0xC2 / 255f;
        float grasshopperGreenB = 0x68 / 255f;

        float red = ((color >> 16) & 0xFF) / 255f;
        float green = ((color >> 8) & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;

        float dr = red - grasshopperGreenR;
        float dg = green - grasshopperGreenG;
        float db = blue - grasshopperGreenB;

        float distance = (float) (Math.sqrt(dr * dr + dg * dg + db * db) / 1.73205080757);
        return distance < 0.35;
    }

}

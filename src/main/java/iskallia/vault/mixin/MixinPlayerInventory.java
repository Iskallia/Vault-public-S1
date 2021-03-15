package iskallia.vault.mixin;

import iskallia.vault.Vault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerInventory.class)
public class MixinPlayerInventory {

    @Shadow @Final public PlayerEntity player;

    @ModifyArg(method = "func_234563_a_", index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damageItem(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"))
    public int limitMaxArmorDamage(int damageAmount) {
        if(this.player.world.getDimensionKey() == Vault.VAULT_KEY) {
            return Math.min(damageAmount, 5); // Allow maximum of 5 armor damage
        }

        return damageAmount;
    }

}

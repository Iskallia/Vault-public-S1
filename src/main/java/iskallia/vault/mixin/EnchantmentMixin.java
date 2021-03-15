package iskallia.vault.mixin;

import iskallia.vault.item.gear.VaultGear;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

	@Inject(method = "canApply", at = @At("HEAD"), cancellable = true)
	private void canApply(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
		if(stack.getItem() instanceof VaultGear && !((VaultGear<?>)stack.getItem()).canApply(stack, (Enchantment)(Object)this)) {
			ci.setReturnValue(false);
		}
	}

}

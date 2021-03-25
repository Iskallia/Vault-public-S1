package iskallia.vault.mixin;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModifiableAttributeInstance.class)
public abstract class MixinAttributeInstance {

	@Redirect(method = "calculateValue", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/attributes/Attribute;sanitizeValue(D)D"))
	private double calculateValue(Attribute attribute, double value) {
		if(attribute == Attributes.ARMOR) {
			return MathHelper.clamp(value, 0, 100);
		}

		return attribute.sanitizeValue(value);
	}

}

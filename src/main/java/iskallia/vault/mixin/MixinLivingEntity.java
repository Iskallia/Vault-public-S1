package iskallia.vault.mixin;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.skill.set.GolemSet;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.CarapaceTalent;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

	public MixinLivingEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Shadow public abstract EffectInstance getActivePotionEffect(Effect potionIn);

	@Shadow @Nullable public abstract ModifiableAttributeInstance getAttribute(Attribute attribute);

	@Shadow public abstract boolean isPotionActive(Effect potionIn);

	@Redirect(method = "registerAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/attributes/AttributeModifierMap;createMutableAttribute()Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;"))
	private static AttributeModifierMap.MutableAttribute registerAttributes() {
		return AttributeModifierMap.createMutableAttribute()
				.createMutableAttribute(ModAttributes.CRIT_CHANCE)
				.createMutableAttribute(ModAttributes.CRIT_MULTIPLIER)
				.createMutableAttribute(ModAttributes.TP_CHANCE)
				.createMutableAttribute(ModAttributes.TP_INDIRECT_CHANCE)
				.createMutableAttribute(ModAttributes.TP_RANGE)
				.createMutableAttribute(ModAttributes.POTION_RESISTANCE);
	}

	@Redirect(method = "applyPotionDamageCalculations", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"))
	protected float applyPotionDamageCalculations(float a, float b) {
		if(!this.world.isRemote && (LivingEntity)(Object)this instanceof PlayerEntity) {
			int resistance = this.isPotionActive(Effects.RESISTANCE) ? 0 : this.getActivePotionEffect(Effects.RESISTANCE).getAmplifier() + 1;
			float damageCancel = (resistance * 5) / 25.0F;
			float damage = a * 25 / (25 - resistance * 5);

			ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;

			TalentTree abilities = PlayerTalentsData.get(player.getServerWorld()).getTalents(player);

			for(TalentNode<?> node : abilities.getNodes()) {
				if(!(node.getTalent() instanceof CarapaceTalent))continue;
				CarapaceTalent talent = (CarapaceTalent)node.getTalent();
				damageCancel += talent.getResistanceBonus();
			}

			SetTree sets = PlayerSetsData.get(player.getServerWorld()).getSets(player);

			for(SetNode<?> node : sets.getNodes()) {
				if(!(node.getSet() instanceof GolemSet))continue;
				GolemSet set = (GolemSet)node.getSet();
				damageCancel += set.getResistanceBonus();
			}

			return Math.max(damage - damage * damageCancel, 0);
		}

		return Math.max(a, b);
	}

	@Inject(method = "addPotionEffect", at = @At("HEAD"), cancellable = true)
	private void addPotionEffect(EffectInstance effect, CallbackInfoReturnable<Boolean> ci) {
		ModifiableAttributeInstance attribute = this.getAttribute(ModAttributes.POTION_RESISTANCE);
		if(attribute == null)return;
		if(this.rand.nextDouble() >= attribute.getValue())return;
		ci.setReturnValue(false);
	}

}

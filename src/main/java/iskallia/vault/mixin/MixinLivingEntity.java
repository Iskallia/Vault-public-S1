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

	@Shadow public abstract EffectInstance getEffect(Effect potionIn);

	@Shadow @Nullable public abstract ModifiableAttributeInstance getAttribute(Attribute attribute);

	@Shadow public abstract boolean hasEffect(Effect potionIn);

	@Redirect(method = "createLivingAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/attributes/AttributeModifierMap;builder()Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;"))
	private static AttributeModifierMap.MutableAttribute createLivingAttributes() {
		return AttributeModifierMap.builder()
				.add(ModAttributes.CRIT_CHANCE)
				.add(ModAttributes.CRIT_MULTIPLIER)
				.add(ModAttributes.TP_CHANCE)
				.add(ModAttributes.TP_INDIRECT_CHANCE)
				.add(ModAttributes.TP_RANGE)
				.add(ModAttributes.POTION_RESISTANCE);
	}

	@Redirect(method = "getDamageAfterMagicAbsorb", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"))
	protected float getDamageAfterMagicAbsorb(float a, float b) {
		if(!this.level.isClientSide && (LivingEntity)(Object)this instanceof PlayerEntity) {
			int resistance = this.hasEffect(Effects.DAMAGE_RESISTANCE) ? 0 : this.getEffect(Effects.DAMAGE_RESISTANCE).getAmplifier() + 1;
			float damageCancel = (resistance * 5) / 25.0F;
			float damage = a * 25 / (25 - resistance * 5);

			ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;

			TalentTree abilities = PlayerTalentsData.get(player.getLevel()).getTalents(player);

			for(TalentNode<?> node : abilities.getNodes()) {
				if(!(node.getTalent() instanceof CarapaceTalent))continue;
				CarapaceTalent talent = (CarapaceTalent)node.getTalent();
				damageCancel += talent.getResistanceBonus();
			}

			SetTree sets = PlayerSetsData.get(player.getLevel()).getSets(player);

			for(SetNode<?> node : sets.getNodes()) {
				if(!(node.getSet() instanceof GolemSet))continue;
				GolemSet set = (GolemSet)node.getSet();
				damageCancel += set.getResistanceBonus();
			}

			return Math.max(damage - damage * damageCancel, 0);
		}

		return Math.max(a, b);
	}

	@Inject(method = "addEffect", at = @At("HEAD"), cancellable = true)
	private void addEffect(EffectInstance effect, CallbackInfoReturnable<Boolean> ci) {
		ModifiableAttributeInstance attribute = this.getAttribute(ModAttributes.POTION_RESISTANCE);
		if(attribute == null)return;
		if(this.random.nextDouble() >= attribute.getValue())return;
		ci.setReturnValue(false);
	}

}

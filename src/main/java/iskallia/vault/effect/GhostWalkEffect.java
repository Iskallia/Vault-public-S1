package iskallia.vault.effect;

import iskallia.vault.init.ModConfigs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GhostWalkEffect extends Effect {

    public AttributeModifier[] attributeModifiers;

    public GhostWalkEffect(EffectType typeIn, int liquidColorIn, ResourceLocation id) {
        super(typeIn, liquidColorIn);
        setRegistryName(id);

    }

    private void initializeAttributeModifiers() {
        this.attributeModifiers = new AttributeModifier[ModConfigs.ABILITIES.GHOST_WALK.getMaxLevel()];
        for (int i = 0; i < this.attributeModifiers.length; i++) {
            this.attributeModifiers[i] = new AttributeModifier(this.getRegistryName().toString(),
                    (i + 1) * 0.2f, AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyAttributesModifiersToEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        ModifiableAttributeInstance movementSpeed = entityLivingBaseIn.getAttribute(Attributes.MOVEMENT_SPEED);
        initializeAttributeModifiers();
        if (movementSpeed != null) {
            AttributeModifier attributeModifier = this.attributeModifiers[MathHelper.clamp(amplifier + 1, 0, this.attributeModifiers.length - 1)];
            movementSpeed.applyNonPersistentModifier(attributeModifier);
        }

        entityLivingBaseIn.setInvulnerable(true);

        super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }

    @Override
    public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        ModifiableAttributeInstance movementSpeed = entityLivingBaseIn.getAttribute(Attributes.MOVEMENT_SPEED);

        if (movementSpeed != null) {
            AttributeModifier attributeModifier = this.attributeModifiers[MathHelper.clamp(amplifier + 1, 0, this.attributeModifiers.length - 1)];
            movementSpeed.removeModifier(attributeModifier.getID());
        }

        entityLivingBaseIn.setInvulnerable(false);

        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }

}

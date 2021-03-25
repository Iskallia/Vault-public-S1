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
        this.attributeModifiers = new AttributeModifier[ModConfigs.ABILITIES.RAMPAGE.getMaxLevel()];
        for (int i = 0; i < this.attributeModifiers.length; i++) {
            this.attributeModifiers[i] = new AttributeModifier(this.getRegistryName().toString(),
                    (i + 1) * 0.2f, AttributeModifier.Operation.ADDITION);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void addAttributeModifiers(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        ModifiableAttributeInstance movementSpeed = entityLivingBaseIn.getAttribute(Attributes.MOVEMENT_SPEED);
        initializeAttributeModifiers();
        if (movementSpeed != null) {
            AttributeModifier attributeModifier = this.attributeModifiers[MathHelper.clamp(amplifier + 1, 0, this.attributeModifiers.length - 1)];
            movementSpeed.addTransientModifier(attributeModifier);
        }

        entityLivingBaseIn.setInvulnerable(true);

        super.addAttributeModifiers(entityLivingBaseIn, attributeMapIn, amplifier);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        ModifiableAttributeInstance movementSpeed = entityLivingBaseIn.getAttribute(Attributes.MOVEMENT_SPEED);

        if (movementSpeed != null) {
            AttributeModifier attributeModifier = this.attributeModifiers[MathHelper.clamp(amplifier + 1, 0, this.attributeModifiers.length - 1)];
            movementSpeed.removeModifier(attributeModifier.getId());
        }

        entityLivingBaseIn.setInvulnerable(false);

        super.removeAttributeModifiers(entityLivingBaseIn, attributeMapIn, amplifier);
    }

}

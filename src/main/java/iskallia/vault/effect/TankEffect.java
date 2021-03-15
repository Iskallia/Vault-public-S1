package iskallia.vault.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

public class TankEffect extends Effect {
//
//    public AttributeModifier[] attributeModifiers;

    public TankEffect(EffectType typeIn, int liquidColorIn, ResourceLocation id) {
        super(typeIn, liquidColorIn);
        setRegistryName(id);
//
//        this.attributeModifiers = new AttributeModifier[5];
//
//        for (int i = 0; i < this.attributeModifiers.length; i++) {
//            this.attributeModifiers[i] = new AttributeModifier(id.toString(),
//                    (i + 1) * 0.2f, AttributeModifier.Operation.ADDITION);
//        }

    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

//    @Override
//    public void applyAttributesModifiersToEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
//        ModifiableAttributeInstance movementSpeed = entityLivingBaseIn.getAttribute(Attributes.MOVEMENT_SPEED);
//
//        if (movementSpeed != null) {
//            float decrease = (50f - ((float) amplifier * 5f)) * .01f;
//            this.attributeModifiers[amplifier] = new AttributeModifier(this.getRegistryName().toString(), -decrease, AttributeModifier.Operation.MULTIPLY_TOTAL);
//            movementSpeed.applyNonPersistentModifier(this.attributeModifiers[amplifier]);
//        }
//
//        super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
//    }
//
//    @Override
//    public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
//        ModifiableAttributeInstance movementSpeed = entityLivingBaseIn.getAttribute(Attributes.MOVEMENT_SPEED);
//
//        if (movementSpeed != null) {
//            movementSpeed.removeModifier(attributeModifiers[amplifier].getID());
//        }
//
//        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
//    }



}

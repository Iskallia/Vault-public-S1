package iskallia.vault.item.gear.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class SamuraiArmorModel<T extends LivingEntity> extends VaultGearModel<T> {

    public SamuraiArmorModel(float modelSize, EquipmentSlotType slotType) {
        super(modelSize, slotType);

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        Head.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
        Head.setTextureOffset(36, 3).addBox(-1.5F, -10.0F, -5.0F, 3.0F, 1.0F, 0.0F, 0.0F, false);
        Head.setTextureOffset(36, 3).addBox(-2.5F, -11.0F, -5.0F, 2.0F, 1.0F, 0.0F, 0.0F, false);
        Head.setTextureOffset(37, 4).addBox(0.5F, -11.0F, -5.0F, 2.0F, 1.0F, 0.0F, 0.0F, false);
        Head.setTextureOffset(38, 6).addBox(2.5F, -12.0F, -5.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        Head.setTextureOffset(40, 6).addBox(-3.5F, -12.0F, -5.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 0.0F, 0.0F);
        Body.setTextureOffset(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
        Body.setTextureOffset(56, 0).addBox(-1.5F, 1.0F, -3.5F, 3.0F, 4.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(56, 0).addBox(-1.5F, 1.0F, 2.5F, 3.0F, 4.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(56, 0).addBox(-2.5F, 0.0F, -3.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(56, 0).addBox(-2.5F, 0.0F, 2.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(56, 0).addBox(1.5F, 0.0F, -3.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(56, 0).addBox(1.5F, 0.0F, 2.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(56, 0).addBox(2.5F, -1.0F, -3.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(56, 0).addBox(2.5F, -1.0F, 2.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(56, 0).addBox(-3.5F, -1.0F, -3.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(56, 0).addBox(-3.5F, -1.0F, 2.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(56, 0).addBox(-4.5F, -1.0F, -3.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(56, 0).addBox(-4.5F, -1.0F, 2.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(56, 0).addBox(3.5F, -1.0F, -3.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(56, 0).addBox(3.5F, -1.0F, 2.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        RightArm = new ModelRenderer(this);
        RightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        RightArm.setTextureOffset(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);

        ModelRenderer shoulderpad_r1 = new ModelRenderer(this);
        shoulderpad_r1.setRotationPoint(-1.5F, -2.75F, -0.5F);
        RightArm.addChild(shoulderpad_r1);
        setRotationAngle(shoulderpad_r1, 0.0F, 0.0F, 0.3927F);
        shoulderpad_r1.setTextureOffset(36, 7).addBox(-3.0F, -1.0F, -3.0F, 5.0F, 2.0F, 7.0F, 0.0F, false);

        LeftArm = new ModelRenderer(this);
        LeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        LeftArm.setTextureOffset(40, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, true);

        ModelRenderer shoulderpad_r2 = new ModelRenderer(this);
        shoulderpad_r2.setRotationPoint(2.0381F, -2.9413F, 0.0F);
        LeftArm.addChild(shoulderpad_r2);
        setRotationAngle(shoulderpad_r2, 0.0F, 0.0F, -0.3491F);
        shoulderpad_r2.setTextureOffset(36, 7).addBox(-2.5F, -1.0F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, true);

        RightLeg = new ModelRenderer(this);
        RightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        RightLeg.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        LeftLeg.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, true);

        Belt = new ModelRenderer(this);
        Belt.setRotationPoint(0.0F, 0.0F, 0.0F);
        Belt.setTextureOffset(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);

        RightBoot = new ModelRenderer(this);
        RightBoot.setRotationPoint(-1.9F, 12.0F, 0.0F);
        RightBoot.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);

        LeftBoot = new ModelRenderer(this);
        LeftBoot.setRotationPoint(1.9F, 12.0F, 0.0F);
        LeftBoot.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, true);
    }

}

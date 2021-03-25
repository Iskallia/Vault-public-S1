package iskallia.vault.item.gear.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class Plated4ArmorModel<T extends LivingEntity> extends VaultGearModel<T> {

    public Plated4ArmorModel(float modelSize, EquipmentSlotType slotType) {
        super(modelSize, slotType);
        textureWidth = 64;
        textureHeight = isLayer2() ? 32 : 64;

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        Head.setTextureOffset(0, 0).addBox(-4.0F, -7.0F, -4.25F, 8.0F, 8.0F, 8.0F, 1.0F, false);
        Head.setTextureOffset(20, 16).addBox(-6.0F, -4.0F, -5.0F, 1.0F, 5.0F, 6.0F, 0.0F, false);
        Head.setTextureOffset(20, 16).addBox(5.0F, -4.0F, -5.0F, 1.0F, 5.0F, 6.0F, 0.0F, false);
        Head.setTextureOffset(32, 5).addBox(-6.0F, -2.0F, -5.75F, 3.0F, 2.0F, 1.0F, 0.0F, false);
        Head.setTextureOffset(32, 5).addBox(3.0F, -2.0F, -5.75F, 3.0F, 2.0F, 1.0F, 0.0F, false);
        Head.setTextureOffset(0, 0).addBox(-3.0F, -9.0F, -4.5F, 6.0F, 1.0F, 9.0F, 0.0F, false);
        Head.setTextureOffset(0, 0).addBox(-5.5F, -7.0F, -2.5F, 1.0F, 5.0F, 6.0F, 0.0F, false);
        Head.setTextureOffset(0, 0).addBox(4.5F, -7.0F, -2.5F, 1.0F, 5.0F, 6.0F, 0.0F, true);
        Head.setTextureOffset(3, 1).addBox(-2.0F, -10.0F, -3.5F, 4.0F, 1.0F, 7.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setRotationPoint(0.0F, -5.5F, -5.25F);
        Head.addChild(cube_r1);
        setRotationAngle(cube_r1, -0.7418F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(28, 0).addBox(-3.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, false);

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 0.0F, 0.0F);
        Body.setTextureOffset(0, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, true);
        Body.setTextureOffset(0, 43).addBox(-6.0F, -2.0F, -4.0F, 12.0F, 13.0F, 8.0F, 0.0F, false);
        Body.setTextureOffset(38, 9).addBox(-5.0F, -1.0F, -6.0F, 10.0F, 8.0F, 3.0F, 0.0F, false);
        Body.setTextureOffset(13, 51).addBox(-5.0F, -1.0F, 3.0F, 10.0F, 8.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(40, 43).addBox(-4.5F, 7.75F, 2.0F, 9.0F, 1.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setRotationPoint(0.0F, 1.875F, -5.875F);
        Body.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.3927F, 0.0F, 0.0F);
        cube_r2.setTextureOffset(40, 43).addBox(-4.75F, -1.875F, -0.875F, 9.0F, 3.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r3 = new ModelRenderer(this);
        cube_r3.setRotationPoint(0.75F, 8.875F, -3.375F);
        Body.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.6545F, 0.0F, 0.0F);
        cube_r3.setTextureOffset(40, 43).addBox(-4.5F, -0.875F, -1.375F, 7.0F, 1.0F, 2.0F, 0.0F, false);

        RightArm = new ModelRenderer(this);
        RightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        RightArm.setTextureOffset(0, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
        RightArm.setTextureOffset(36, 32).addBox(-5.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, true);
        RightArm.setTextureOffset(0, 55).addBox(-6.25F, -6.25F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, true);
        RightArm.setTextureOffset(0, 55).addBox(-6.25F, -6.25F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, true);
        RightArm.setTextureOffset(20, 52).addBox(-5.0F, 5.0F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, true);

        LeftArm = new ModelRenderer(this);
        LeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        LeftArm.setTextureOffset(0, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, true);
        LeftArm.setTextureOffset(20, 52).addBox(-0.25F, 5.0F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, false);
        LeftArm.setTextureOffset(36, 32).addBox(-2.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, false);
        LeftArm.setTextureOffset(0, 55).addBox(1.25F, -6.25F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, false);

        RightBoot = new ModelRenderer(this);
        RightBoot.setRotationPoint(-1.9F, 12.0F, 0.0F);
        RightBoot.setTextureOffset(48, 57).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, false);

        LeftBoot = new ModelRenderer(this);
        LeftBoot.setRotationPoint(1.9F, 12.0F, 0.0F);
        LeftBoot.setTextureOffset(48, 57).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, true);

        Belt = new ModelRenderer(this);
        Belt.setRotationPoint(0.0F, 0.0F, 0.0F);
        Belt.setTextureOffset(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);

        RightLeg = new ModelRenderer(this);
        RightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        RightLeg.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
        RightLeg.setTextureOffset(0, 0).addBox(-3.0F, 3.0F, -3.0F, 3.0F, 4.0F, 6.0F, 0.0F, false);

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        LeftLeg.setTextureOffset(0, 0).addBox(-0.05F, 3.0F, -3.0F, 3.0F, 4.0F, 6.0F, 0.0F, true);
        LeftLeg.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, true);
    }

}

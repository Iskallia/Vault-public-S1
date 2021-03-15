package iskallia.vault.item.gear.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class Plated1ArmorModel<T extends LivingEntity> extends VaultGearModel<T> {

    public Plated1ArmorModel(float modelSize, EquipmentSlotType slotType) {
        super(modelSize, slotType);
        textureWidth = 64;
        textureHeight = isLayer2() ? 32 : 64;

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        Head.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.25F, 8.0F, 8.0F, 8.0F, 1.0F, false);
        Head.setTextureOffset(38, 8).addBox(-1.0F, -10.0F, -6.0F, 2.0F, 6.0F, 1.0F, 0.0F, false);
        Head.setTextureOffset(38, 20).addBox(-1.0F, -10.0F, -5.0F, 2.0F, 1.0F, 10.0F, 0.0F, false);
        Head.setTextureOffset(0, 0).addBox(-0.4F, -11.0F, 3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Head.setTextureOffset(0, 0).addBox(-0.4F, -11.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Head.setTextureOffset(0, 0).addBox(-0.4F, -11.0F, -3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Head.setTextureOffset(0, 0).addBox(-5.9F, -10.25F, -3.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
        Head.setTextureOffset(0, 0).addBox(5.1F, -10.25F, -3.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
        Head.setTextureOffset(54, 4).addBox(-1.0F, -10.0F, 5.0F, 2.0F, 6.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setRotationPoint(4.5F, -8.0F, -5.5F);
        Head.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.0F, 1.2654F);
        cube_r1.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 10.0F, 0.0F, false);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setRotationPoint(-4.5F, -8.0F, -5.5F);
        Head.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.0F, 0.3491F);
        cube_r2.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 10.0F, 0.0F, false);

        ModelRenderer cube_r3 = new ModelRenderer(this);
        cube_r3.setRotationPoint(5.6F, -8.0F, 2.0F);
        Head.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.0F, 0.0F, 0.5236F);
        cube_r3.setTextureOffset(0, 0).addBox(-0.5F, -3.25F, -1.0F, 1.0F, 5.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r4 = new ModelRenderer(this);
        cube_r4.setRotationPoint(-5.4F, -8.0F, 2.0F);
        Head.addChild(cube_r4);
        setRotationAngle(cube_r4, 0.0F, 0.0F, -0.5672F);
        cube_r4.setTextureOffset(0, 0).addBox(-0.5F, -3.25F, -1.0F, 1.0F, 5.0F, 2.0F, 0.0F, false);

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 0.0F, 0.0F);
        Body.setTextureOffset(40, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, true);
        Body.setTextureOffset(0, 50).addBox(-6.0F, -2.0F, -4.0F, 12.0F, 6.0F, 8.0F, 0.0F, false);
        Body.setTextureOffset(0, 26).addBox(1.0F, -1.0F, -5.0F, 4.0F, 5.0F, 1.0F, 0.0F, true);
        Body.setTextureOffset(0, 26).addBox(-5.0F, -1.0F, -5.0F, 4.0F, 5.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(0, 28).addBox(-1.0F, -1.0F, -5.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(0, 30).addBox(-1.0F, 2.0F, -5.0F, 0.0F, 1.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(0, 30).addBox(0.25F, 2.0F, -5.0F, 0.0F, 1.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(40, 21).addBox(-6.0F, 4.0F, -4.0F, 4.0F, 3.0F, 8.0F, 0.0F, false);
        Body.setTextureOffset(14, 15).addBox(-5.0F, 5.0F, -4.5F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        Body.setTextureOffset(14, 15).addBox(-5.0F, 7.0F, -4.5F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        Body.setTextureOffset(14, 15).addBox(4.0F, 5.0F, -4.5F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        Body.setTextureOffset(13, 15).addBox(4.0F, 7.0F, -4.5F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        Body.setTextureOffset(44, 23).addBox(-6.0F, 7.0F, -4.0F, 3.0F, 2.0F, 7.0F, 0.0F, false);
        Body.setTextureOffset(0, 24).addBox(-3.0F, 7.0F, -3.5F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        Body.setTextureOffset(0, 24).addBox(2.0F, 7.0F, -3.5F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        Body.setTextureOffset(0, 24).addBox(-2.0F, 6.0F, -3.5F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        Body.setTextureOffset(0, 24).addBox(1.0F, 6.0F, -3.5F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        Body.setTextureOffset(0, 24).addBox(4.0F, 9.0F, -3.5F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        Body.setTextureOffset(0, 24).addBox(-5.0F, 9.0F, -3.5F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        Body.setTextureOffset(0, 24).addBox(-1.0F, 5.0F, -3.5F, 2.0F, 1.0F, 7.0F, 0.0F, false);
        Body.setTextureOffset(44, 23).addBox(3.0F, 7.0F, -4.0F, 3.0F, 2.0F, 7.0F, 0.0F, true);
        Body.setTextureOffset(40, 21).addBox(2.0F, 4.0F, -4.0F, 4.0F, 3.0F, 8.0F, 0.0F, true);
        Body.setTextureOffset(0, 29).addBox(-2.0F, 4.0F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(0, 29).addBox(-2.0F, 4.0F, 3.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(0, 30).addBox(-1.0F, 4.0F, -4.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(0, 30).addBox(-1.0F, 4.0F, 3.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(0, 29).addBox(1.0F, 4.0F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(0, 29).addBox(1.0F, 4.0F, 3.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        RightArm = new ModelRenderer(this);
        RightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        RightArm.setTextureOffset(48, 6).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
        RightArm.setTextureOffset(9, 20).addBox(-5.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, true);
        RightArm.setTextureOffset(9, 20).addBox(-5.0F, 3.0F, -3.5F, 5.0F, 5.0F, 7.0F, 0.0F, true);
        RightArm.setTextureOffset(44, 11).addBox(-4.0F, -5.0F, -3.0F, 4.0F, 1.0F, 6.0F, 0.0F, true);

        LeftArm = new ModelRenderer(this);
        LeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        LeftArm.setTextureOffset(48, 6).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
        LeftArm.setTextureOffset(9, 20).addBox(-0.25F, 3.0F, -3.5F, 5.0F, 5.0F, 7.0F, 0.0F, true);
        LeftArm.setTextureOffset(9, 20).addBox(-2.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, true);
        LeftArm.setTextureOffset(44, 11).addBox(0.0F, -5.0F, -3.0F, 4.0F, 1.0F, 6.0F, 0.0F, false);

        RightBoot = new ModelRenderer(this);
        RightBoot.setRotationPoint(-1.9F, 12.0F, 0.0F);
        RightBoot.setTextureOffset(48, 25).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, false);

        LeftBoot = new ModelRenderer(this);
        LeftBoot.setRotationPoint(1.9F, 12.0F, 0.0F);
        LeftBoot.setTextureOffset(48, 25).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, true);

        Belt = new ModelRenderer(this);
        Belt.setRotationPoint(0.0F, 0.0F, 0.0F);
        Belt.setTextureOffset(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);

        RightLeg = new ModelRenderer(this);
        RightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        RightLeg.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        LeftLeg.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, true);
    }

}

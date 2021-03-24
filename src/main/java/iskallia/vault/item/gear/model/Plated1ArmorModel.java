package iskallia.vault.item.gear.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class Plated1ArmorModel<T extends LivingEntity> extends VaultGearModel<T> {

    public Plated1ArmorModel(float modelSize, EquipmentSlotType slotType) {
        super(modelSize, slotType);
        textureWidth = isLayer2() ? 64 : 128;
        textureHeight = isLayer2() ? 32 : 128;

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        Head.setTextureOffset(0, 0).addBox(-1.0F, -10.75F, -5.75F, 2.0F, 7.0F, 1.0F, 0.0F, false);
        Head.setTextureOffset(0, 29).addBox(-1.0F, -10.75F, -4.75F, 2.0F, 1.0F, 11.0F, 0.0F, false);
        Head.setTextureOffset(20, 31).addBox(-0.4F, -11.75F, 3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Head.setTextureOffset(20, 31).addBox(-0.4F, -11.75F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Head.setTextureOffset(20, 31).addBox(-0.4F, -11.75F, -3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Head.setTextureOffset(15, 26).addBox(-5.9F, -12.25F, -3.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
        Head.setTextureOffset(15, 26).addBox(5.1F, -12.25F, -3.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
        Head.setTextureOffset(21, 14).addBox(-1.0F, -9.75F, 5.25F, 2.0F, 6.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setRotationPoint(4.5F, -8.0F, -5.5F);
        Head.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.0F, 1.2654F);
        cube_r1.setTextureOffset(29, 3).addBox(-2.0F, -1.75F, -0.5F, 1.0F, 1.0F, 11.0F, 0.0F, false);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setRotationPoint(-4.5F, -8.0F, -5.5F);
        Head.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.0F, 0.3491F);
        cube_r2.setTextureOffset(29, 3).addBox(-1.75F, -2.0F, -0.5F, 1.0F, 1.0F, 11.0F, 0.0F, false);

        ModelRenderer cube_r3 = new ModelRenderer(this);
        cube_r3.setRotationPoint(5.6F, -8.0F, 2.0F);
        Head.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.0F, 0.0F, 0.5236F);
        cube_r3.setTextureOffset(0, 14).addBox(-0.5F, -4.25F, -1.0F, 1.0F, 5.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r4 = new ModelRenderer(this);
        cube_r4.setRotationPoint(-5.4F, -8.0F, 2.0F);
        Head.addChild(cube_r4);
        setRotationAngle(cube_r4, 0.0F, 0.0F, -0.5672F);
        cube_r4.setTextureOffset(0, 14).addBox(-0.5F, -4.25F, -1.0F, 1.0F, 5.0F, 2.0F, 0.0F, false);

        ModelRenderer helmet2 = new ModelRenderer(this);
        helmet2.setRotationPoint(0.0F, 0.0F, 0.0F);
        Head.addChild(helmet2);
        helmet2.setTextureOffset(28, 15).addBox(-5.5F, -9.75F, 4.5F, 11.0F, 8.0F, 1.0F, 0.0F, false);
        helmet2.setTextureOffset(48, 25).addBox(-4.5F, -9.75F, -5.5F, 9.0F, 5.0F, 1.0F, 0.0F, false);
        helmet2.setTextureOffset(32, 0).addBox(-4.5F, -4.75F, -5.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        helmet2.setTextureOffset(32, 0).addBox(-5.5F, -1.75F, -4.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        helmet2.setTextureOffset(32, 4).addBox(-5.5F, -1.75F, -3.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        helmet2.setTextureOffset(32, 4).addBox(4.5F, -1.75F, -3.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        helmet2.setTextureOffset(32, 0).addBox(4.5F, -1.75F, -4.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        helmet2.setTextureOffset(0, 34).addBox(-5.5F, -1.75F, -5.5F, 4.0F, 3.0F, 1.0F, 0.0F, false);
        helmet2.setTextureOffset(0, 34).addBox(1.5F, -1.75F, -5.5F, 4.0F, 3.0F, 1.0F, 0.0F, false);
        helmet2.setTextureOffset(32, 0).addBox(3.5F, -4.75F, -5.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        ModelRenderer Helmet6_r1 = new ModelRenderer(this);
        Helmet6_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        helmet2.addChild(Helmet6_r1);
        setRotationAngle(Helmet6_r1, -1.5708F, 0.0F, 0.0F);
        Helmet6_r1.setTextureOffset(43, 43).addBox(-4.5F, -4.5F, -9.75F, 9.0F, 9.0F, 1.0F, 0.0F, false);

        ModelRenderer Helmet4_r1 = new ModelRenderer(this);
        Helmet4_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        helmet2.addChild(Helmet4_r1);
        setRotationAngle(Helmet4_r1, 0.0F, 1.5708F, 0.0F);
        Helmet4_r1.setTextureOffset(42, 0).addBox(-4.5F, -9.75F, -5.5F, 10.0F, 8.0F, 1.0F, 0.0F, false);
        Helmet4_r1.setTextureOffset(42, 0).addBox(-4.5F, -9.75F, 4.5F, 10.0F, 8.0F, 1.0F, 0.0F, false);

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 0.0F, 0.0F);
        Body.setTextureOffset(24, 24).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
        Body.setTextureOffset(0, 0).addBox(-6.0F, -2.0F, -4.0F, 12.0F, 6.0F, 8.0F, 0.0F, false);
        Body.setTextureOffset(0, 28).addBox(1.0F, -1.0F, -5.0F, 4.0F, 5.0F, 1.0F, 0.0F, true);
        Body.setTextureOffset(0, 28).addBox(-5.0F, -1.0F, -5.0F, 4.0F, 5.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(15, 32).addBox(-1.0F, -1.0F, -5.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(40, 32).addBox(-6.0F, 4.0F, -4.0F, 4.0F, 3.0F, 8.0F, 0.0F, false);
        Body.setTextureOffset(40, 32).addBox(2.0F, 4.0F, -4.0F, 4.0F, 3.0F, 8.0F, 0.0F, true);
        Body.setTextureOffset(20, 35).addBox(-2.0F, 4.0F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(20, 35).addBox(-2.0F, 4.0F, 3.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(19, 26).addBox(-1.0F, 4.0F, -4.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(19, 26).addBox(-1.0F, 4.0F, 3.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(20, 35).addBox(1.0F, 4.0F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        Body.setTextureOffset(20, 35).addBox(1.0F, 4.0F, 3.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        ModelRenderer Innerchestplate = new ModelRenderer(this);
        Innerchestplate.setRotationPoint(0.0F, 0.0F, 0.0F);
        Body.addChild(Innerchestplate);
        Innerchestplate.setTextureOffset(0, 26).addBox(-3.0F, 11.75F, -3.25F, 6.0F, 1.0F, 1.0F, 0.0F, false);
        Innerchestplate.setTextureOffset(16, 52).addBox(-4.0F, 5.0F, -3.5F, 8.0F, 7.0F, 1.0F, 0.0F, false);
        Innerchestplate.setTextureOffset(16, 52).addBox(-4.0F, 5.0F, 2.5F, 8.0F, 7.0F, 1.0F, 0.0F, false);
        Innerchestplate.setTextureOffset(0, 26).addBox(-3.0F, 11.75F, 2.25F, 6.0F, 1.0F, 1.0F, 0.0F, false);

        RightArm = new ModelRenderer(this);
        RightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        RightArm.setTextureOffset(0, 41).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
        RightArm.setTextureOffset(0, 14).addBox(-7.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, true);
        RightArm.setTextureOffset(19, 40).addBox(-7.0F, 3.0F, -3.5F, 5.0F, 5.0F, 7.0F, 0.0F, true);
        RightArm.setTextureOffset(46, 18).addBox(-6.0F, -5.0F, -3.0F, 4.0F, 1.0F, 6.0F, 0.0F, true);

        LeftArm = new ModelRenderer(this);
        LeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        LeftArm.setTextureOffset(0, 41).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, true);
        LeftArm.setTextureOffset(46, 18).addBox(2.0F, -5.0F, -3.0F, 4.0F, 1.0F, 6.0F, 0.0F, false);
        LeftArm.setTextureOffset(0, 14).addBox(0.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, false);
        LeftArm.setTextureOffset(19, 40).addBox(1.75F, 3.0F, -3.5F, 5.0F, 5.0F, 7.0F, 0.0F, false);

        RightBoot = new ModelRenderer(this);
        RightBoot.setRotationPoint(-1.9F, 12.0F, 0.0F);
        RightBoot.setTextureOffset(52, 11).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, false);

        LeftBoot = new ModelRenderer(this);
        LeftBoot.setRotationPoint(1.9F, 12.0F, 0.0F);
        LeftBoot.setTextureOffset(52, 11).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, true);

        Belt = new ModelRenderer(this);
        Belt.setRotationPoint(0.0F, 0.0F, 0.0F);
        Belt.setTextureOffset(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);

        RightLeg = new ModelRenderer(this);
        RightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        RightLeg.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
        RightLeg.setTextureOffset(19, 1).addBox(-3.25F, -2.0F, -3.0F, 2.0F, 6.0F, 6.0F, 0.0F, false);
        RightLeg.setTextureOffset(0, 0).addBox(-3.25F, 4.0F, -2.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        LeftLeg.setTextureOffset(0, 0).addBox(1.2F, 4.0F, -2.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
        LeftLeg.setTextureOffset(0, 0).addBox(1.2F, -2.0F, -3.0F, 2.0F, 6.0F, 6.0F, 0.0F, false);
        LeftLeg.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, true);
    }

}

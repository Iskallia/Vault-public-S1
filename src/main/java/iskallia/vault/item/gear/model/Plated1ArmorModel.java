package iskallia.vault.item.gear.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class Plated1ArmorModel<T extends LivingEntity> extends VaultGearModel<T> {

    public Plated1ArmorModel(float modelSize, EquipmentSlotType slotType) {
        super(modelSize, slotType);
        texWidth = isLayer2() ? 64 : 128;
        texHeight = isLayer2() ? 32 : 128;

        Head = new ModelRenderer(this);
        Head.setPos(0.0F, 0.0F, 0.0F);
        Head.texOffs(0, 0).addBox(-1.0F, -10.75F, -5.75F, 2.0F, 7.0F, 1.0F, 0.0F, false);
        Head.texOffs(0, 29).addBox(-1.0F, -10.75F, -4.75F, 2.0F, 1.0F, 11.0F, 0.0F, false);
        Head.texOffs(20, 31).addBox(-0.4F, -11.75F, 3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Head.texOffs(20, 31).addBox(-0.4F, -11.75F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Head.texOffs(20, 31).addBox(-0.4F, -11.75F, -3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Head.texOffs(15, 26).addBox(-5.9F, -12.25F, -3.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
        Head.texOffs(15, 26).addBox(5.1F, -12.25F, -3.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
        Head.texOffs(21, 14).addBox(-1.0F, -9.75F, 5.25F, 2.0F, 6.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(4.5F, -8.0F, -5.5F);
        Head.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.0F, 1.2654F);
        cube_r1.texOffs(29, 3).addBox(-2.0F, -1.75F, -0.5F, 1.0F, 1.0F, 11.0F, 0.0F, false);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(-4.5F, -8.0F, -5.5F);
        Head.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.0F, 0.3491F);
        cube_r2.texOffs(29, 3).addBox(-1.75F, -2.0F, -0.5F, 1.0F, 1.0F, 11.0F, 0.0F, false);

        ModelRenderer cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(5.6F, -8.0F, 2.0F);
        Head.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.0F, 0.0F, 0.5236F);
        cube_r3.texOffs(0, 14).addBox(-0.5F, -4.25F, -1.0F, 1.0F, 5.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r4 = new ModelRenderer(this);
        cube_r4.setPos(-5.4F, -8.0F, 2.0F);
        Head.addChild(cube_r4);
        setRotationAngle(cube_r4, 0.0F, 0.0F, -0.5672F);
        cube_r4.texOffs(0, 14).addBox(-0.5F, -4.25F, -1.0F, 1.0F, 5.0F, 2.0F, 0.0F, false);

        ModelRenderer helmet2 = new ModelRenderer(this);
        helmet2.setPos(0.0F, 0.0F, 0.0F);
        Head.addChild(helmet2);
        helmet2.texOffs(28, 15).addBox(-5.5F, -9.75F, 4.5F, 11.0F, 8.0F, 1.0F, 0.0F, false);
        helmet2.texOffs(48, 25).addBox(-4.5F, -9.75F, -5.5F, 9.0F, 5.0F, 1.0F, 0.0F, false);
        helmet2.texOffs(32, 0).addBox(-4.5F, -4.75F, -5.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        helmet2.texOffs(32, 0).addBox(-5.5F, -1.75F, -4.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        helmet2.texOffs(32, 4).addBox(-5.5F, -1.75F, -3.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        helmet2.texOffs(32, 4).addBox(4.5F, -1.75F, -3.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        helmet2.texOffs(32, 0).addBox(4.5F, -1.75F, -4.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        helmet2.texOffs(0, 34).addBox(-5.5F, -1.75F, -5.5F, 4.0F, 3.0F, 1.0F, 0.0F, false);
        helmet2.texOffs(0, 34).addBox(1.5F, -1.75F, -5.5F, 4.0F, 3.0F, 1.0F, 0.0F, false);
        helmet2.texOffs(32, 0).addBox(3.5F, -4.75F, -5.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        ModelRenderer Helmet6_r1 = new ModelRenderer(this);
        Helmet6_r1.setPos(0.0F, 0.0F, 0.0F);
        helmet2.addChild(Helmet6_r1);
        setRotationAngle(Helmet6_r1, -1.5708F, 0.0F, 0.0F);
        Helmet6_r1.texOffs(43, 43).addBox(-4.5F, -4.5F, -9.75F, 9.0F, 9.0F, 1.0F, 0.0F, false);

        ModelRenderer Helmet4_r1 = new ModelRenderer(this);
        Helmet4_r1.setPos(0.0F, 0.0F, 0.0F);
        helmet2.addChild(Helmet4_r1);
        setRotationAngle(Helmet4_r1, 0.0F, 1.5708F, 0.0F);
        Helmet4_r1.texOffs(42, 0).addBox(-4.5F, -9.75F, -5.5F, 10.0F, 8.0F, 1.0F, 0.0F, false);
        Helmet4_r1.texOffs(42, 0).addBox(-4.5F, -9.75F, 4.5F, 10.0F, 8.0F, 1.0F, 0.0F, false);

        Body = new ModelRenderer(this);
        Body.setPos(0.0F, 0.0F, 0.0F);
        Body.texOffs(24, 24).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
        Body.texOffs(0, 0).addBox(-6.0F, -2.0F, -4.0F, 12.0F, 6.0F, 8.0F, 0.0F, false);
        Body.texOffs(0, 28).addBox(1.0F, -1.0F, -5.0F, 4.0F, 5.0F, 1.0F, 0.0F, true);
        Body.texOffs(0, 28).addBox(-5.0F, -1.0F, -5.0F, 4.0F, 5.0F, 1.0F, 0.0F, false);
        Body.texOffs(15, 32).addBox(-1.0F, -1.0F, -5.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);
        Body.texOffs(40, 32).addBox(-6.0F, 4.0F, -4.0F, 4.0F, 3.0F, 8.0F, 0.0F, false);
        Body.texOffs(40, 32).addBox(2.0F, 4.0F, -4.0F, 4.0F, 3.0F, 8.0F, 0.0F, true);
        Body.texOffs(20, 35).addBox(-2.0F, 4.0F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        Body.texOffs(20, 35).addBox(-2.0F, 4.0F, 3.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        Body.texOffs(19, 26).addBox(-1.0F, 4.0F, -4.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        Body.texOffs(19, 26).addBox(-1.0F, 4.0F, 3.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        Body.texOffs(20, 35).addBox(1.0F, 4.0F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        Body.texOffs(20, 35).addBox(1.0F, 4.0F, 3.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        ModelRenderer Innerchestplate = new ModelRenderer(this);
        Innerchestplate.setPos(0.0F, 0.0F, 0.0F);
        Body.addChild(Innerchestplate);
        Innerchestplate.texOffs(0, 26).addBox(-3.0F, 11.75F, -3.25F, 6.0F, 1.0F, 1.0F, 0.0F, false);
        Innerchestplate.texOffs(16, 52).addBox(-4.0F, 5.0F, -3.5F, 8.0F, 7.0F, 1.0F, 0.0F, false);
        Innerchestplate.texOffs(16, 52).addBox(-4.0F, 5.0F, 2.5F, 8.0F, 7.0F, 1.0F, 0.0F, false);
        Innerchestplate.texOffs(0, 26).addBox(-3.0F, 11.75F, 2.25F, 6.0F, 1.0F, 1.0F, 0.0F, false);

        RightArm = new ModelRenderer(this);
        RightArm.setPos(-5.0F, 2.0F, 0.0F);
        RightArm.texOffs(0, 41).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
        RightArm.texOffs(0, 14).addBox(-7.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, true);
        RightArm.texOffs(19, 40).addBox(-7.0F, 3.0F, -3.5F, 5.0F, 5.0F, 7.0F, 0.0F, true);
        RightArm.texOffs(46, 18).addBox(-6.0F, -5.0F, -3.0F, 4.0F, 1.0F, 6.0F, 0.0F, true);

        LeftArm = new ModelRenderer(this);
        LeftArm.setPos(5.0F, 2.0F, 0.0F);
        LeftArm.texOffs(0, 41).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, true);
        LeftArm.texOffs(46, 18).addBox(2.0F, -5.0F, -3.0F, 4.0F, 1.0F, 6.0F, 0.0F, false);
        LeftArm.texOffs(0, 14).addBox(0.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, false);
        LeftArm.texOffs(19, 40).addBox(1.75F, 3.0F, -3.5F, 5.0F, 5.0F, 7.0F, 0.0F, false);

        RightBoot = new ModelRenderer(this);
        RightBoot.setPos(-1.9F, 12.0F, 0.0F);
        RightBoot.texOffs(52, 11).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, false);

        LeftBoot = new ModelRenderer(this);
        LeftBoot.setPos(1.9F, 12.0F, 0.0F);
        LeftBoot.texOffs(52, 11).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, true);

        Belt = new ModelRenderer(this);
        Belt.setPos(0.0F, 0.0F, 0.0F);
        Belt.texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);

        RightLeg = new ModelRenderer(this);
        RightLeg.setPos(-1.9F, 12.0F, 0.0F);
        RightLeg.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
        RightLeg.texOffs(19, 1).addBox(-3.25F, -2.0F, -3.0F, 2.0F, 6.0F, 6.0F, 0.0F, false);
        RightLeg.texOffs(0, 0).addBox(-3.25F, 4.0F, -2.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setPos(1.9F, 12.0F, 0.0F);
        LeftLeg.texOffs(0, 0).addBox(1.2F, 4.0F, -2.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
        LeftLeg.texOffs(0, 0).addBox(1.2F, -2.0F, -3.0F, 2.0F, 6.0F, 6.0F, 0.0F, false);
        LeftLeg.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, true);
    }

}

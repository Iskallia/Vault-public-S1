package iskallia.vault.item.gear.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class Plated3ArmorModel<T extends LivingEntity> extends VaultGearModel<T> {

    public Plated3ArmorModel(float modelSize, EquipmentSlotType slotType) {
        super(modelSize, slotType);
        texWidth = isLayer2() ? 64 : 128;
        texHeight = isLayer2() ? 32 : 128;

        Head = new ModelRenderer(this);
        Head.setPos(0.0F, 0.0F, 0.0F);
        Head.texOffs(0, 25).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
        Head.texOffs(24, 50).addBox(-6.0F, -4.0F, -5.0F, 1.0F, 5.0F, 6.0F, 0.0F, false);
        Head.texOffs(24, 50).addBox(5.0F, -4.0F, -5.0F, 1.0F, 5.0F, 6.0F, 0.0F, false);
        Head.texOffs(0, 19).addBox(-6.0F, -1.0F, -6.0F, 3.0F, 2.0F, 1.0F, 0.0F, false);
        Head.texOffs(0, 19).addBox(3.0F, -1.0F, -6.0F, 3.0F, 2.0F, 1.0F, 0.0F, false);
        Head.texOffs(0, 0).addBox(-1.0F, -9.0F, -6.0F, 2.0F, 5.0F, 1.0F, 0.0F, false);
        Head.texOffs(0, 41).addBox(-1.0F, -9.0F, -5.0F, 2.0F, 1.0F, 10.0F, 0.0F, false);
        Head.texOffs(0, 14).addBox(-1.0F, -9.0F, 5.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(6.0F, -7.4944F, -4.5165F);
        Head.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.8727F, 0.0F, 0.0F);
        cube_r1.texOffs(30, 18).addBox(-4.0F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        cube_r1.texOffs(30, 18).addBox(-9.0F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(-2.5F, 0.5F, -5.5F);
        Head.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, -0.3927F, 0.0F);
        cube_r2.texOffs(0, 6).addBox(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(2.5F, 0.5F, -5.5F);
        Head.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.0F, 0.3927F, 0.0F);
        cube_r3.texOffs(0, 6).addBox(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r4 = new ModelRenderer(this);
        cube_r4.setPos(2.1F, -7.75F, 5.0F);
        Head.addChild(cube_r4);
        setRotationAngle(cube_r4, 0.0F, 0.0F, 0.6545F);
        cube_r4.texOffs(4, 25).addBox(-0.5F, -2.75F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r5 = new ModelRenderer(this);
        cube_r5.setPos(5.6F, -9.5F, 3.5F);
        Head.addChild(cube_r5);
        setRotationAngle(cube_r5, 0.0F, 0.0F, 0.6545F);
        cube_r5.texOffs(4, 25).addBox(-0.5F, -2.75F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r6 = new ModelRenderer(this);
        cube_r6.setPos(3.6F, -9.0F, -0.5F);
        Head.addChild(cube_r6);
        setRotationAngle(cube_r6, 0.0F, 0.0F, 0.5236F);
        cube_r6.texOffs(32, 32).addBox(-0.5F, -2.25F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r7 = new ModelRenderer(this);
        cube_r7.setPos(-1.9F, -7.75F, 4.5F);
        Head.addChild(cube_r7);
        setRotationAngle(cube_r7, 0.0F, 0.0F, -0.6109F);
        cube_r7.texOffs(4, 25).addBox(-0.5F, -2.75F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r8 = new ModelRenderer(this);
        cube_r8.setPos(-5.4F, -9.25F, 3.0F);
        Head.addChild(cube_r8);
        setRotationAngle(cube_r8, 0.0F, 0.0F, -0.6109F);
        cube_r8.texOffs(4, 25).addBox(-0.5F, -2.75F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r9 = new ModelRenderer(this);
        cube_r9.setPos(-3.4F, -8.75F, -1.0F);
        Head.addChild(cube_r9);
        setRotationAngle(cube_r9, 0.0F, 0.0F, -0.5236F);
        cube_r9.texOffs(32, 32).addBox(-0.5F, -2.25F, 0.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);

        Body = new ModelRenderer(this);
        Body.setPos(0.0F, 0.0F, 0.0F);
        Body.texOffs(40, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
        Body.texOffs(0, 0).addBox(-6.0F, -2.0F, -4.0F, 12.0F, 6.0F, 8.0F, 0.0F, false);
        Body.texOffs(0, 14).addBox(-5.5F, 4.0F, -4.0F, 11.0F, 3.0F, 8.0F, 0.0F, false);
        Body.texOffs(31, 18).addBox(-5.25F, 7.0F, -3.5F, 10.0F, 3.0F, 7.0F, 0.0F, false);
        Body.texOffs(0, 52).addBox(-5.0F, -1.0F, -5.0F, 10.0F, 3.0F, 2.0F, 0.0F, false);

        RightArm = new ModelRenderer(this);
        RightArm.setPos(-5.0F, 2.0F, 0.0F);
        RightArm.texOffs(38, 44).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
        RightArm.texOffs(32, 32).addBox(-6.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, false);
        RightArm.texOffs(14, 41).addBox(-5.0F, 5.0F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, true);

        ModelRenderer cube_r10 = new ModelRenderer(this);
        cube_r10.setPos(-3.0F, -5.0F, -4.0F);
        RightArm.addChild(cube_r10);
        setRotationAngle(cube_r10, 0.0F, 0.0F, 0.829F);
        cube_r10.texOffs(30, 16).addBox(-1.0F, 0.0F, 2.0F, 5.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r11 = new ModelRenderer(this);
        cube_r11.setPos(-4.0F, -5.0F, -2.0F);
        RightArm.addChild(cube_r11);
        setRotationAngle(cube_r11, 0.0F, 0.0F, 0.829F);
        cube_r11.texOffs(24, 30).addBox(-3.0F, 0.0F, 2.0F, 7.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r12 = new ModelRenderer(this);
        cube_r12.setPos(13.0F, -4.0F, -4.0F);
        RightArm.addChild(cube_r12);
        setRotationAngle(cube_r12, 0.0F, 0.0F, 2.3562F);
        cube_r12.texOffs(30, 16).addBox(-1.5F, 0.0F, 2.0F, 5.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r13 = new ModelRenderer(this);
        cube_r13.setPos(14.0F, -4.0F, -2.0F);
        RightArm.addChild(cube_r13);
        setRotationAngle(cube_r13, 0.0F, 0.0F, 2.3562F);
        cube_r13.texOffs(24, 30).addBox(-3.0F, 0.0F, 2.0F, 7.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r14 = new ModelRenderer(this);
        cube_r14.setPos(15.0F, -1.0F, 0.0F);
        RightArm.addChild(cube_r14);
        setRotationAngle(cube_r14, 0.0F, 0.0F, 2.618F);
        cube_r14.texOffs(24, 28).addBox(-4.0F, 0.0F, 2.0F, 8.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r15 = new ModelRenderer(this);
        cube_r15.setPos(15.0F, -4.0F, 0.0F);
        RightArm.addChild(cube_r15);
        setRotationAngle(cube_r15, 0.0F, 0.0F, 2.618F);
        cube_r15.texOffs(24, 28).addBox(-4.0F, 0.0F, 2.0F, 8.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r16 = new ModelRenderer(this);
        cube_r16.setPos(14.125F, -4.5F, 3.0F);
        RightArm.addChild(cube_r16);
        setRotationAngle(cube_r16, -0.6545F, 0.0F, 0.0F);
        cube_r16.texOffs(0, 25).addBox(-0.125F, -4.5F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        cube_r16.texOffs(0, 25).addBox(-19.125F, -4.5F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r17 = new ModelRenderer(this);
        cube_r17.setPos(-5.0F, -2.0F, 0.0F);
        RightArm.addChild(cube_r17);
        setRotationAngle(cube_r17, 0.0F, 0.0F, 0.4363F);
        cube_r17.texOffs(24, 28).addBox(-4.0F, 0.0F, 2.0F, 8.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r18 = new ModelRenderer(this);
        cube_r18.setPos(-5.0F, -5.0F, 0.0F);
        RightArm.addChild(cube_r18);
        setRotationAngle(cube_r18, 0.0F, 0.0F, 0.4363F);
        cube_r18.texOffs(24, 28).addBox(-4.0F, 0.0F, 2.0F, 8.0F, 1.0F, 1.0F, 0.0F, false);

        LeftArm = new ModelRenderer(this);
        LeftArm.setPos(5.0F, 2.0F, 0.0F);
        LeftArm.texOffs(38, 44).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, true);
        LeftArm.texOffs(14, 41).addBox(-0.25F, 5.0F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, false);
        LeftArm.texOffs(32, 32).addBox(-1.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, false);

        RightBoot = new ModelRenderer(this);
        RightBoot.setPos(-1.9F, 12.0F, 0.0F);
        RightBoot.texOffs(53, 28).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, false);
        RightBoot.texOffs(44, 5).addBox(-3.25F, 8.0F, -3.75F, 6.0F, 5.0F, 1.0F, 0.0F, false);
        RightBoot.texOffs(44, 5).addBox(0.75F, 8.0F, -3.75F, 6.0F, 5.0F, 1.0F, 0.0F, false);
        RightBoot.texOffs(43, 5).addBox(-2.25F, 7.0F, -3.75F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        RightBoot.texOffs(43, 5).addBox(1.75F, 7.0F, -3.75F, 4.0F, 1.0F, 1.0F, 0.0F, false);

        LeftBoot = new ModelRenderer(this);
        LeftBoot.setPos(1.9F, 12.0F, 0.0F);
        LeftBoot.texOffs(53, 28).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, true);

        Belt = new ModelRenderer(this);
        Belt.setPos(0.0F, 0.0F, 0.0F);
        Belt.texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);

        RightLeg = new ModelRenderer(this);
        RightLeg.setPos(-1.9F, 12.0F, 0.0F);
        RightLeg.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
        RightLeg.texOffs(0, 0).addBox(-3.0F, 3.0F, -3.0F, 5.0F, 5.0F, 6.0F, 0.0F, false);
        RightLeg.texOffs(0, 0).addBox(2.0F, 3.0F, -3.0F, 5.0F, 5.0F, 6.0F, 0.0F, false);

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setPos(1.9F, 12.0F, 0.0F);
        LeftLeg.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, true);
    }

}

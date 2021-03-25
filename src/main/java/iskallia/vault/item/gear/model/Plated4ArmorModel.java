package iskallia.vault.item.gear.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class Plated4ArmorModel<T extends LivingEntity> extends VaultGearModel<T> {

    public Plated4ArmorModel(float modelSize, EquipmentSlotType slotType) {
        super(modelSize, slotType);
        texWidth = 64;
        texHeight = isLayer2() ? 32 : 64;

        Head = new ModelRenderer(this);
        Head.setPos(0.0F, 0.0F, 0.0F);
        Head.texOffs(0, 0).addBox(-4.0F, -7.0F, -4.25F, 8.0F, 8.0F, 8.0F, 1.0F, false);
        Head.texOffs(20, 16).addBox(-6.0F, -4.0F, -5.0F, 1.0F, 5.0F, 6.0F, 0.0F, false);
        Head.texOffs(20, 16).addBox(5.0F, -4.0F, -5.0F, 1.0F, 5.0F, 6.0F, 0.0F, false);
        Head.texOffs(32, 5).addBox(-6.0F, -2.0F, -5.75F, 3.0F, 2.0F, 1.0F, 0.0F, false);
        Head.texOffs(32, 5).addBox(3.0F, -2.0F, -5.75F, 3.0F, 2.0F, 1.0F, 0.0F, false);
        Head.texOffs(0, 0).addBox(-3.0F, -9.0F, -4.5F, 6.0F, 1.0F, 9.0F, 0.0F, false);
        Head.texOffs(0, 0).addBox(-5.5F, -7.0F, -2.5F, 1.0F, 5.0F, 6.0F, 0.0F, false);
        Head.texOffs(0, 0).addBox(4.5F, -7.0F, -2.5F, 1.0F, 5.0F, 6.0F, 0.0F, true);
        Head.texOffs(3, 1).addBox(-2.0F, -10.0F, -3.5F, 4.0F, 1.0F, 7.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, -5.5F, -5.25F);
        Head.addChild(cube_r1);
        setRotationAngle(cube_r1, -0.7418F, 0.0F, 0.0F);
        cube_r1.texOffs(28, 0).addBox(-3.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, false);

        Body = new ModelRenderer(this);
        Body.setPos(0.0F, 0.0F, 0.0F);
        Body.texOffs(0, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, true);
        Body.texOffs(0, 43).addBox(-6.0F, -2.0F, -4.0F, 12.0F, 13.0F, 8.0F, 0.0F, false);
        Body.texOffs(38, 9).addBox(-5.0F, -1.0F, -6.0F, 10.0F, 8.0F, 3.0F, 0.0F, false);
        Body.texOffs(13, 51).addBox(-5.0F, -1.0F, 3.0F, 10.0F, 8.0F, 1.0F, 0.0F, false);
        Body.texOffs(40, 43).addBox(-4.5F, 7.75F, 2.0F, 9.0F, 1.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(0.0F, 1.875F, -5.875F);
        Body.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.3927F, 0.0F, 0.0F);
        cube_r2.texOffs(40, 43).addBox(-4.75F, -1.875F, -0.875F, 9.0F, 3.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(0.75F, 8.875F, -3.375F);
        Body.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.6545F, 0.0F, 0.0F);
        cube_r3.texOffs(40, 43).addBox(-4.5F, -0.875F, -1.375F, 7.0F, 1.0F, 2.0F, 0.0F, false);

        RightArm = new ModelRenderer(this);
        RightArm.setPos(-5.0F, 2.0F, 0.0F);
        RightArm.texOffs(0, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);
        RightArm.texOffs(36, 32).addBox(-5.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, true);
        RightArm.texOffs(0, 55).addBox(-6.25F, -6.25F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, true);
        RightArm.texOffs(0, 55).addBox(-6.25F, -6.25F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, true);
        RightArm.texOffs(20, 52).addBox(-5.0F, 5.0F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, true);

        LeftArm = new ModelRenderer(this);
        LeftArm.setPos(5.0F, 2.0F, 0.0F);
        LeftArm.texOffs(0, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, true);
        LeftArm.texOffs(20, 52).addBox(-0.25F, 5.0F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, false);
        LeftArm.texOffs(36, 32).addBox(-2.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, false);
        LeftArm.texOffs(0, 55).addBox(1.25F, -6.25F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, false);

        RightBoot = new ModelRenderer(this);
        RightBoot.setPos(-1.9F, 12.0F, 0.0F);
        RightBoot.texOffs(48, 57).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, false);

        LeftBoot = new ModelRenderer(this);
        LeftBoot.setPos(1.9F, 12.0F, 0.0F);
        LeftBoot.texOffs(48, 57).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, true);

        Belt = new ModelRenderer(this);
        Belt.setPos(0.0F, 0.0F, 0.0F);
        Belt.texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);

        RightLeg = new ModelRenderer(this);
        RightLeg.setPos(-1.9F, 12.0F, 0.0F);
        RightLeg.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);
        RightLeg.texOffs(0, 0).addBox(-3.0F, 3.0F, -3.0F, 3.0F, 4.0F, 6.0F, 0.0F, false);

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setPos(1.9F, 12.0F, 0.0F);
        LeftLeg.texOffs(0, 0).addBox(-0.05F, 3.0F, -3.0F, 3.0F, 4.0F, 6.0F, 0.0F, true);
        LeftLeg.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, true);
    }

}

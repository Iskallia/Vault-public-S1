package iskallia.vault.item.gear.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class SamuraiArmorModel<T extends LivingEntity> extends VaultGearModel<T> {

    public SamuraiArmorModel(float modelSize, EquipmentSlotType slotType) {
        super(modelSize, slotType);

        Head = new ModelRenderer(this);
        Head.setPos(0.0F, 0.0F, 0.0F);
        Head.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
        Head.texOffs(36, 3).addBox(-1.5F, -10.0F, -5.0F, 3.0F, 1.0F, 0.0F, 0.0F, false);
        Head.texOffs(36, 3).addBox(-2.5F, -11.0F, -5.0F, 2.0F, 1.0F, 0.0F, 0.0F, false);
        Head.texOffs(37, 4).addBox(0.5F, -11.0F, -5.0F, 2.0F, 1.0F, 0.0F, 0.0F, false);
        Head.texOffs(38, 6).addBox(2.5F, -12.0F, -5.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        Head.texOffs(40, 6).addBox(-3.5F, -12.0F, -5.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);

        Body = new ModelRenderer(this);
        Body.setPos(0.0F, 0.0F, 0.0F);
        Body.texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.01F, false);
        Body.texOffs(56, 0).addBox(-1.5F, 1.0F, -3.5F, 3.0F, 4.0F, 1.0F, 0.0F, false);
        Body.texOffs(56, 0).addBox(-1.5F, 1.0F, 2.5F, 3.0F, 4.0F, 1.0F, 0.0F, false);
        Body.texOffs(56, 0).addBox(-2.5F, 0.0F, -3.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.texOffs(56, 0).addBox(-2.5F, 0.0F, 2.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.texOffs(56, 0).addBox(1.5F, 0.0F, -3.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.texOffs(56, 0).addBox(1.5F, 0.0F, 2.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.texOffs(56, 0).addBox(2.5F, -1.0F, -3.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.texOffs(56, 0).addBox(2.5F, -1.0F, 2.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.texOffs(56, 0).addBox(-3.5F, -1.0F, -3.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.texOffs(56, 0).addBox(-3.5F, -1.0F, 2.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        Body.texOffs(56, 0).addBox(-4.5F, -1.0F, -3.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        Body.texOffs(56, 0).addBox(-4.5F, -1.0F, 2.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        Body.texOffs(56, 0).addBox(3.5F, -1.0F, -3.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        Body.texOffs(56, 0).addBox(3.5F, -1.0F, 2.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        RightArm = new ModelRenderer(this);
        RightArm.setPos(-5.0F, 2.0F, 0.0F);
        RightArm.texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);

        ModelRenderer shoulderpad_r1 = new ModelRenderer(this);
        shoulderpad_r1.setPos(-1.5F, -2.75F, -0.5F);
        RightArm.addChild(shoulderpad_r1);
        setRotationAngle(shoulderpad_r1, 0.0F, 0.0F, 0.3927F);
        shoulderpad_r1.texOffs(36, 7).addBox(-3.0F, -1.0F, -3.0F, 5.0F, 2.0F, 7.0F, 0.0F, false);

        LeftArm = new ModelRenderer(this);
        LeftArm.setPos(5.0F, 2.0F, 0.0F);
        LeftArm.texOffs(40, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, true);

        ModelRenderer shoulderpad_r2 = new ModelRenderer(this);
        shoulderpad_r2.setPos(2.0381F, -2.9413F, 0.0F);
        LeftArm.addChild(shoulderpad_r2);
        setRotationAngle(shoulderpad_r2, 0.0F, 0.0F, -0.3491F);
        shoulderpad_r2.texOffs(36, 7).addBox(-2.5F, -1.0F, -3.5F, 5.0F, 2.0F, 7.0F, 0.0F, true);

        RightLeg = new ModelRenderer(this);
        RightLeg.setPos(-1.9F, 12.0F, 0.0F);
        RightLeg.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, false);

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setPos(1.9F, 12.0F, 0.0F);
        LeftLeg.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, true);

        Belt = new ModelRenderer(this);
        Belt.setPos(0.0F, 0.0F, 0.0F);
        Belt.texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.51F, false);

        RightBoot = new ModelRenderer(this);
        RightBoot.setPos(-1.9F, 12.0F, 0.0F);
        RightBoot.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);

        LeftBoot = new ModelRenderer(this);
        LeftBoot.setPos(1.9F, 12.0F, 0.0F);
        LeftBoot.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, true);
    }

}

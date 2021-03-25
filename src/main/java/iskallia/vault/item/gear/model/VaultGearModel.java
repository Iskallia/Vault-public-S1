package iskallia.vault.item.gear.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public abstract class VaultGearModel<T extends LivingEntity> extends BipedModel<T> {

    protected final EquipmentSlotType slotType;

    protected ModelRenderer Head;

    protected ModelRenderer Body;
    protected ModelRenderer RightArm;
    protected ModelRenderer LeftArm;

    protected ModelRenderer RightLeg;
    protected ModelRenderer LeftLeg;

    protected ModelRenderer Belt;
    protected ModelRenderer RightBoot;
    protected ModelRenderer LeftBoot;

    public VaultGearModel(float modelSize, EquipmentSlotType slotType) {
        super(modelSize, 0, 64, 32);
        this.slotType = slotType;
    }

    public boolean isLayer2() {
        return slotType == EquipmentSlotType.LEGS
                || slotType == EquipmentSlotType.FEET;
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        matrixStack.push();

        if (this.slotType == EquipmentSlotType.HEAD) {
            Head.copyModelAngles(this.bipedHead);
            Head.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

        } else if (this.slotType == EquipmentSlotType.CHEST) {
            Body.copyModelAngles(this.bipedBody);
            Body.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            RightArm.copyModelAngles(this.bipedRightArm);
            RightArm.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            LeftArm.copyModelAngles(this.bipedLeftArm);
            LeftArm.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

        } else if (this.slotType == EquipmentSlotType.LEGS) {
            Belt.copyModelAngles(bipedBody);
            Belt.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
//            matrixStack.translate(0, 0.15f, 0);
//            float scale = 0.9f;
//            matrixStack.scale(scale, scale, scale);
            RightLeg.copyModelAngles(this.bipedRightLeg);
            RightLeg.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            LeftLeg.copyModelAngles(this.bipedLeftLeg);
            LeftLeg.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

        } else if (this.slotType == EquipmentSlotType.FEET) {
            RightBoot.copyModelAngles(this.bipedRightLeg);
            RightBoot.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            LeftBoot.copyModelAngles(this.bipedLeftLeg);
            LeftBoot.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        matrixStack.pop();
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

}

package iskallia.vault.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.entity.TreasureGoblinEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class TreasureGoblinModel extends EntityModel<TreasureGoblinEntity> {

    private final ModelRenderer Head;
    private final ModelRenderer ear6_r1;
    private final ModelRenderer ear5_r1;
    private final ModelRenderer Body;
    private final ModelRenderer RightArm;
    private final ModelRenderer LeftArm;
    private final ModelRenderer RightLeg;
    private final ModelRenderer LeftLeg;

    public TreasureGoblinModel() {
        texWidth = 64;
        texHeight = 64;

        Head = new ModelRenderer(this);
        Head.setPos(0.0F, 0.0F, 0.0F);
        Head.texOffs(0, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, 1.0F, false);
        Head.texOffs(0, 26).addBox(-1.0F, -2.0F, -7.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
        Head.texOffs(0, 21).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 1.0F, 1.0F, 0.0F, false);

        ear6_r1 = new ModelRenderer(this);
        ear6_r1.setPos(6.375F, -4.875F, 2.125F);
        Head.addChild(ear6_r1);
        setRotationAngle(ear6_r1, 0.0F, 0.3927F, 0.0F);
        ear6_r1.texOffs(0, 0).addBox(-0.125F, -2.125F, 0.875F, 0.0F, 1.0F, 2.0F, 0.0F, false);
        ear6_r1.texOffs(0, 0).addBox(-0.125F, -1.125F, -1.125F, 0.0F, 1.0F, 3.0F, 0.0F, false);
        ear6_r1.texOffs(0, 0).addBox(-0.125F, -0.125F, -2.125F, 0.0F, 1.0F, 3.0F, 0.0F, false);
        ear6_r1.texOffs(0, 0).addBox(-0.125F, 0.875F, -3.125F, 0.0F, 2.0F, 3.0F, 0.0F, false);

        ear5_r1 = new ModelRenderer(this);
        ear5_r1.setPos(-6.625F, -4.875F, 2.125F);
        Head.addChild(ear5_r1);
        setRotationAngle(ear5_r1, 0.0F, -0.7854F, 0.0F);
        ear5_r1.texOffs(0, 0).addBox(-0.125F, -2.125F, 0.875F, 0.0F, 1.0F, 2.0F, 0.0F, false);
        ear5_r1.texOffs(0, 0).addBox(-0.125F, -1.125F, -1.125F, 0.0F, 1.0F, 3.0F, 0.0F, false);
        ear5_r1.texOffs(0, 0).addBox(-0.125F, -0.125F, -2.125F, 0.0F, 1.0F, 3.0F, 0.0F, false);
        ear5_r1.texOffs(0, 1).addBox(-0.125F, 0.875F, -3.125F, 0.0F, 2.0F, 3.0F, 0.0F, false);

        Body = new ModelRenderer(this, 16, 16);
        Body.setPos(0.0F, 0.0F, 0.0F);
        Body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F);
        Body.setPos(0.0F, 0.0F, 0.0F);

        RightArm = new ModelRenderer(this, 40, 16);
        RightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        RightArm.setPos(-5.0F, 2.0F, 0.0F);

        LeftArm = new ModelRenderer(this, 40, 16);
        LeftArm.mirror = true;
        LeftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        LeftArm.setPos(5.0F, 2.0F, 0.0F);

        RightLeg = new ModelRenderer(this, 0, 16);
        RightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        RightLeg.setPos(-1.9F, 12.0F, 0.0F);

        LeftLeg = new ModelRenderer(this, 0, 16);
        LeftLeg.mirror = true;
        LeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        LeftLeg.setPos(1.9F, 12.0F, 0.0F);
    }

    @Override
    public void setupAnim(TreasureGoblinEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        //previously the render function, render code was moved to a method below
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        matrixStack.pushPose();
        matrixStack.translate(0, 0.75, 0);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        Head.render(matrixStack, buffer, packedLight, packedOverlay);
        Body.render(matrixStack, buffer, packedLight, packedOverlay);
        RightArm.render(matrixStack, buffer, packedLight, packedOverlay);
        LeftArm.render(matrixStack, buffer, packedLight, packedOverlay);
        RightLeg.render(matrixStack, buffer, packedLight, packedOverlay);
        LeftLeg.render(matrixStack, buffer, packedLight, packedOverlay);
        matrixStack.popPose();
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }

}

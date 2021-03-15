package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.ResourceLocation;

public class RobotRenderer extends IronGolemRenderer {

    public static final ResourceLocation TEXTURE = Vault.id("textures/entity/robot.png");

    public RobotRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    protected void preRenderCallback(IronGolemEntity entitylivingbase, MatrixStack matrixStack, float partialTickTime) {
        super.preRenderCallback(entitylivingbase, matrixStack, partialTickTime);
        matrixStack.scale(2, 2, 2);
    }

    @Override
    public ResourceLocation getEntityTexture(IronGolemEntity entity) {
        return TEXTURE;
    }

}

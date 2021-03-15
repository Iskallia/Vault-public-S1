package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.util.ResourceLocation;

public class BlueBlazeRenderer extends BlazeRenderer {

    public static final ResourceLocation TEXTURE = Vault.id("textures/entity/blue_blaze.png");

    public BlueBlazeRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    protected void preRenderCallback(BlazeEntity entitylivingbase, MatrixStack matrixStack, float partialTickTime) {
        super.preRenderCallback(entitylivingbase, matrixStack, partialTickTime);
        matrixStack.scale(2, 2, 2);
    }

    @Override
    public ResourceLocation getEntityTexture(BlazeEntity entity) {
        return TEXTURE;
    }

}

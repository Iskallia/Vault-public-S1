package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

public class VaultGuardianRenderer extends PiglinRenderer {

    public static final ResourceLocation TEXTURE = Vault.id("textures/entity/vault_guardian.png");

    public VaultGuardianRenderer(EntityRendererManager renderManager) {
        super(renderManager, false);
    }

    @Override
    protected void preRenderCallback(MobEntity entity, MatrixStack matrixStack, float partialTickTime) {
        super.preRenderCallback(entity, matrixStack, partialTickTime);


        matrixStack.scale(1.5f, 1.5f, 1.5f);
    }

    @Override
    public ResourceLocation getEntityTexture(MobEntity entity) {
        return TEXTURE;
    }

}

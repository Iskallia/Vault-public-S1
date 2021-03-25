package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.entity.FinalDummyEntity;
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
    protected void scale(MobEntity entity, MatrixStack matrixStack, float partialTickTime) {
        super.scale(entity, matrixStack, partialTickTime);

        if(entity instanceof FinalDummyEntity) {
            float size = ((FinalDummyEntity) entity).sizeMultiplier;
            matrixStack.scale(size, size, size);
            return;
        }

        matrixStack.scale(1.5f, 1.5f, 1.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(MobEntity entity) {
        return TEXTURE;
    }

}

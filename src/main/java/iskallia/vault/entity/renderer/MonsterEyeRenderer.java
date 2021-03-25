package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.ResourceLocation;

public class MonsterEyeRenderer extends SlimeRenderer {

    public static final ResourceLocation TEXTURE = Vault.id("textures/entity/monster_eye.png");

    public MonsterEyeRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected void scale(SlimeEntity entitylivingbase, MatrixStack matrixStack, float partialTickTime) {
        super.scale(entitylivingbase, matrixStack, partialTickTime);
        matrixStack.scale(2, 2, 2);
    }

    @Override
    public ResourceLocation getTextureLocation(SlimeEntity entity) {
        return TEXTURE;
    }

}

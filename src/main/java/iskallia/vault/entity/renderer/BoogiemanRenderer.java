package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;

public class BoogiemanRenderer extends ZombieRenderer {

    public static final ResourceLocation TEXTURE = Vault.id("textures/entity/boogieman.png");

    public BoogiemanRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);

//        for (int i = layerRenderers.size() - 1; i >= 0; i--) {
//            LayerRenderer<ZombieEntity, ZombieModel<ZombieEntity>> layer = layerRenderers.get(i);
//            System.out.println(i + " " + layer.getClass().getSimpleName());
//        }

        layers.remove(this.layers.size() - 1); // Do not render armors
    }

    @Override
    protected void scale(ZombieEntity entitylivingbase, MatrixStack matrixStack, float partialTickTime) {
        super.scale(entitylivingbase, matrixStack, partialTickTime);
        matrixStack.scale(2, 2, 2);
    }

    @Override
    public ResourceLocation getTextureLocation(ZombieEntity entity) {
        return TEXTURE;
    }

}

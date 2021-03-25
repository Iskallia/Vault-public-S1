package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.entity.TreasureGoblinEntity;
import iskallia.vault.entity.model.TreasureGoblinModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class TreasureGoblinRenderer extends MobRenderer<TreasureGoblinEntity, TreasureGoblinModel> {

    public static final ResourceLocation TREASURE_GOBLIN_TEXTURES = Vault.id("textures/entity/treasure_goblin.png");

    public TreasureGoblinRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new TreasureGoblinModel(), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TreasureGoblinEntity entity) {
        return TREASURE_GOBLIN_TEXTURES;
    }

    @Override
    protected void setupRotations(TreasureGoblinEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }

}

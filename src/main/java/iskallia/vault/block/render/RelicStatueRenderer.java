package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.Vault;
import iskallia.vault.block.PlayerStatueBlock;
import iskallia.vault.block.RelicStatueBlock;
import iskallia.vault.block.entity.RelicStatueTileEntity;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.item.RelicItem;
import iskallia.vault.util.RelicSet;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;

public class RelicStatueRenderer extends TileEntityRenderer<RelicStatueTileEntity> {

    public static final StatuePlayerModel<PlayerEntity> PLAYER_MODEL = new StatuePlayerModel<>(0.1f, true);
    public static final ResourceLocation TWOLF999_SKIN = Vault.id("textures/block/statue_twolf999.png");
    public static final ResourceLocation SHIELDMANH_SKIN = Vault.id("textures/block/statue_shieldmanh.png");

    public RelicStatueRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(RelicStatueTileEntity statue, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        RelicSet relicSet = RelicSet.REGISTRY.get(statue.getRelicSet());
        BlockState state = statue.getBlockState();

        matrixStack.pushPose();
        matrixStack.translate(0.5, 0, 0.5);
        float horizontalAngle = state.getValue(RelicStatueBlock.FACING).toYRot();
        matrixStack.mulPose(Vector3f.YN.rotationDegrees(180 + horizontalAngle));

        if (relicSet == RelicSet.DRAGON) {
            matrixStack.translate(0, 0, 0.15);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
            renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 0.7f, 7f, Registry.ITEM.get(Vault.id("statue_dragon")));
        } else if (relicSet == RelicSet.MINER) {
            renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 1.2f, 2f, RelicItem.withCustomModelData(0));
        } else if (relicSet == RelicSet.WARRIOR) {
            renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 1.2f, 2f, RelicItem.withCustomModelData(1));
        } else if (relicSet == RelicSet.RICHITY) {
            renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 1.2f, 2f, RelicItem.withCustomModelData(2));
        } else if (relicSet == RelicSet.TWITCH) {
            renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 1.2f, 2f, RelicItem.withCustomModelData(3));
        } else if (relicSet == RelicSet.CUPCAKE) {
            renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 1.2f, 2f, RelicItem.withCustomModelData(4));
        } else if (relicSet == RelicSet.ELEMENT) {
            renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 1.2f, 2f, RelicItem.withCustomModelData(5));
        } else if (relicSet == RelicSet.TWOLF999) {
            IVertexBuilder vertexBuilder = getPlayerVertexBuilder(TWOLF999_SKIN, buffer);
            renderPlayer(matrixStack, state, vertexBuilder, combinedLight, combinedOverlay);
        } else if (relicSet == RelicSet.SHIELDMANH) {
            IVertexBuilder vertexBuilder = getPlayerVertexBuilder(SHIELDMANH_SKIN, buffer);
            renderPlayer(matrixStack, state, vertexBuilder, combinedLight, combinedOverlay);
        }

        matrixStack.popPose();
    }

    public IVertexBuilder getPlayerVertexBuilder(ResourceLocation skinTexture, IRenderTypeBuffer buffer) {
        RenderType renderType = PLAYER_MODEL.renderType(skinTexture);
        return buffer.getBuffer(renderType);
    }

    public void renderPlayer(MatrixStack matrixStack, BlockState blockState, IVertexBuilder vertexBuilder, int combinedLight, int combinedOverlay) {
        Direction direction = blockState.getValue(PlayerStatueBlock.FACING);

        matrixStack.pushPose();
        matrixStack.translate(0, 1.6, 0);
        matrixStack.scale(0.4f, 0.4f, 0.4f);
//        matrixStack.rotate(Vector3f.YN.rotationDegrees(direction.getHorizontalAngle() + 180));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180));
        PLAYER_MODEL.body.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.leftLeg.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.rightLeg.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.leftArm.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.rightArm.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);

        PLAYER_MODEL.jacket.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.leftPants.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.rightPants.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.leftSleeve.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);

        matrixStack.pushPose();
        matrixStack.translate(0, 0, -0.62f);
        PLAYER_MODEL.rightSleeve.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        matrixStack.popPose();

        PLAYER_MODEL.hat.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.head.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);

        matrixStack.popPose();
    }

    private void renderItem(MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel, int overlay,
                            float yOffset, float scale, Item item) {
        renderItem(matrixStack, buffer, lightLevel, overlay, yOffset, scale, new ItemStack(item));
    }

    private void renderItem(MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel, int overlay,
                            float yOffset, float scale, ItemStack itemStack) {
        Minecraft minecraft = Minecraft.getInstance();
        matrixStack.pushPose();
        matrixStack.translate(0, yOffset, 0);
        matrixStack.scale(scale, scale, scale);
        IBakedModel ibakedmodel = minecraft
                .getItemRenderer().getModel(itemStack, null, null);
        minecraft.getItemRenderer()
                .render(itemStack, ItemCameraTransforms.TransformType.GROUND, true,
                        matrixStack, buffer, lightLevel, overlay, ibakedmodel);
        matrixStack.popPose();
    }

}

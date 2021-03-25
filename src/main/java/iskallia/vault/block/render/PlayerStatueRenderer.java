package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.Vault;
import iskallia.vault.block.PlayerStatueBlock;
import iskallia.vault.block.entity.PlayerStatueTileEntity;
import iskallia.vault.entity.model.StatuePlayerModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;

public class PlayerStatueRenderer extends TileEntityRenderer<PlayerStatueTileEntity> {

    protected static final StatuePlayerModel<PlayerEntity> PLAYER_MODEL = new StatuePlayerModel<>(0.1f, true);

    private Minecraft mc = Minecraft.getInstance();

    public PlayerStatueRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(PlayerStatueTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        String latestNickname = tileEntity.getSkin().getLatestNickname();

        if (latestNickname == null || latestNickname.equals(""))
            return;

        ResourceLocation skinLocation = tileEntity.getSkin().getLocationSkin();
        RenderType renderType = PLAYER_MODEL.renderType(skinLocation);
        IVertexBuilder vertexBuilder = buffer.getBuffer(renderType);

        BlockState blockState = tileEntity.getBlockState();
        Direction direction = blockState.getValue(PlayerStatueBlock.FACING);

        float scale = 0.4f;
        float headScale = 1.75f;
        float crownScale = 1.5f;

        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.9, 0.5);
        matrixStack.scale(scale, scale, scale);
        matrixStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot() + 180));
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

        matrixStack.scale(headScale, headScale, headScale);
        PLAYER_MODEL.hat.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.head.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);

        matrixStack.popPose();

        Minecraft minecraft = Minecraft.getInstance();
        if (tileEntity.hasCrown() && minecraft.player != null) {
            matrixStack.pushPose();
            matrixStack.translate(0.5, 1.2, 0.5);
            matrixStack.scale(crownScale, crownScale, crownScale);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(minecraft.player.tickCount));
            //        matrixStack.rotate(Vector3f.ZP.rotationDegrees(20f));
            ItemStack itemStack = new ItemStack(Registry.ITEM.get(Vault.id("mvp_crown")));
            IBakedModel ibakedmodel = minecraft
                    .getItemRenderer().getModel(itemStack, null, null);
            minecraft.getItemRenderer()
                    .render(itemStack, ItemCameraTransforms.TransformType.GROUND, true,
                            matrixStack, buffer, combinedLight, combinedOverlay, ibakedmodel);
            matrixStack.popPose();
        }

        StringTextComponent text = new StringTextComponent(tileEntity.getSkin().getLatestNickname());

        if(mc.hitResult != null && mc.hitResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult result = (BlockRayTraceResult)mc.hitResult;

            if(tileEntity.getBlockPos().equals(result.getBlockPos())) {
                renderLabel(matrixStack, buffer, combinedLight, text, 0xFF_FFFFFF);
            }
        }
    }

    private void renderLabel(MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel,
                             StringTextComponent text, int color) {
        FontRenderer fontRenderer = mc.font;

        //render amount required for the item
        matrixStack.pushPose();
        float scale = 0.02f;
        int opacity = (int) (.4f * 255.0F) << 24;
        float offset = (float) (-fontRenderer.width(text) / 2);
        Matrix4f matrix4f = matrixStack.last().pose();

        matrixStack.translate(0.5f, 1.6f, 0.5f);
        matrixStack.scale(scale, scale, scale);
        matrixStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation()); // face the camera
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F)); // flip vertical

        fontRenderer.drawInBatch(text, offset, 0, color, false, matrix4f, buffer, true, opacity, lightLevel);
        fontRenderer.drawInBatch(text, offset, 0, -1, false, matrix4f, buffer, false, 0, lightLevel);
        matrixStack.popPose();
    }

}

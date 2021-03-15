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
        RenderType renderType = PLAYER_MODEL.getRenderType(skinLocation);
        IVertexBuilder vertexBuilder = buffer.getBuffer(renderType);

        BlockState blockState = tileEntity.getBlockState();
        Direction direction = blockState.get(PlayerStatueBlock.FACING);

        float scale = 0.4f;
        float headScale = 1.75f;
        float crownScale = 1.5f;

        matrixStack.push();
        matrixStack.translate(0.5, 0.9, 0.5);
        matrixStack.scale(scale, scale, scale);
        matrixStack.rotate(Vector3f.YN.rotationDegrees(direction.getHorizontalAngle() + 180));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(180));
        PLAYER_MODEL.bipedBody.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.bipedLeftLeg.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.bipedRightLeg.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.bipedLeftArm.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.bipedRightArm.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);

        PLAYER_MODEL.bipedBodyWear.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.bipedLeftLegwear.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.bipedRightLegwear.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.bipedLeftArmwear.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);

        matrixStack.push();
        matrixStack.translate(0, 0, -0.62f);
        PLAYER_MODEL.bipedRightArmwear.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        matrixStack.pop();

        matrixStack.scale(headScale, headScale, headScale);
        PLAYER_MODEL.bipedHeadwear.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
        PLAYER_MODEL.bipedHead.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);

        matrixStack.pop();

        Minecraft minecraft = Minecraft.getInstance();
        if (tileEntity.hasCrown() && minecraft.player != null) {
            matrixStack.push();
            matrixStack.translate(0.5, 1.2, 0.5);
            matrixStack.scale(crownScale, crownScale, crownScale);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(minecraft.player.ticksExisted));
            //        matrixStack.rotate(Vector3f.ZP.rotationDegrees(20f));
            ItemStack itemStack = new ItemStack(Registry.ITEM.getOrDefault(Vault.id("mvp_crown")));
            IBakedModel ibakedmodel = minecraft
                    .getItemRenderer().getItemModelWithOverrides(itemStack, null, null);
            minecraft.getItemRenderer()
                    .renderItem(itemStack, ItemCameraTransforms.TransformType.GROUND, true,
                            matrixStack, buffer, combinedLight, combinedOverlay, ibakedmodel);
            matrixStack.pop();
        }

        StringTextComponent text = new StringTextComponent(tileEntity.getSkin().getLatestNickname());

        if(mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult result = (BlockRayTraceResult)mc.objectMouseOver;

            if(tileEntity.getPos().equals(result.getPos())) {
                renderLabel(matrixStack, buffer, combinedLight, text, 0xFF_FFFFFF);
            }
        }
    }

    private void renderLabel(MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel,
                             StringTextComponent text, int color) {
        FontRenderer fontRenderer = mc.fontRenderer;

        //render amount required for the item
        matrixStack.push();
        float scale = 0.02f;
        int opacity = (int) (.4f * 255.0F) << 24;
        float offset = (float) (-fontRenderer.getStringPropertyWidth(text) / 2);
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();

        matrixStack.translate(0.5f, 1.6f, 0.5f);
        matrixStack.scale(scale, scale, scale);
        matrixStack.rotate(mc.getRenderManager().getCameraOrientation()); // face the camera
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F)); // flip vertical

        fontRenderer.func_243247_a(text, offset, 0, color, false, matrix4f, buffer, true, opacity, lightLevel);
        fontRenderer.func_243247_a(text, offset, 0, -1, false, matrix4f, buffer, false, 0, lightLevel);
        matrixStack.pop();
    }

}

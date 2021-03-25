package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.block.entity.VaultRuneTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;

public class VaultRuneRenderer extends TileEntityRenderer<VaultRuneTileEntity> {

    private Minecraft mc = Minecraft.getInstance();

    public VaultRuneRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void
    render(VaultRuneTileEntity tileEntity, float partialTicks, MatrixStack matrixStack,
           IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        ClientPlayerEntity player = mc.player;
        Vector3d eyePosition = player.getEyePosition(1);
        Vector3d look = player.getViewVector(1);
        Vector3d endPos = eyePosition.add(look.x * 5, look.y * 5, look.z * 5);
        RayTraceContext context = new RayTraceContext(eyePosition, endPos,
                RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player);

        BlockRayTraceResult result = player.level.clip(context);

        if (result.getBlockPos().equals(tileEntity.getBlockPos())) {
            StringTextComponent text = new StringTextComponent(tileEntity.getBelongsTo());
            renderLabel(matrixStack, buffer, combinedLight, text, 0xFF_FFFFFF);
        }
    }

    private void renderLabel(MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel,
                             StringTextComponent text, int color) {
        FontRenderer fontRenderer = mc.font;

        //render amount required for the item
        matrixStack.pushPose();
        float scale = 0.02f;
        int opacity = (int) (0.4f * 255.0F) << 24;
        float offset = (float) (-fontRenderer.width(text) / 2);
        Matrix4f matrix4f = matrixStack.last().pose();

        matrixStack.translate(0.5f, 1.4f, 0.5f);
        matrixStack.scale(scale, scale, scale);
        matrixStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation()); // face the camera
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F)); // flip vertical

        fontRenderer.drawInBatch(text, offset, 0, color, false, matrix4f, buffer, true, opacity, lightLevel);
        fontRenderer.drawInBatch(text, offset, 0, -1, false, matrix4f, buffer, false, 0, lightLevel);
        matrixStack.popPose();
    }

}

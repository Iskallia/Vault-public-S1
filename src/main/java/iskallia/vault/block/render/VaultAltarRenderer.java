package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.altar.RequiredItem;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import iskallia.vault.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.List;

public class VaultAltarRenderer extends TileEntityRenderer<VaultAltarTileEntity> {

    private Minecraft mc = Minecraft.getInstance();
    private float currentTick = 0;

    public VaultAltarRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(VaultAltarTileEntity altar, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if (!altar.containsVaultRock())
            return;

        ClientPlayerEntity player = mc.player;
        int lightLevel = getLightAtPos(altar.getLevel(), altar.getBlockPos().above());

        // render vault rock for all players
        renderItem(new ItemStack(ModItems.VAULT_ROCK),
                new double[]{.5d, 1.60d, .5d},
                Vector3f.YP.rotationDegrees(180.0F - player.yRot),
                matrixStack, buffer, partialTicks, combinedOverlay, lightLevel);

        if (altar.getRecipe() == null || altar.getRecipe().getRequiredItems().isEmpty()) return;

        AltarInfusionRecipe recipe = altar.getRecipe();
        List<RequiredItem> items = recipe.getRequiredItems();
        for (int i = 0; i < items.size(); i++) {
            double[] translation = getTranslation(i);
            RequiredItem requiredItem = items.get(i);
            ItemStack stack = requiredItem.getItem();
            StringTextComponent text = new StringTextComponent(String.valueOf(requiredItem.getAmountRequired() - requiredItem.getCurrentAmount()));
            int textColor = 0xffffff;
            if (requiredItem.reachedAmountRequired()) {
                text = new StringTextComponent("Complete");
                textColor = 0x00ff00;
            }

            renderItem(stack, translation,
                    Vector3f.YP.rotationDegrees(getAngle(player, partialTicks) * 5f),
                    matrixStack, buffer, partialTicks, combinedOverlay, lightLevel);
            renderLabel(requiredItem, matrixStack, buffer, lightLevel, translation, text, textColor);
        }


    }

    private void renderItem(ItemStack stack, double[] translation, Quaternion rotation, MatrixStack matrixStack, IRenderTypeBuffer buffer, float partialTicks, int combinedOverlay, int lightLevel) {
        matrixStack.pushPose();
        matrixStack.translate(translation[0], translation[1], translation[2]);
        matrixStack.mulPose(rotation);
        IBakedModel ibakedmodel = mc.getItemRenderer().getModel(stack, null, null);
        mc.getItemRenderer().render(stack, ItemCameraTransforms.TransformType.GROUND, true, matrixStack, buffer, lightLevel, combinedOverlay, ibakedmodel);
        matrixStack.popPose();
    }

    private void renderLabel(RequiredItem item, MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel, double[] corner, StringTextComponent text, int color) {
        FontRenderer fontRenderer = mc.font;

        //render amount required for the item
        matrixStack.pushPose();
        float scale = 0.01f;
        int opacity = (int) (.4f * 255.0F) << 24;
        float offset = (float) (-fontRenderer.width(text) / 2);
        Matrix4f matrix4f = matrixStack.last().pose();

        matrixStack.translate(corner[0], corner[1] + .4f, corner[2]);
        matrixStack.scale(scale, scale, scale);
        matrixStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation()); // face the camera
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F)); // flip vertical
        fontRenderer.drawInBatch(text, offset, 0, color, false, matrix4f, buffer, false, opacity, lightLevel);
        matrixStack.popPose();
    }

    private float getAngle(ClientPlayerEntity player, float partialTicks) {
        currentTick = player.tickCount;
        float angle = (currentTick + partialTicks) % 360;
        return angle;
    }

    private int getLightAtPos(World world, BlockPos pos) {
        int blockLight = world.getBrightness(LightType.BLOCK, pos);
        int skyLight = world.getBrightness(LightType.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }

    private double[] getTranslation(int index) {
        switch (index) {
            case 0:
                return new double[]{.875, 1.1, 0.125};
            case 1:
                return new double[]{.875, 1.1, .875};
            case 2:
                return new double[]{0.125, 1.1, .875};
            default:
                return new double[]{0.125, 1.1, 0.125};
        }
    }

}

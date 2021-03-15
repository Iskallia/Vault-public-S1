package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.Vault;
import iskallia.vault.block.LootStatueBlock;
import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class LootStatueRenderer extends TileEntityRenderer<LootStatueTileEntity> {

    protected static final StatuePlayerModel<PlayerEntity> PLAYER_MODEL = new StatuePlayerModel<>(0.1f, true);

    private Minecraft mc = Minecraft.getInstance();

    public LootStatueRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(LootStatueTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

        if (tileEntity.getChipCount() > 0) {


            ClientPlayerEntity player = mc.player;
            int lightLevel = getLightAtPos(tileEntity.getWorld(), tileEntity.getPos().up());
            for (int i = 0; i < tileEntity.getChipCount(); i++) {
                // render acceleration chips
                renderItem(new ItemStack(ModItems.ACCELERATION_CHIP),
                        getTranslation(i),
                        Vector3f.YP.rotationDegrees(180.0F - player.rotationYaw),
                        matrixStack, buffer, partialTicks, combinedOverlay, lightLevel);
            }

        }
        String latestNickname = tileEntity.getSkin().getLatestNickname();

        if (latestNickname == null || latestNickname.equals(""))
            return;

        ResourceLocation skinLocation = tileEntity.getSkin().getLocationSkin();
        RenderType renderType = PLAYER_MODEL.getRenderType(skinLocation);
        IVertexBuilder vertexBuilder = buffer.getBuffer(renderType);

        BlockState blockState = tileEntity.getBlockState();
        Direction direction = blockState.get(LootStatueBlock.FACING);

        float scale = 0.4f;
        float headScale = 1.75f;
        float hatScale = 3f;
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
        LootStatueBlock block = (LootStatueBlock) blockState.getBlock();
        if (block.getType().ordinal() == 1 && minecraft.player != null) {
            matrixStack.push();
            matrixStack.translate(0.5, 1.1, 0.5);
            matrixStack.scale(hatScale, hatScale, hatScale);
            matrixStack.rotate(Vector3f.YN.rotationDegrees(direction.getHorizontalAngle() + 180));
//            matrixStack.rotate(Vector3f.ZP.rotationDegrees(20f));
            ItemStack itemStack = new ItemStack(Registry.ITEM.getOrDefault(Vault.id("bow_hat")));
            IBakedModel ibakedmodel = minecraft
                    .getItemRenderer().getItemModelWithOverrides(itemStack, null, null);
            minecraft.getItemRenderer()
                    .renderItem(itemStack, ItemCameraTransforms.TransformType.GROUND, true,
                            matrixStack, buffer, combinedLight, combinedOverlay, ibakedmodel);
            matrixStack.pop();
        }

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

        matrixStack.translate(0.5f, 1.7f, 0.5f);
        matrixStack.scale(scale, scale, scale);
        matrixStack.rotate(mc.getRenderManager().getCameraOrientation()); // face the camera
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F)); // flip vertical

        fontRenderer.func_243247_a(text, offset, 0, color, false, matrix4f, buffer, true, opacity, lightLevel);
        fontRenderer.func_243247_a(text, offset, 0, -1, false, matrix4f, buffer, false, 0, lightLevel);
        matrixStack.pop();
    }

    private void renderItem(ItemStack stack, double[] translation, Quaternion rotation, MatrixStack matrixStack, IRenderTypeBuffer buffer, float partialTicks, int combinedOverlay, int lightLevel) {
        matrixStack.push();
        matrixStack.translate(translation[0], translation[1], translation[2]);
        matrixStack.rotate(rotation);
        matrixStack.scale(0.25f,0.25f,0.25f);
        IBakedModel ibakedmodel = mc.getItemRenderer().getItemModelWithOverrides(stack, null, null);
        mc.getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, true, matrixStack, buffer, lightLevel, combinedOverlay, ibakedmodel);
        matrixStack.pop();
    }

    private int getLightAtPos(World world, BlockPos pos) {
        int blockLight = world.getLightFor(LightType.BLOCK, pos);
        int skyLight = world.getLightFor(LightType.SKY, pos);
        return LightTexture.packLight(blockLight, skyLight);
    }
    private double[] getTranslation(int index) {
        switch (index) {
            case 0:
                return new double[]{.75, 0.3, 0.25};
            case 1:
                return new double[]{.75, 0.3, .75};
            case 2:
                return new double[]{0.25, 0.3, .75};
            default:
                return new double[]{0.25, 0.3, 0.25};
        }
    }

}

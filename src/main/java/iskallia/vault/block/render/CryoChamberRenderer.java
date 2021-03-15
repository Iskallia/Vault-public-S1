package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.Vault;
import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.block.PlayerStatueBlock;
import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.entity.EternalData;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.init.ModConfigs;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.awt.*;

public class CryoChamberRenderer extends TileEntityRenderer<CryoChamberTileEntity> {

    public static final Minecraft mc = Minecraft.getInstance();
    public static final ResourceLocation INFUSED_PLAYER_SKIN = Vault.id("textures/entity/infusion_skin_white.png");
    public static final StatuePlayerModel<PlayerEntity> PLAYER_MODEL = new StatuePlayerModel<>(0.1f, true);

    public CryoChamberRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);

    }

    public IVertexBuilder getPlayerVertexBuilder(ResourceLocation skinTexture, IRenderTypeBuffer buffer) {
        RenderType renderType = PLAYER_MODEL.getRenderType(skinTexture);
        return buffer.getBuffer(renderType);
    }


    @Override
    public void render(CryoChamberTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if (tileEntity.isInfusing()) {
            float maxTime = ((float) ModConfigs.CRYO_CHAMBER.getInfusionTime());
            float scale = Math.min(tileEntity.getInfusionTimeRemaining() / maxTime, .85f);
            tileEntity.updateSkin();
            ResourceLocation skinTexture = tileEntity.getSkin().getLocationSkin();
            IVertexBuilder vertexBuilder = getPlayerVertexBuilder(skinTexture, buffer);
            renderPlayerModel(matrixStack, tileEntity, scale, 0.5f,
                    vertexBuilder, combinedLight, combinedOverlay);

        } else if (tileEntity.isGrowingEternal()) {
            float maxTime = ((float) ModConfigs.CRYO_CHAMBER.getGrowEternalTime());
            float scale = Math.min(1 - tileEntity.getGrowEternalTimeRemaining() / maxTime, .85f);
            IVertexBuilder vertexBuilder = getPlayerVertexBuilder(INFUSED_PLAYER_SKIN, buffer);
            renderPlayerModel(matrixStack, tileEntity, scale, .5f,
                    vertexBuilder, combinedLight, combinedOverlay);

        } else if (tileEntity.getEternalName() != null && !tileEntity.getEternalName().isEmpty()) {
            tileEntity.updateSkin();
            ResourceLocation skinTexture = tileEntity.getSkin().getLocationSkin();
            IVertexBuilder vertexBuilder = getPlayerVertexBuilder(skinTexture, buffer);
            renderPlayerModel(matrixStack, tileEntity, .85f, 1f,
                    vertexBuilder, combinedLight, combinedOverlay);
        }

        renderArmor(matrixStack, tileEntity, buffer, combinedOverlay);

        renderLiquid(matrixStack, tileEntity, buffer, partialTicks);

        if (mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK
                && tileEntity.getEternal() != null && tileEntity.getEternal().getName() != null) {
            BlockRayTraceResult result = (BlockRayTraceResult) mc.objectMouseOver;

            if (tileEntity.getPos().equals(result.getPos()) || tileEntity.getPos().up().equals(result.getPos())) {
                renderLabel(matrixStack, buffer, combinedLight, new StringTextComponent(tileEntity.getEternal().getName()),
                        0xFF_FFFFFF, tileEntity.getWorld().getBlockState(result.getPos()).get(CryoChamberBlock.HALF) == DoubleBlockHalf.UPPER);
            }
        }
    }

    private void renderLabel(MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel,
                             StringTextComponent text, int color, boolean topBlock) {
        FontRenderer fontRenderer = mc.fontRenderer;

        //render amount required for the item
        matrixStack.push();
        float scale = 0.02f;
        int opacity = (int) (.4f * 255.0F) << 24;
        float offset = (float) (-fontRenderer.getStringPropertyWidth(text) / 2);
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();

        matrixStack.translate(0.5f, 2.3f, 0.5f);
        matrixStack.scale(scale, scale, scale);
        matrixStack.rotate(mc.getRenderManager().getCameraOrientation()); // face the camera
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F)); // flip vertical

        fontRenderer.func_243247_a(text, offset, 0, color, false, matrix4f, buffer, true, opacity, lightLevel);
        fontRenderer.func_243247_a(text, offset, 0, -1, false, matrix4f, buffer, false, 0, lightLevel);
        matrixStack.pop();
    }

    public void renderPlayerModel(MatrixStack matrixStack, CryoChamberTileEntity tileEntity, float scale, float alpha, IVertexBuilder vertexBuilder, int combinedLight, int combinedOverlay) {
        BlockState blockState = tileEntity.getBlockState();
        Direction direction = blockState.get(PlayerStatueBlock.FACING);

        matrixStack.push();
        matrixStack.translate(0.5, 1.3, 0.5);
        matrixStack.scale(scale, scale, scale);
        matrixStack.rotate(Vector3f.YN.rotationDegrees(direction.getHorizontalAngle() + 180));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(180));
        PLAYER_MODEL.bipedBody.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.bipedLeftLeg.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.bipedRightLeg.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.bipedLeftArm.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.bipedRightArm.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);

        PLAYER_MODEL.bipedBodyWear.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.bipedLeftLegwear.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.bipedRightLegwear.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.bipedLeftArmwear.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);

        matrixStack.push();
        matrixStack.translate(0, 0, -0.62f);
        PLAYER_MODEL.bipedRightArmwear.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        matrixStack.pop();

        PLAYER_MODEL.bipedHeadwear.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.bipedHead.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        matrixStack.pop();
    }

    public void renderArmor(MatrixStack matrixStack, CryoChamberTileEntity tileEntity, IRenderTypeBuffer buffer, int combinedOverlay) {
        if (tileEntity.getEternal() == null) return;
        BlockState blockState = tileEntity.getBlockState();
        Direction direction = blockState.get(CryoChamberBlock.FACING);

        int lightLevel = getLightAtPos(tileEntity.getWorld(), tileEntity.getPos().up());

        EternalData eternalData = tileEntity.getEternal();

        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            ItemStack stack = eternalData.getStack(slot);
            if (stack.isEmpty()) continue;
            renderItem(stack, matrixStack, buffer, combinedOverlay, lightLevel, direction, slot);
        }
    }


    private void renderItem(ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedOverlay, int lightLevel, Direction direction, EquipmentSlotType slot) {
        matrixStack.push();
        double[] rootTranslation = getRootTranslation(direction);
        double[] itemTranslation = getItemTranslation(slot);
        matrixStack.rotate(getRotationFromDirection(direction));
        matrixStack.translate(rootTranslation[0], rootTranslation[1], rootTranslation[2]);
        matrixStack.translate(itemTranslation[0], itemTranslation[1], itemTranslation[2]);
        matrixStack.scale(0.25f, 0.25f, 0.25f);

        IBakedModel ibakedmodel = mc.getItemRenderer().getItemModelWithOverrides(stack, null, null);
        mc.getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStack, buffer, lightLevel, combinedOverlay, ibakedmodel);
        matrixStack.pop();
    }

    private int getLightAtPos(World world, BlockPos pos) {
        int blockLight = world.getLightFor(LightType.BLOCK, pos);
        int skyLight = world.getLightFor(LightType.SKY, pos);
        return LightTexture.packLight(blockLight, skyLight);
    }

    private Quaternion getRotationFromDirection(Direction direction) {
        switch (direction) {
            case NORTH:
            case SOUTH:
                return Vector3f.YP.rotationDegrees(direction.getOpposite().getHorizontalAngle());
            default:
                return Vector3f.YP.rotationDegrees(direction.getHorizontalAngle());
        }
    }

    private double[] getRootTranslation(Direction direction) {
        switch (direction) {
            case SOUTH:
                return new double[]{-1.0f, 0f, -1.0f};
            case WEST:
                return new double[]{-1.0f, 0f, 0f};
            case EAST:
                return new double[]{0f, 0f, -1.0f};
            default:
                return new double[]{0f, 0f, 0f};
        }
    }

    private double[] getItemTranslation(EquipmentSlotType slot) {
        double pixel = .0625d;
        double width = 14d * pixel;
        double distance = width / 6d;
        double start = pixel * 2d;
        switch (slot) {
            case MAINHAND:
                return new double[]{start, 1.85d, 1};
            case HEAD:
                return new double[]{start + distance, 1.85d, 1};
            case CHEST:
                return new double[]{start + distance * 2d, 1.85d, 1};
            case LEGS:
                return new double[]{start + distance * 3d, 1.85d, 1};
            case FEET:
                return new double[]{start + distance * 4d, 1.85d, 1};
            case OFFHAND:
                return new double[]{start + distance * 5d, 1.85d, 1};
        }
        return new double[3];
    }

    private void renderLiquid(MatrixStack matrixStack, CryoChamberTileEntity tileEntity, IRenderTypeBuffer buffer, float partialTicks) {
        if (tileEntity.getMaxCores() == 0) return;
        IVertexBuilder builder = buffer.getBuffer(RenderType.getTranslucent());
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(Fluids.WATER.getAttributes().getStillTexture());
        BlockState blockState = tileEntity.getBlockState();
        Direction direction = blockState.get(PlayerStatueBlock.FACING);

        float max = tileEntity.getMaxCores();
        float difference = (float) tileEntity.getCoreCount() - tileEntity.lastCoreCount;
        tileEntity.lastCoreCount += difference * 0.02F;
        float scale = tileEntity.lastCoreCount / max;

        updateIndex(mc.player.ticksExisted);

        updateColor(partialTicks);

        float r = currentColor.getRed() / 255f;
        float g = currentColor.getGreen() / 255.0F;
        float b = currentColor.getBlue() / 255.0F;

        float minU = sprite.getInterpolatedU(0);
        float maxU = sprite.getInterpolatedU(16);
        float minV = sprite.getInterpolatedV(0);

        float maxVBottom = sprite.getInterpolatedV(scale < .5 ? (scale * 2) * 16d : 16);
        float maxVTop = sprite.getInterpolatedV(scale >= .5 ? ((scale * 2) - 1) * 16d : 0);

        float bottomHeight = scale < .5f ? scale * 2f : 1.0f;
        float topHeight = scale < .5f ? 0f : Math.min(scale * 2f, 1.90f);

        matrixStack.push();
        renderSides(matrixStack, builder, scale, r, g, b, minU, maxU, minV, maxVBottom, maxVTop, bottomHeight, topHeight, direction);
        renderTop(matrixStack, builder, scale, r, g, b, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV(), bottomHeight, topHeight);
        matrixStack.pop();
    }


    private void renderTop(MatrixStack matrixStack, IVertexBuilder builder, float scale, float r, float g, float b, float minU, float maxU, float minV, float maxV, float bottomHeight, float topHeight) {

        //top
        addVertex(builder, matrixStack, p2f(1), scale < .5f ? bottomHeight : topHeight, p2f(1), minU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(1), scale < .5f ? bottomHeight : topHeight, p2f(9), maxU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(15), scale < .5f ? bottomHeight : topHeight, p2f(9), maxU, maxV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(15), scale < .5f ? bottomHeight : topHeight, p2f(1), minU, maxV, r, g, b, 1f);

        addVertex(builder, matrixStack, p2f(1), scale < .5f ? bottomHeight : topHeight, p2f(9), minU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(4), scale < .5f ? bottomHeight : topHeight, p2f(15), maxU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(12), scale < .5f ? bottomHeight : topHeight, p2f(15), maxU, maxV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(15), scale < .5f ? bottomHeight : topHeight, p2f(9), minU, maxV, r, g, b, 1f);
    }


    private void renderSides(MatrixStack matrixStack, IVertexBuilder builder, float scale, float r, float g, float b, float minU, float maxU, float minV, float maxVBottom, float maxVTop, float bottomHeight, float topHeight, Direction direction) {

        double[] translation = getRootTranslation(direction);
        matrixStack.rotate(getRotationFromDirection(direction));
        matrixStack.translate(translation[0], translation[1], translation[2]);


        /*--------- Bottom Half ---------*/

        // Front
        addVertex(builder, matrixStack, p2f(4), p2f(1), p2f(15), minU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(12), p2f(1), p2f(15), maxU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(12), bottomHeight, p2f(15), maxU, maxVBottom, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(4), bottomHeight, p2f(15), minU, maxVBottom, r, g, b, 1f);

        // Left Angle
        addVertex(builder, matrixStack, p2f(1), p2f(1), p2f(9), minU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(4), p2f(1), p2f(15), maxU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(4), bottomHeight, p2f(15), maxU, maxVBottom, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(1), bottomHeight, p2f(9), minU, maxVBottom, r, g, b, 1f);

        // Right Angle
        addVertex(builder, matrixStack, p2f(12), p2f(1), p2f(15), minU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(15), p2f(1), p2f(9), maxU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(15), bottomHeight, p2f(9), maxU, maxVBottom, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(12), bottomHeight, p2f(15), minU, maxVBottom, r, g, b, 1f);

        // Left Wall
        addVertex(builder, matrixStack, p2f(1), p2f(1), p2f(1), minU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(1), p2f(1), p2f(9), maxU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(1), bottomHeight, p2f(9), maxU, maxVBottom, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(1), bottomHeight, p2f(1), minU, maxVBottom, r, g, b, 1f);

        // Right Wall
        addVertex(builder, matrixStack, p2f(15), p2f(1), p2f(9), minU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(15), p2f(1), p2f(1), maxU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(15), bottomHeight, p2f(1), maxU, maxVBottom, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(15), bottomHeight, p2f(9), minU, maxVBottom, r, g, b, 1f);

        if (scale < 0.5f) return;

        /*--------- Top Half ---------*/

        // Front
        addVertex(builder, matrixStack, p2f(4), p2f(16), p2f(15), minU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(12), p2f(16), p2f(15), maxU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(12), topHeight, p2f(15), maxU, maxVTop, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(4), topHeight, p2f(15), minU, maxVTop, r, g, b, 1f);

        // Left Angle
        addVertex(builder, matrixStack, p2f(1), p2f(16), p2f(9), minU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(4), p2f(16), p2f(15), maxU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(4), topHeight, p2f(15), maxU, maxVTop, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(1), topHeight, p2f(9), minU, maxVTop, r, g, b, 1f);

        // Right Angle
        addVertex(builder, matrixStack, p2f(12), p2f(16), p2f(15), minU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(15), p2f(16), p2f(9), maxU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(15), topHeight, p2f(9), maxU, maxVTop, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(12), topHeight, p2f(15), minU, maxVTop, r, g, b, 1f);

        // Left Wall
        addVertex(builder, matrixStack, p2f(1), p2f(16), p2f(1), minU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(1), p2f(16), p2f(9), maxU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(1), topHeight, p2f(9), maxU, maxVTop, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(1), topHeight, p2f(1), minU, maxVTop, r, g, b, 1f);

        // Right Wall
        addVertex(builder, matrixStack, p2f(15), p2f(16), p2f(9), minU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(15), p2f(16), p2f(1), maxU, minV, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(15), topHeight, p2f(1), maxU, maxVTop, r, g, b, 1f);
        addVertex(builder, matrixStack, p2f(15), topHeight, p2f(9), minU, maxVTop, r, g, b, 1f);

    }

    private void addVertex(IVertexBuilder renderer, MatrixStack stack, float x, float y, float z, float u, float v, float r, float g, float b, float a) {
        renderer.pos(stack.getLast().getMatrix(), x, y, z)
                .color(r, g, b, .5f)
                .tex(u, v)
                .lightmap(0, 240)
                .normal(1, 0, 0)
                .endVertex();
    }

    // convert block pixel to float
    private float p2f(int pixel) {
        return 0.0625f * (float) pixel;
    }


    /*~~~~~~~~~~~~~~~~~~COLOR STUFF~~~~~~~~~~~~~~~~~~~*/


    private final Color[] colors = new Color[]{
            Color.WHITE,
            Color.YELLOW,
            Color.MAGENTA,
            Color.GREEN
    };

    private int index = 0;
    private boolean wait = false;

    private Color currentColor = Color.WHITE;
    private float currentRed = 1.0f;
    private float currentGreen = 1.0f;
    private float currentBlue = 1.0f;
    private final float colorChangeDelay = 3.0f;

    private void updateIndex(int ticksExisted) {
        if (ticksExisted % (20 * colorChangeDelay) == 0) {
            if (wait) return;
            wait = true;
            if (index++ == colors.length - 1) index = 0;
        } else {
            wait = false;
        }
    }

    private void updateColor(float partialTicks) {
        int nextIndex = index + 1;
        if (nextIndex == colors.length) nextIndex = 0;
        currentColor = getBlendedColor(colors[index], colors[nextIndex], partialTicks);
    }

    private Color getBlendedColor(Color prev, Color next, float partialTicks) {
        float prevRed = prev.getRed() / 255f;
        float prevGreen = prev.getGreen() / 255f;
        float prevBlue = prev.getBlue() / 255f;

        float nextRed = next.getRed() / 255f;
        float nextGreen = next.getGreen() / 255f;
        float nextBlue = next.getBlue() / 255f;

        float percentage = .01f;
        float transitionTime = colorChangeDelay * .3f;
        float red = Math.abs((nextRed - prevRed) * percentage / transitionTime * partialTicks);
        float green = Math.abs((nextGreen - prevGreen) * percentage / transitionTime * partialTicks);
        float blue = Math.abs((nextBlue - prevBlue) * percentage / transitionTime * partialTicks);

        currentRed = nextRed > prevRed ? currentRed + red : currentRed - red;
        currentGreen = nextGreen > prevGreen ? currentGreen + green : currentGreen - green;
        currentBlue = nextBlue > prevBlue ? currentBlue + blue : currentBlue - blue;

        currentRed = ensureRange(currentRed);
        currentGreen = ensureRange(currentGreen);
        currentBlue = ensureRange(currentBlue);

        return new Color(currentRed, currentGreen, currentBlue);
    }

    private float ensureRange(float value) {
        return Math.min(Math.max(value, 0.0f), 1.0f);
    }


}

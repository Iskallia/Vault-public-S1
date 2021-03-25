package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.Vault;
import iskallia.vault.block.entity.VendingMachineTileEntity;
import iskallia.vault.block.render.VendingMachineRenderer;
import iskallia.vault.client.gui.component.ScrollableContainer;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.client.gui.widget.TradeWidget;
import iskallia.vault.container.VendingMachineContainer;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.event.InputEvents;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VendingUIMessage;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.vending.Trade;
import iskallia.vault.vending.TraderCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

import java.util.LinkedList;
import java.util.List;

public class VendingMachineScreen extends ContainerScreen<VendingMachineContainer> {

    public static final ResourceLocation HUD_RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/vending-machine.png");

    public ScrollableContainer tradesContainer;
    public List<TradeWidget> tradeWidgets;

    public VendingMachineScreen(VendingMachineContainer screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, new StringTextComponent("Vending Machine"));

        tradesContainer = new ScrollableContainer(this::renderTrades);
        tradeWidgets = new LinkedList<>();

        refreshWidgets();

        imageWidth = 394;
        imageHeight = 170;
    }

    public void refreshWidgets() {
        tradeWidgets.clear();

        List<TraderCore> cores = getMenu().getTileEntity().getCores();

        for (int i = 0; i < cores.size(); i++) {
            TraderCore traderCore = cores.get(i);
            int x = 0;
            int y = i * TradeWidget.BUTTON_HEIGHT;
            tradeWidgets.add(new TradeWidget(x, y, traderCore, this));
        }
    }

    public Rectangle getTradeBoundaries() {
        float midX = width / 2f;
        float midY = height / 2f;

        Rectangle boundaries = new Rectangle();
        boundaries.x0 = (int) (midX - 134);
        boundaries.y0 = (int) (midY - 66);
        boundaries.setWidth(100);
        boundaries.setHeight(142);

        return boundaries;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        Rectangle tradeBoundaries = getTradeBoundaries();

        double tradeContainerX = mouseX - tradeBoundaries.x0;
        double tradeContainerY = mouseY - tradeBoundaries.y0;

        for (TradeWidget tradeWidget : tradeWidgets) {
            tradeWidget.mouseMoved(tradeContainerX, tradeContainerY);
        }

        tradesContainer.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Rectangle tradeBoundaries = getTradeBoundaries();

        double tradeContainerX = mouseX - tradeBoundaries.x0;
        double tradeContainerY = mouseY - tradeBoundaries.y0 + tradesContainer.getyOffset();

        for (int i = 0; i < tradeWidgets.size(); i++) {
            TradeWidget tradeWidget = tradeWidgets.get(i);
            boolean isHovered = tradeWidget.x <= tradeContainerX && tradeContainerX <= tradeWidget.x + TradeWidget.BUTTON_WIDTH
                    && tradeWidget.y <= tradeContainerY && tradeContainerY <= tradeWidget.y + TradeWidget.BUTTON_HEIGHT;

            if (isHovered) {
                if (InputEvents.isShiftDown()) {
                    getMenu().ejectCore(i);
                    refreshWidgets();
                    ModNetwork.CHANNEL.sendToServer(VendingUIMessage.ejectTrade(i));
                    Minecraft.getInstance().getSoundManager()
                            .play(SimpleSound.forUI(SoundEvents.ITEM_PICKUP, 1f));

                } else {
                    getMenu().selectTrade(i);
                    ModNetwork.CHANNEL.sendToServer(VendingUIMessage.selectTrade(i));
                    Minecraft.getInstance().getSoundManager()
                            .play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                }

                break;
            }
        }

        tradesContainer.mouseClicked(mouseX, mouseY, button);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        tradesContainer.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        tradesContainer.mouseScrolled(mouseX, mouseY, delta);
        return true;
    }

    @Override
    protected void
    renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) { }

    @Override
    protected void
    renderLabels(MatrixStack matrixStack, int x, int y) {
        // For some reason, without this it won't render :V
        this.font.draw(matrixStack,
                new StringTextComponent(""),
                (float) this.titleLabelX, (float) this.titleLabelY,
                4210752);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);

        float midX = width / 2f;
        float midY = height / 2f;

        Minecraft minecraft = getMinecraft();

        int containerWidth = 276;
        int containerHeight = 166;

        minecraft.getTextureManager().bind(HUD_RESOURCE);
        blit(matrixStack, (int) (midX - containerWidth / 2), (int) (midY - containerHeight / 2),
                0, 0, containerWidth, containerHeight,
                512, 256);

        VendingMachineContainer container = getMenu();
        VendingMachineTileEntity tileEntity = container.getTileEntity();
        Rectangle tradeBoundaries = getTradeBoundaries();

        tradesContainer.setBounds(tradeBoundaries);
        tradesContainer.setInnerHeight(TradeWidget.BUTTON_HEIGHT * tradeWidgets.size());

        tradesContainer.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        TraderCore lastCore = tileEntity.getLastCore();
        if (lastCore != null) {
//            drawSkin((int) midX - 175, (int) midY - 10, 45, tileEntity.getSkin(), lastCore.isMegahead());
            drawSkin((int) midX + 175, (int) midY - 10, -45, tileEntity.getSkin(), lastCore.isMegahead());
        }

        minecraft.font.draw(matrixStack,
                "Trades", midX - 108, midY - 77, 0xFF_3f3f3f);

        if (lastCore != null) {
            String name = "Vendor - " + lastCore.getName();
            int nameWidth = minecraft.font.width(name);
            minecraft.font.draw(matrixStack,
                    name,
                    midX + 50 - nameWidth / 2f,
                    midY - 70, 0xFF_3f3f3f);
        }

        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    public void
    renderTrades(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Rectangle tradeBoundaries = getTradeBoundaries();

        int tradeContainerX = mouseX - tradeBoundaries.x0;
        int tradeContainerY = mouseY - tradeBoundaries.y0 + tradesContainer.getyOffset();

        for (TradeWidget tradeWidget : tradeWidgets) {
            tradeWidget.render(matrixStack, tradeContainerX, tradeContainerY, partialTicks);
        }
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        Rectangle tradeBoundaries = getTradeBoundaries();

        int tradeContainerX = mouseX - tradeBoundaries.x0;
        int tradeContainerY = mouseY - tradeBoundaries.y0 + tradesContainer.getyOffset();

        for (TradeWidget tradeWidget : tradeWidgets) {
            if (tradeWidget.isHovered(tradeContainerX, tradeContainerY)) {
                Trade trade = tradeWidget.getTraderCode().getTrade();
                if (trade.getTradesLeft() != 0) {
                    ItemStack sellStack = trade.getSell().toStack();
                    renderTooltip(matrixStack, sellStack, mouseX, mouseY);
                } else {
                    StringTextComponent text = new StringTextComponent("Sold out, sorry!");
                    text.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FF0000)));
                    renderTooltip(matrixStack, text, mouseX, mouseY);
                }
            }
        }

        super.renderTooltip(matrixStack, mouseX, mouseY);
    }

    public static void drawSkin(int posX, int posY, int yRotation, SkinProfile skin, boolean megahead) {
        float scale = 8;
        float headScale = megahead ? 1.75f : 1f;
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) posX, (float) posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale((float) scale, (float) scale, (float) scale);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F + 20f);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(45f);
        quaternion.mul(quaternion1);
//        matrixStack.rotate(quaternion);
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderermanager.overrideCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        StatuePlayerModel<PlayerEntity> model = VendingMachineRenderer.PLAYER_MODEL;
        RenderSystem.runAsFancy(() -> {
            matrixStack.scale(scale, scale, scale);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(20));
            matrixStack.mulPose(Vector3f.YN.rotationDegrees(yRotation));
            int lighting = 0xf00000;
            int overlay = 0xf0000;
            RenderType renderType = model.renderType(skin.getLocationSkin());
            IVertexBuilder vertexBuilder = irendertypebuffer$impl.getBuffer(renderType);
            model.body.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.leftLeg.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.rightLeg.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.leftArm.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.rightArm.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);

            model.jacket.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.leftPants.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.rightPants.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.leftSleeve.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);

            matrixStack.pushPose();
            matrixStack.translate(0, 0, -0.62f);
            model.rightSleeve.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            matrixStack.popPose();

            matrixStack.scale(headScale, headScale, headScale);
            model.hat.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.head.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            matrixStack.popPose();
        });
        irendertypebuffer$impl.endBatch();
        entityrenderermanager.setRenderShadow(true);
        RenderSystem.popMatrix();
    }

}

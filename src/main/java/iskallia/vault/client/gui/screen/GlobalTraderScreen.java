package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.Vault;
import iskallia.vault.block.entity.GlobalTraderTileEntity;
import iskallia.vault.block.render.VendingMachineRenderer;
import iskallia.vault.client.gui.component.ScrollableContainer;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.client.gui.widget.GlobalTradeWidget;
import iskallia.vault.client.gui.widget.TradeWidget;
import iskallia.vault.container.GlobalTraderContainer;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VendingUIMessage;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.vending.Trade;
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

public class GlobalTraderScreen extends ContainerScreen<GlobalTraderContainer> {

    public static final ResourceLocation HUD_RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/vending-machine.png");

    public ScrollableContainer tradesContainer;
    public List<GlobalTradeWidget> tradeWidgets;

    public GlobalTraderScreen(GlobalTraderContainer screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, new StringTextComponent("Global Trader"));

        tradesContainer = new ScrollableContainer(this::renderTrades);
        tradeWidgets = new LinkedList<>();

        refreshWidgets();

        xSize = 394;
        ySize = 170;
    }

    public void refreshWidgets() {
        tradeWidgets.clear();

        List<Trade> trades = this.getContainer().getPlayerTrades();

        for (int i = 0; i < trades.size(); i++) {
            Trade trade = trades.get(i);
            int x = 0;
            int y = i * TradeWidget.BUTTON_HEIGHT;
            tradeWidgets.add(new GlobalTradeWidget(x, y, trade, this));
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

        for (GlobalTradeWidget tradeWidget : tradeWidgets) {
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
            GlobalTradeWidget tradeWidget = tradeWidgets.get(i);
            boolean isHovered = tradeWidget.x <= tradeContainerX && tradeContainerX <= tradeWidget.x + TradeWidget.BUTTON_WIDTH
                    && tradeWidget.y <= tradeContainerY && tradeContainerY <= tradeWidget.y + TradeWidget.BUTTON_HEIGHT;

            if (isHovered) {

                getContainer().selectTrade(i);
                ModNetwork.CHANNEL.sendToServer(VendingUIMessage.selectTrade(i));
                Minecraft.getInstance().getSoundHandler()
                        .play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1f));

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
    drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
    }

    @Override
    protected void
    drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        // For some reason, without this it won't render :V
        this.font.func_243248_b(matrixStack,
                new StringTextComponent(""),
                (float) this.titleX, (float) this.titleY,
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

        minecraft.getTextureManager().bindTexture(HUD_RESOURCE);
        blit(matrixStack, (int) (midX - containerWidth / 2), (int) (midY - containerHeight / 2),
                0, 0, containerWidth, containerHeight,
                512, 256);

        GlobalTraderContainer container = getContainer();
        GlobalTraderTileEntity tileEntity = container.getTileEntity();
        Rectangle tradeBoundaries = getTradeBoundaries();

        tradesContainer.setBounds(tradeBoundaries);
        tradesContainer.setInnerHeight(TradeWidget.BUTTON_HEIGHT * tradeWidgets.size());

        tradesContainer.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        drawSkin((int) midX + 175, (int) midY - 10, -45, tileEntity.getSkin(), false);


        minecraft.fontRenderer.drawString(matrixStack,
                "Trades", midX - 108, midY - 77, 0xFF_3f3f3f);

        String name = "Vendor - " + tileEntity.getSkin().getLatestNickname();
        int nameWidth = minecraft.fontRenderer.getStringWidth(name);
        minecraft.fontRenderer.drawString(matrixStack,
                name,
                midX + 50 - nameWidth / 2f,
                midY - 70, 0xFF_3f3f3f);

        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    public void
    renderTrades(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Rectangle tradeBoundaries = getTradeBoundaries();

        int tradeContainerX = mouseX - tradeBoundaries.x0;
        int tradeContainerY = mouseY - tradeBoundaries.y0 + tradesContainer.getyOffset();

        for (GlobalTradeWidget tradeWidget : tradeWidgets) {
            tradeWidget.render(matrixStack, tradeContainerX, tradeContainerY, partialTicks);
        }
    }

    @Override
    protected void renderHoveredTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        Rectangle tradeBoundaries = getTradeBoundaries();

        int tradeContainerX = mouseX - tradeBoundaries.x0;
        int tradeContainerY = mouseY - tradeBoundaries.y0 + tradesContainer.getyOffset();

        for (GlobalTradeWidget tradeWidget : tradeWidgets) {
            if (tradeWidget.isHovered(tradeContainerX, tradeContainerY)) {
                Trade trade = tradeWidget.getTrade();
                if (trade.getTradesLeft() != 0) {
                    ItemStack sellStack = trade.getSell().toStack();
                    renderTooltip(matrixStack, sellStack, mouseX, mouseY);
                } else {
                    StringTextComponent text = new StringTextComponent("Sold out, sorry!");
                    text.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_FF0000)));
                    renderTooltip(matrixStack, text, mouseX, mouseY);
                }
            }
        }

        super.renderHoveredTooltip(matrixStack, mouseX, mouseY);
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
        quaternion.multiply(quaternion1);
//        matrixStack.rotate(quaternion);
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        quaternion1.conjugate();
        entityrenderermanager.setCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        StatuePlayerModel<PlayerEntity> model = VendingMachineRenderer.PLAYER_MODEL;
        RenderSystem.runAsFancy(() -> {
            matrixStack.scale(scale, scale, scale);
            matrixStack.rotate(Vector3f.XP.rotationDegrees(20));
            matrixStack.rotate(Vector3f.YN.rotationDegrees(yRotation));
            int lighting = 0xf00000;
            int overlay = 0xf0000;
            RenderType renderType = model.getRenderType(skin.getLocationSkin());
            IVertexBuilder vertexBuilder = irendertypebuffer$impl.getBuffer(renderType);
            model.bipedBody.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.bipedLeftLeg.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.bipedRightLeg.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.bipedLeftArm.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.bipedRightArm.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);

            model.bipedBodyWear.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.bipedLeftLegwear.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.bipedRightLegwear.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.bipedLeftArmwear.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);

            matrixStack.push();
            matrixStack.translate(0, 0, -0.62f);
            model.bipedRightArmwear.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            matrixStack.pop();

            matrixStack.scale(headScale, headScale, headScale);
            model.bipedHeadwear.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            model.bipedHead.render(matrixStack, vertexBuilder, lighting, overlay, 1, 1, 1, 1);
            matrixStack.pop();
        });
        irendertypebuffer$impl.finish();
        entityrenderermanager.setRenderShadow(true);
        RenderSystem.popMatrix();
    }

}

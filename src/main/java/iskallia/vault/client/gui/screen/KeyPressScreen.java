package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.container.KeyPressContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class KeyPressScreen extends ContainerScreen<KeyPressContainer> {

    public static final ResourceLocation GUI_RESOURCE = Vault.id("textures/gui/key-press.png");

    public KeyPressScreen(KeyPressContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void
    drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {}

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
    public void
    render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);

        float midX = width / 2f;
        float midY = height / 2f;

        Minecraft minecraft = getMinecraft();

        int containerWidth = 176;
        int containerHeight = 166;

        minecraft.getTextureManager().bindTexture(GUI_RESOURCE);
        blit(matrixStack, (int) (midX - containerWidth / 2), (int) (midY - containerHeight / 2),
                0, 0, containerWidth, containerHeight);

        FontRenderer fontRenderer = minecraft.fontRenderer;

        String title = "Mold Vault Keys";
        fontRenderer.drawString(matrixStack, title,
                midX - 35,
                midY - 63,
                0x00_3f3f3f);

        String inventoryTitle = "Inventory";
        fontRenderer.drawString(matrixStack, inventoryTitle,
                midX - 80,
                midY - 11,
                0x00_3f3f3f);

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

}

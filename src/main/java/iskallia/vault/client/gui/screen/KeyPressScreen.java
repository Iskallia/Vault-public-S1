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

    private static final ResourceLocation GUI_RESOURCE = Vault.id("textures/gui/key-press.png");

    public KeyPressScreen(KeyPressContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void
    renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {}

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
    public void
    render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);

        float midX = width / 2f;
        float midY = height / 2f;

        Minecraft minecraft = getMinecraft();

        int containerWidth = 176;
        int containerHeight = 166;

        minecraft.getTextureManager().bind(GUI_RESOURCE);
        blit(matrixStack, (int) (midX - containerWidth / 2), (int) (midY - containerHeight / 2),
                0, 0, containerWidth, containerHeight);

        FontRenderer fontRenderer = minecraft.font;

        String title = "Mold Vault Keys";
        fontRenderer.draw(matrixStack, title,
                midX - 35,
                midY - 63,
                0x00_3f3f3f);

        String inventoryTitle = "Inventory";
        fontRenderer.draw(matrixStack, inventoryTitle,
                midX - 80,
                midY - 11,
                0x00_3f3f3f);

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

}

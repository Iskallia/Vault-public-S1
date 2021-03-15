package iskallia.vault.client.gui.helper;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;

public class FontHelper {

    public static void
    drawStringWithBorder(MatrixStack matrixStack, String text, float x, float y, int color, int borderColor) {
        Minecraft minecraft = Minecraft.getInstance();

        minecraft.fontRenderer.drawString(matrixStack, text, x - 1, y, borderColor);
        minecraft.fontRenderer.drawString(matrixStack, text, x + 1, y, borderColor);
        minecraft.fontRenderer.drawString(matrixStack, text, x, y - 1, borderColor);
        minecraft.fontRenderer.drawString(matrixStack, text, x, y + 1, borderColor);
        minecraft.fontRenderer.drawString(matrixStack, text, x, y, color);
    }

}

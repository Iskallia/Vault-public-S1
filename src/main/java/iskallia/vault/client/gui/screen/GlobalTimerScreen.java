package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.StringTextComponent;

import java.time.Instant;

public class GlobalTimerScreen extends Screen {

    public static final ResourceLocation UI_RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/global_timer.png");

    protected long endUnix;

    public GlobalTimerScreen(long endUnix) {
        super(new StringTextComponent("Global Timer"));
        this.endUnix = endUnix;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack, 0x00_000000);

        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(UI_RESOURCE);

        long now = Instant.now().getEpochSecond();
        long secondsLeft = endUnix - now;

        float midX = minecraft.getMainWindow().getScaledWidth() / 2f;
        float midY = minecraft.getMainWindow().getScaledHeight() / 2f;

        int containerWidth = 140;
        int containerHeight = 70;

        UIHelper.renderContainerBorder(this, matrixStack,
                (int) (midX - containerWidth / 2),
                (int) (midY - containerHeight / 2),
                containerWidth,
                containerHeight,
                1, 1, 5, 5, 5, 5, 0xFF_c6c6c6);

        String formattedTime = formatTimeLeft(secondsLeft);
        int formattedTimeLength = minecraft.fontRenderer.getStringWidth(formattedTime);

        String formattedSeconds = formatSecondsLeft(secondsLeft);
        int formattedSecondsLength = minecraft.fontRenderer.getStringWidth(formattedSeconds);

        String label = "Time left until the end...";
        int labelWidth = minecraft.fontRenderer.getStringWidth(label);
        minecraft.fontRenderer.drawString(matrixStack, label,
                midX - labelWidth / 2f, midY - 20, 0xFF_3f3f3f);

        matrixStack.push();
        matrixStack.translate(0, 5, 0);
        matrixStack.push();
        matrixStack.translate(midX - formattedSecondsLength / 2f, midY, 0);
        matrixStack.scale(2, 2, 2);
        FontHelper.drawStringWithBorder(matrixStack, formattedTime,
                -formattedTimeLength / 2f, -4,
                0xFF_FFFFFF, 0xFF_483121);
        matrixStack.pop();

        FontHelper.drawStringWithBorder(matrixStack, formattedSeconds,
                5 + midX + formattedTimeLength / 2f + 12, midY,
                0xFF_FFFFFF, 0xFF_483121);
        matrixStack.pop();

        minecraft.getTextureManager().bindTexture(UI_RESOURCE);

        int hourglassWidth = 12;
        int hourglassHeight = 16;

        matrixStack.push();
        matrixStack.translate(midX - containerWidth / 2f, midY, 0);
        matrixStack.scale(2, 2, 2);
        matrixStack.translate(-18, 0, 0);
        matrixStack.rotate(new Quaternion(0, 0, (System.currentTimeMillis() / 10L) % 360, true));
        blit(matrixStack, (int) (-hourglassWidth / 2f), (int) (-hourglassHeight / 2f),
                1, 15, hourglassWidth, hourglassHeight);
        matrixStack.pop();
    }

    public static String formatTimeLeft(long secondsLeft) {
        long minutesLeft = (secondsLeft / 60);
        long hoursLeft = (secondsLeft / (60 * 60));
        long daysLeft = (secondsLeft / (60 * 60 * 24));
        return String.format("%02d:%02d:%02d", daysLeft, hoursLeft % 24, minutesLeft % 60);
    }

    public static String formatSecondsLeft(long secondsLeft) {
        return String.format("%02d", secondsLeft % 60);
    }

}

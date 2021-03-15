package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.gui.helper.ConfettiParticles;
import iskallia.vault.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GiftBombOverlay {

    private static ConfettiParticles leftConfettiPopper = new ConfettiParticles()
            .angleRange(200 + 90, 265 + 90)
            .quantityRange(60, 80)
            .delayRange(0, 10)
            .lifespanRange(20, 20 * 5)
            .sizeRange(2, 5)
            .speedRange(2, 10);

    private static ConfettiParticles rightConfettiPopper = new ConfettiParticles()
            .angleRange(200, 265)
            .quantityRange(60, 80)
            .delayRange(0, 10)
            .lifespanRange(20, 20 * 5)
            .sizeRange(2, 5)
            .speedRange(2, 10);

    @OnlyIn(Dist.CLIENT)
    public static void pop() {
        Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(
                ModSounds.CONFETTI_SFX,
                1.0F
        ));
        leftConfettiPopper.pop();
        rightConfettiPopper.pop();
    }

    @SubscribeEvent
    public static void
    onPostRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR)
            return; // Render only on HOTBAR

        Minecraft minecraft = Minecraft.getInstance();
        MatrixStack matrixStack = event.getMatrixStack();

        int width = minecraft.getMainWindow().getScaledWidth();
        int height = minecraft.getMainWindow().getScaledHeight();

        int midX = width / 2;
        int midY = height / 2;

        leftConfettiPopper.spawnedPosition(
                10,
                midY
        );
        rightConfettiPopper.spawnedPosition(
                width - 10,
                midY
        );

        leftConfettiPopper.tick();
        rightConfettiPopper.tick();

        leftConfettiPopper.render(matrixStack);
        rightConfettiPopper.render(matrixStack);
    }

}

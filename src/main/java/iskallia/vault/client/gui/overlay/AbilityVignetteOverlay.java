package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.init.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AbilityVignetteOverlay {

    @SubscribeEvent
    public static void
    onPreRender(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL)
            return; // Render only on HOTBAR

        MatrixStack matrixStack = event.getMatrixStack();

        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();

        if (minecraft.player == null) return;

        if (minecraft.player.getEffect(ModEffects.RAMPAGE) != null) {
            int alpha = (int) (0x15 * (Math.sin(System.currentTimeMillis() / 250d) + 2)) << 24;
            AbstractGui.fill(matrixStack, 0, 0, width, height, alpha | 0x00_FF0000);

        } else if (minecraft.player.getEffect(ModEffects.GHOST_WALK) != null) {
            AbstractGui.fill(matrixStack, 0, 0, width, height, 0x20_ABEABE);

        }
    }

}

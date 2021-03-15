package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.Vault;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.init.ModSounds;
import iskallia.vault.world.raid.modifier.VaultModifiers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class VaultRaidOverlay {

    public static final ResourceLocation RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/vault-hud.png");
    public static final ResourceLocation NORMAL_RARITY = new ResourceLocation(Vault.MOD_ID, "textures/gui/modifiers/normal.png");
    public static final ResourceLocation RARE_RARITY = new ResourceLocation(Vault.MOD_ID, "textures/gui/modifiers/rare.png");
    public static final ResourceLocation EPIC_RARITY = new ResourceLocation(Vault.MOD_ID, "textures/gui/modifiers/epic.png");
    public static final ResourceLocation OMEGA_RARITY = new ResourceLocation(Vault.MOD_ID, "textures/gui/modifiers/omega.png");

    public static int currentRarity;

    public static int remainingTicks;

    public static SimpleSound panicSound;
    public static SimpleSound ambientLoop;
    public static SimpleSound ambientSound;
    public static SimpleSound bossLoop;

    public static boolean bossSummoned;
    private static int ticksBeforeAmbientSound;

    public static void startBossLoop() {
        if (bossLoop != null) stopBossLoop();
        Minecraft minecraft = Minecraft.getInstance();
        bossLoop = SimpleSound.ambientWithoutAttenuation(ModSounds.VAULT_BOSS_LOOP, 0.75f, 1f);
        minecraft.getSoundHandler().play(bossLoop);
    }

    public static void stopBossLoop() {
        if (bossLoop == null) return;
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getSoundHandler().stop(bossLoop);
        bossLoop = null;
    }

    @SubscribeEvent
    public static void
    onPostRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.POTION_ICONS)
            return; // Render only on HOTBAR

        Minecraft minecraft = Minecraft.getInstance();

        boolean inVault = minecraft.world.getDimensionKey() == Vault.VAULT_KEY;

        if (minecraft.world == null || (!inVault)) {
            if (inVault) stopBossLoop();
            bossSummoned = false;
            return;
        }

        if (remainingTicks == 0)
            return; // Timed out, stop here

        MatrixStack matrixStack = event.getMatrixStack();
        int bottom = minecraft.getMainWindow().getScaledHeight();
        int barWidth = 62;
        int barHeight = 22;
        int panicTicks = 30 * 20;

        if (!bossSummoned) {
            if (inVault) stopBossLoop();
        } else if (!minecraft.getSoundHandler().isPlaying(bossLoop)) {
            if (inVault) startBossLoop();
        }

        matrixStack.push();
        matrixStack.translate(barWidth, bottom, 0);
        FontHelper.drawStringWithBorder(matrixStack,
                formatTimeString(),
                18, -12,
                remainingTicks < panicTicks
                        && remainingTicks % 10 < 5
                        ? 0xFF_FF0000
                        : 0xFF_FFFFFF,
                0xFF_000000);

        matrixStack.translate(30, -25, 0);

        if (remainingTicks < panicTicks)
            matrixStack.rotate(new Quaternion(0, 0, (remainingTicks * 10f) % 360, true));
        else
            matrixStack.rotate(new Quaternion(0, 0, (float) remainingTicks % 360, true));

        minecraft.getTextureManager().bindTexture(RESOURCE);
        RenderSystem.enableBlend();
        int hourglassWidth = 12;
        int hourglassHeight = 16;
        matrixStack.translate(-hourglassWidth / 2f, -hourglassHeight / 2f, 0);

        minecraft.ingameGUI.blit(matrixStack,
                0, 0,
                1, 36,
                hourglassWidth, hourglassHeight
        );

        matrixStack.pop();

        if (inVault) {
            if (bossSummoned && ambientLoop != null && minecraft.getSoundHandler().isPlaying(ambientLoop)) {
                minecraft.getSoundHandler().stop(ambientLoop);
            }

            if (ambientLoop == null || !minecraft.getSoundHandler().isPlaying(ambientLoop)) {
                if (!bossSummoned) {
                    ambientLoop = SimpleSound.music(ModSounds.VAULT_AMBIENT_LOOP);
                    minecraft.getSoundHandler().play(ambientLoop);
                }
            }

            if (ticksBeforeAmbientSound < 0) {
                if (ambientSound == null || !minecraft.getSoundHandler().isPlaying(ambientSound)) {
                    ambientSound = SimpleSound.ambient(ModSounds.VAULT_AMBIENT);
                    minecraft.getSoundHandler().play(ambientSound);
                    ticksBeforeAmbientSound = 60 * 60;
                }
            }

            ticksBeforeAmbientSound--;
        }

        renderVaultModifiers(event);

        if (remainingTicks < panicTicks) {
            if (panicSound == null || !minecraft.getSoundHandler().isPlaying(panicSound)) {
                panicSound = SimpleSound.master(
                        ModSounds.TIMER_PANIC_TICK_SFX,
                        2.0f - ((float) remainingTicks / panicTicks)
                );
                minecraft.getSoundHandler().play(panicSound);
            }
        }
    }

    public static void
    renderVaultModifiers(RenderGameOverlayEvent.Post event) {
        if (VaultModifiers.CLIENT == null) return;

        Minecraft minecraft = Minecraft.getInstance();
        MatrixStack matrixStack = event.getMatrixStack();

        int right = minecraft.getMainWindow().getScaledWidth();
        int bottom = minecraft.getMainWindow().getScaledHeight();

        int rightMargin = 28;
        int raritySize = 16 + 8;
        int modifierSize = 16 + 8;
        int modifierGap = 2;

        int xPosition = right - rightMargin;

        matrixStack.push();

        matrixStack.push();
        matrixStack.translate(right - 1, bottom - 96, 0);
        minecraft.getTextureManager().bindTexture(
                currentRarity == 0 ? NORMAL_RARITY
                        : currentRarity == 1 ? RARE_RARITY
                        : currentRarity == 2 ? EPIC_RARITY
                        : currentRarity == 3 ? OMEGA_RARITY
                        : NORMAL_RARITY
        );
        AbstractGui.blit(matrixStack,
                -raritySize, -raritySize - 3,
                0, 0, raritySize, raritySize, raritySize, raritySize);
        matrixStack.pop();

        VaultModifiers.CLIENT.forEach((index, modifier) -> {
            minecraft.getTextureManager().bindTexture(modifier.getIcon());
            AbstractGui.blit(matrixStack,
                    right - (rightMargin + modifierSize), bottom - modifierSize - 2,
                    0, 0, modifierSize, modifierSize, modifierSize, modifierSize);
            matrixStack.translate(-(modifierGap + modifierSize), 0, 0);
        });

        matrixStack.pop();
    }

    public static String formatTimeString() {
        long seconds = (remainingTicks / 20) % 60;
        long minutes = ((remainingTicks / 20) / 60) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}

package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.Vault;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AbilitiesOverlay {

    public static final ResourceLocation HUD_RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/vault-hud.png");
    private static final ResourceLocation ABILITIES_RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/abilities.png");

    public static List<AbilityNode<?>> learnedAbilities;
    public static Map<Integer, Integer> cooldowns = new HashMap<>();
    public static int focusedIndex;
    public static boolean active;

    @SubscribeEvent
    public static void
    onPostRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR)
            return; // Render only on HOTBAR

        if (learnedAbilities == null || learnedAbilities.size() == 0)
            return; // Render only if there are any learned abilities

        int previousIndex = (focusedIndex - 1);
        if (previousIndex < 0)
            previousIndex += learnedAbilities.size();

        int nextIndex = (focusedIndex + 1);
        if (nextIndex >= learnedAbilities.size())
            nextIndex -= learnedAbilities.size();

        MatrixStack matrixStack = event.getMatrixStack();
        Minecraft minecraft = Minecraft.getInstance();
        int bottom = minecraft.getMainWindow().getScaledHeight();
        int barWidth = 62;
        int barHeight = 22;

        minecraft.getProfiler().startSection("abilityBar");
        matrixStack.push();

        RenderSystem.enableBlend();
        matrixStack.translate(10, bottom - barHeight, 0);

        minecraft.getTextureManager().bindTexture(HUD_RESOURCE);
        minecraft.ingameGUI.blit(matrixStack,
                0, 0,
                1, 13, barWidth, barHeight);

        minecraft.getTextureManager().bindTexture(ABILITIES_RESOURCE);
        AbilityNode<?> focusedAbility = learnedAbilities.get(focusedIndex);
        SkillStyle focusedStyle = ModConfigs.ABILITIES_GUI.getStyles().get(focusedAbility.getGroup().getParentName());
        GlStateManager.color4f(1, 1, 1, cooldowns.getOrDefault(focusedIndex, 0) > 0 ? 0.4f : 1);
        minecraft.ingameGUI.blit(matrixStack,
                23, 3,
                focusedStyle.u, focusedStyle.v,
                16, 16);

        if (cooldowns.getOrDefault(focusedIndex, 0) > 0) {
            float cooldownPercent = (float) cooldowns.get(focusedIndex) / ModConfigs.ABILITIES.cooldownOf(focusedAbility, minecraft.player);
            int cooldownHeight = (int) (16 * cooldownPercent);
            AbstractGui.fill(matrixStack,
                    23, 3 + (16 - cooldownHeight),
                    23 + 16, 3 + 16,
                    0x99_FFFFFF);
            RenderSystem.enableBlend();
        }

        GlStateManager.color4f(0.7f, 0.7f, 0.7f, 0.5f);
        AbilityNode<?> previousAbility = learnedAbilities.get(previousIndex);
        if (cooldowns.getOrDefault(previousIndex, 0) > 0) {
            float cooldownPercent = (float) cooldowns.get(previousIndex) / ModConfigs.ABILITIES.cooldownOf(previousAbility, minecraft.player);
            int cooldownHeight = (int) (16 * cooldownPercent);
            AbstractGui.fill(matrixStack,
                    43, 3 + (16 - cooldownHeight),
                    43 + 16, 3 + 16,
                    0x99_FFFFFF);
            RenderSystem.enableBlend();
        }
        SkillStyle previousStyle = ModConfigs.ABILITIES_GUI.getStyles().get(previousAbility.getGroup().getParentName());
        minecraft.ingameGUI.blit(matrixStack,
                43, 3,
                previousStyle.u, previousStyle.v,
                16, 16);

        AbilityNode<?> nextAbility = learnedAbilities.get(nextIndex);
        if (cooldowns.getOrDefault(nextIndex, 0) > 0) {
            float cooldownPercent = (float) cooldowns.get(nextIndex) / ModConfigs.ABILITIES.cooldownOf(nextAbility, minecraft.player);
            int cooldownHeight = (int) (16 * cooldownPercent);
            AbstractGui.fill(matrixStack,
                    3, 3 + (16 - cooldownHeight),
                    3 + 16, 3 + 16,
                    0x99_FFFFFF);
            RenderSystem.enableBlend();
        }
        SkillStyle nextStyle = ModConfigs.ABILITIES_GUI.getStyles().get(nextAbility.getGroup().getParentName());
        minecraft.ingameGUI.blit(matrixStack,
                3, 3,
                nextStyle.u, nextStyle.v,
                16, 16);

        minecraft.getTextureManager().bindTexture(HUD_RESOURCE);
        GlStateManager.color4f(1, 1, 1, 1);
        minecraft.ingameGUI.blit(matrixStack,
                19, -1,
                64 + (cooldowns.getOrDefault(focusedIndex, 0) > 0 ? 50 : active ? 25 : 0),
                13,
                24, 24);

        matrixStack.pop();
        minecraft.getProfiler().endSection();
    }

}

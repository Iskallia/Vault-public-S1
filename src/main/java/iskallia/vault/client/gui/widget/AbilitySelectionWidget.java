package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.Vault;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.client.gui.overlay.AbilitiesOverlay;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.util.MathUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.StringTextComponent;

public class AbilitySelectionWidget extends Widget {

    public static final ResourceLocation HUD_RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/vault-hud.png");
    private static final ResourceLocation ABILITIES_RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/abilities.png");

    protected AbilityNode<?> abilityNode;
    protected double angleBoundary;

    public AbilitySelectionWidget(int x, int y, AbilityNode<?> abilityNode, double angleBoundary) {
        super(x, y, 24, 24, new StringTextComponent(abilityNode.getName()));
        this.abilityNode = abilityNode;
        this.angleBoundary = angleBoundary;
    }

    public AbilityNode<?> getAbilityNode() {
        return abilityNode;
    }

    public Rectangle getBounds() {
        Rectangle bounds = new Rectangle();
        bounds.x0 = x - 12;
        bounds.y0 = y - 12;
        bounds.setWidth(width);
        bounds.setHeight(height);
        return bounds;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();

        float midX = minecraft.getWindow().getGuiScaledWidth() / 2f;
        float midY = minecraft.getWindow().getGuiScaledHeight() / 2f;

        Vector2f towardsWidget = new Vector2f(x - midX, y - midY);
        Vector2f towardsMouse = new Vector2f((float) mouseX - midX, (float) (mouseY - midY));

        double dot = (towardsWidget.x * towardsMouse.x) + (towardsWidget.y * towardsMouse.y);
        double angleBetween = Math.acos(dot / (MathUtilities.length(towardsWidget) * MathUtilities.length(towardsMouse)));

        return angleBetween < angleBoundary;
//        return getBounds().contains((int) mouseX, (int) mouseY);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Rectangle bounds = getBounds();

        Minecraft minecraft = Minecraft.getInstance();

        SkillStyle abilityStyle = ModConfigs.ABILITIES_GUI.getStyles()
                .get(abilityNode.getGroup().getParentName());

        int abilityIndex = AbilitiesOverlay.learnedAbilities.indexOf(abilityNode);
        int cooldown = AbilitiesOverlay.cooldowns.getOrDefault(abilityIndex, 0);

        if (AbilitiesOverlay.focusedIndex == abilityIndex) {
            GlStateManager._color4f(0.7f, 0.7f, 0.7f, 0.3f);

        } else {
            GlStateManager._color4f(1f, 1f, 1f, 1f);
        }

        RenderSystem.enableBlend();

        minecraft.getTextureManager().bind(HUD_RESOURCE);
        blit(matrixStack, bounds.x0 + 1, bounds.y0 + 1,
                28, 36, 22, 22);

        minecraft.getTextureManager().bind(ABILITIES_RESOURCE);
        blit(matrixStack, bounds.x0 + 4, bounds.y0 + 4,
                abilityStyle.u, abilityStyle.v, 16, 16);

        if (cooldown > 0) {
            GlStateManager._color4f(0.7f, 0.7f, 0.7f, 0.5f);
            float cooldownPercent = (float) cooldown / ModConfigs.ABILITIES.cooldownOf(abilityNode, minecraft.player);
            int cooldownHeight = (int) (16 * cooldownPercent);
            AbstractGui.fill(matrixStack,
                    bounds.x0 + 4, bounds.y0 + 4 + (16 - cooldownHeight),
                    bounds.x0 + 4 + 16, bounds.y0 + 4 + 16,
                    0x99_FFFFFF);
            RenderSystem.enableBlend();
        }

        if (AbilitiesOverlay.focusedIndex == abilityIndex) {
            minecraft.getTextureManager().bind(HUD_RESOURCE);
            blit(matrixStack, bounds.x0, bounds.y0,
                    64 + 25,
                    13,
                    24, 24);

        } else if (isMouseOver(mouseX, mouseY)) {
            GlStateManager._color4f(1f, 1f, 1f, 1f);
            minecraft.getTextureManager().bind(HUD_RESOURCE);
            blit(matrixStack, bounds.x0, bounds.y0,
                    64 + (cooldown > 0 ? 50 : 0),
                    13,
                    24, 24);
        }
    }

}

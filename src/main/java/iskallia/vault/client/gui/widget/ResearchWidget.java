package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.util.ResourceBoundary;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class ResearchWidget extends Widget {

    private static final int ICON_SIZE = 30; // px

    private static final ResourceLocation SKILL_WIDGET_RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/skill-widget.png");
    private static final ResourceLocation RESEARCHES_RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/researches.png");

    String researchName;
    ResearchTree researchTree;
    boolean locked;
    SkillStyle style;

    boolean selected;

    public ResearchWidget(String researchName, ResearchTree researchTree, SkillStyle style) {
        super(style.x, style.y,
                ICON_SIZE, ICON_SIZE,
                new StringTextComponent("the_vault.widgets.research"));
        this.style = style;
        this.locked = ModConfigs.SKILL_GATES.getGates().isLocked(researchName, researchTree);
        this.researchName = researchName;
        this.researchTree = researchTree;
    }

    public ResearchTree getResearchTree() {
        return researchTree;
    }

    public String getResearchName() {
        return researchName;
    }

    public Rectangle getClickableBounds() {
        Rectangle bounds = new Rectangle();
        bounds.x0 = x - ICON_SIZE / 2;
        bounds.y0 = y - ICON_SIZE / 2;
        bounds.x1 = x + ICON_SIZE / 2;
        bounds.y1 = y + ICON_SIZE / 2;
        return bounds;
    }

    /* ----------------------------------------- */

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        Rectangle clickableBounds = getClickableBounds();
        return clickableBounds.x0 <= mouseX && mouseX <= clickableBounds.x1
                && clickableBounds.y0 <= mouseY && mouseY <= clickableBounds.y1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        if (button == 1) return false;
        if (locked) return false;
        if (selected) return false;

        this.playDownSound(Minecraft.getInstance().getSoundHandler());
        return true;
    }

    public void select() {
        this.selected = true;
    }

    public void deselect() {
        this.selected = false;
    }

    @Override
    public void
    render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderIcon(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void
    renderIcon(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        ResourceBoundary resourceBoundary = style.frameType.getResourceBoundary();

        matrixStack.push();
        matrixStack.translate(-ICON_SIZE / 2f, -ICON_SIZE / 2f, 0);
        Minecraft.getInstance().textureManager.bindTexture(resourceBoundary.getResource());

        int vOffset = locked ? 62
                : selected || isMouseOver(mouseX, mouseY) ? -31
                : researchTree.getResearchesDone().contains(researchName) ? 31 : 0;
        blit(matrixStack, this.x, this.y,
                resourceBoundary.getU(),
                resourceBoundary.getV() + vOffset,
                resourceBoundary.getW(),
                resourceBoundary.getH());
        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(-16 / 2f, -16 / 2f, 0);
        Minecraft.getInstance().textureManager.bindTexture(locked ? SKILL_WIDGET_RESOURCE : RESEARCHES_RESOURCE);
        if (locked) {
            blit(matrixStack, this.x + 3, this.y + 1,
                    10, 124, 10, 14);
        } else {
            blit(matrixStack, this.x, this.y,
                    style.u, style.v,
                    16, 16);
        }
        matrixStack.pop();
    }

}

package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.util.ResourceBoundary;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class TalentWidget extends Widget {

    private static final int PIP_SIZE = 8; //px
    private static final int GAP_SIZE = 2; //px
    private static final int ICON_SIZE = 30; // px
    private static final int MAX_PIPs_INLINE = 4;

    private static final ResourceLocation SKILL_WIDGET_RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/skill-widget.png");
    private static final ResourceLocation TALENTS_RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/talents.png");

    TalentGroup<?> talentGroup;
    TalentTree talentTree;
    boolean locked;
    SkillStyle style;

    boolean selected;

    public TalentWidget(TalentGroup<?> talentGroup, TalentTree talentTree, SkillStyle style) {
        super(style.x, style.y,
                5 * PIP_SIZE + 4 * GAP_SIZE,
                pipRowCount(talentTree.getNodeOf(talentGroup).getLevel()) * (PIP_SIZE + GAP_SIZE) - GAP_SIZE,
                new StringTextComponent("the_vault.widgets.talent"));
        this.style = style;
        this.talentGroup = talentGroup;
        this.talentTree = talentTree;
        this.locked = ModConfigs.SKILL_GATES.getGates().isLocked(talentGroup, talentTree);
        this.selected = false;
    }

    public TalentGroup<?> getTalentGroup() {
        return talentGroup;
    }

    public TalentTree getTalentTree() {
        return talentTree;
    }

    public int getClickableWidth() {
        int onlyIconWidth = ICON_SIZE + 2 * GAP_SIZE;
        int pipLineWidth = Math.min(talentGroup.getMaxLevel(), MAX_PIPs_INLINE) * (PIP_SIZE + GAP_SIZE);
        return hasPips()
                ? Math.max(pipLineWidth, onlyIconWidth)
                : onlyIconWidth;
    }

    public int getClickableHeight() {
        int height = 2 * GAP_SIZE + ICON_SIZE;
        if (hasPips()) {
            int lines = pipRowCount(talentGroup.getMaxLevel());
            height += GAP_SIZE;
            height += lines * PIP_SIZE + (lines - 1) * GAP_SIZE;
        }
        return height;
    }

    public Rectangle getClickableBounds() {
        Rectangle bounds = new Rectangle();
        bounds.x0 = x - getClickableWidth() / 2;
        bounds.y0 = y - (ICON_SIZE / 2) - GAP_SIZE;
        bounds.x1 = bounds.x0 + getClickableWidth();
        bounds.y1 = bounds.y0 + getClickableHeight();
        return bounds;
    }

    public boolean hasPips() {
        return !locked && talentGroup.getMaxLevel() > 1;
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

    /* ----------------------------------------- */

    @Override
    public void
    render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderIcon(matrixStack, mouseX, mouseY, partialTicks);
        if (hasPips()) renderPips(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void
    renderIcon(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        ResourceBoundary resourceBoundary = style.frameType.getResourceBoundary();

        matrixStack.push();
        matrixStack.translate(-ICON_SIZE / 2f, -ICON_SIZE / 2f, 0);
        Minecraft.getInstance().textureManager.bindTexture(resourceBoundary.getResource());

        int vOffset = locked ? 62
                : selected || isMouseOver(mouseX, mouseY) ? -31
                : talentTree.getNodeOf(talentGroup).getLevel() >= 1 ? 31 : 0;
        blit(matrixStack, this.x, this.y,
                resourceBoundary.getU(),
                resourceBoundary.getV() + vOffset,
                resourceBoundary.getW(),
                resourceBoundary.getH());
        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(-16 / 2f, -16 / 2f, 0);
        Minecraft.getInstance().textureManager.bindTexture(locked ? SKILL_WIDGET_RESOURCE : TALENTS_RESOURCE);
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

    public void
    renderPips(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().textureManager.bindTexture(SKILL_WIDGET_RESOURCE);

        int rowCount = pipRowCount(talentGroup.getMaxLevel());
        int remainingPips = talentGroup.getMaxLevel();
        int remainingFilledPips = talentTree.getNodeOf(talentGroup).getLevel();
        for (int r = 0; r < rowCount; r++) {
            renderPipLine(matrixStack,
                    x,
                    y + (ICON_SIZE / 2) + (2 * GAP_SIZE) + r * (GAP_SIZE + PIP_SIZE),
                    Math.min(MAX_PIPs_INLINE, remainingPips),
                    Math.min(MAX_PIPs_INLINE, remainingFilledPips)
            );
            remainingPips -= MAX_PIPs_INLINE;
            remainingFilledPips -= MAX_PIPs_INLINE;
        }
    }

    public void
    renderPipLine(MatrixStack matrixStack, int x, int y, int count, int filledCount) {
        int lineWidth = count * PIP_SIZE + (count - 1) * GAP_SIZE;
        int remainingFilled = filledCount;

        matrixStack.push();
        matrixStack.translate(x, y, 0);
        matrixStack.translate(-lineWidth / 2f, -PIP_SIZE / 2f, 0);

        for (int i = 0; i < count; i++) {
            if (remainingFilled > 0) {
                blit(matrixStack, 0, 0,
                        1, 133, 8, 8);
                remainingFilled--;

            } else {
                blit(matrixStack, 0, 0,
                        1, 124, 8, 8);
            }
            matrixStack.translate(PIP_SIZE + GAP_SIZE, 0, 0);
        }

        matrixStack.pop();
    }

    public static int
    pipRowCount(int level) {
        return (int) Math.ceil((float) level / MAX_PIPs_INLINE);
    }

}

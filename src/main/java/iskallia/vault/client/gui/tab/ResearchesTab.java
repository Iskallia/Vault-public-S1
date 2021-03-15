package iskallia.vault.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.widget.ResearchWidget;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.StringTextComponent;

import java.util.LinkedList;
import java.util.List;

public class ResearchesTab extends SkillTab {

    private List<ResearchWidget> researchWidgets;
    private ResearchWidget selectedWidget;

    public ResearchesTab(SkillTreeScreen parentScreen) {
        super(parentScreen, new StringTextComponent("Researches Tab"));
        this.researchWidgets = new LinkedList<>();
    }

    @Override
    public void refresh() {
        this.researchWidgets.clear();

        ResearchTree researchTree = parentScreen.getContainer().getResearchTree();
        ModConfigs.RESEARCHES_GUI.getStyles().forEach((researchName, style) -> {
            this.researchWidgets.add(new ResearchWidget(
                    researchName,
                    researchTree,
                    style
            ));
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean mouseClicked = super.mouseClicked(mouseX, mouseY, button);

        Vector2f midpoint = parentScreen.getContainerBounds().midpoint();
        int containerMouseX = (int) ((mouseX - midpoint.x) / viewportScale - viewportTranslation.x);
        int containerMouseY = (int) ((mouseY - midpoint.y) / viewportScale - viewportTranslation.y);

        for (ResearchWidget researchWidget : researchWidgets) {
            if (researchWidget.isMouseOver(containerMouseX, containerMouseY)
                    && researchWidget.mouseClicked(containerMouseX, containerMouseY, button)) {
                if (this.selectedWidget != null) this.selectedWidget.deselect();
                this.selectedWidget = researchWidget;
                this.selectedWidget.select();
                parentScreen.getResearchDialog().setResearchName(this.selectedWidget.getResearchName());
                break;
            }
        }

        return mouseClicked;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.enableBlend();

        Vector2f midpoint = parentScreen.getContainerBounds().midpoint();

        matrixStack.push();
        matrixStack.translate(midpoint.x, midpoint.y, 0);
        matrixStack.scale(viewportScale, viewportScale, 1);
        matrixStack.translate(viewportTranslation.x, viewportTranslation.y, 0);

        int containerMouseX = (int) ((mouseX - midpoint.x) / viewportScale - viewportTranslation.x);
        int containerMouseY = (int) ((mouseY - midpoint.y) / viewportScale - viewportTranslation.y);

        for (ResearchWidget researchWidget : researchWidgets) {
            researchWidget.render(matrixStack, containerMouseX, containerMouseY, partialTicks);
        }

        matrixStack.pop();
    }

}

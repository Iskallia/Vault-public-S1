package iskallia.vault.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.widget.TalentWidget;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentTree;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.StringTextComponent;

import java.util.LinkedList;
import java.util.List;

public class TalentsTab extends SkillTab {

    private List<TalentWidget> talentWidgets;
    private TalentWidget selectedWidget;

    public TalentsTab(SkillTreeScreen parentScreen) {
        super(parentScreen, new StringTextComponent("Talents Tab"));
        this.talentWidgets = new LinkedList<>();
    }

    public void refresh() {
        this.talentWidgets.clear();

        TalentTree talentTree = parentScreen.getMenu().getTalentTree();
        ModConfigs.TALENTS_GUI.getStyles().forEach((abilityName, style) -> {
            this.talentWidgets.add(new TalentWidget(
                    ModConfigs.TALENTS.getByName(abilityName),
                    talentTree,
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
        for (TalentWidget abilityWidget : talentWidgets) {
            if (abilityWidget.isMouseOver(containerMouseX, containerMouseY)
                    && abilityWidget.mouseClicked(containerMouseX, containerMouseY, button)) {
                if (this.selectedWidget != null) this.selectedWidget.deselect();
                this.selectedWidget = abilityWidget;
                this.selectedWidget.select();
                parentScreen.getTalentDialog().setTalentGroup(this.selectedWidget.getTalentGroup());
                break;
            }
        }

        return mouseClicked;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.enableBlend();

        Vector2f midpoint = parentScreen.getContainerBounds().midpoint();

        matrixStack.pushPose();
        matrixStack.translate(midpoint.x, midpoint.y, 0);
        matrixStack.scale(viewportScale, viewportScale, 1);
        matrixStack.translate(viewportTranslation.x, viewportTranslation.y, 0);

        int containerMouseX = (int) ((mouseX - midpoint.x) / viewportScale - viewportTranslation.x);
        int containerMouseY = (int) ((mouseY - midpoint.y) / viewportScale - viewportTranslation.y);

        for (TalentWidget abilityWidget : talentWidgets) {
            abilityWidget.render(matrixStack, containerMouseX, containerMouseY, partialTicks);
        }

        matrixStack.popPose();
    }

}

package iskallia.vault.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.widget.AbilityWidget;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityTree;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.StringTextComponent;

import java.util.LinkedList;
import java.util.List;

public class AbilitiesTab extends SkillTab {

    private List<AbilityWidget> abilityWidgets;
    private AbilityWidget selectedWidget;

    public AbilitiesTab(SkillTreeScreen parentScreen) {
        super(parentScreen, new StringTextComponent("Abilities Tab"));
        this.abilityWidgets = new LinkedList<>();
    }

    public void refresh() {
        this.abilityWidgets.clear();

        AbilityTree abilityTree = parentScreen.getMenu().getAbilityTree();
        ModConfigs.ABILITIES_GUI.getStyles().forEach((abilityName, style) -> {
            this.abilityWidgets.add(new AbilityWidget(
                    ModConfigs.ABILITIES.getByName(abilityName),
                    abilityTree,
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
        for (AbilityWidget abilityWidget : abilityWidgets) {
            if (abilityWidget.isMouseOver(containerMouseX, containerMouseY)
                    && abilityWidget.mouseClicked(containerMouseX, containerMouseY, button)) {
                if (this.selectedWidget != null) this.selectedWidget.deselect();
                this.selectedWidget = abilityWidget;
                this.selectedWidget.select();
                parentScreen.getAbilityDialog().setAbilityGroup(this.selectedWidget.getAbilityGroup());
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

        for (AbilityWidget abilityWidget : abilityWidgets) {
            abilityWidget.render(matrixStack, containerMouseX, containerMouseY, partialTicks);
        }

        matrixStack.popPose();
    }

}

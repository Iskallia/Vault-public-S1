package iskallia.vault.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.widget.ResearchWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.ResearchMessage;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.skill.talent.TalentTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ResearchDialog extends AbstractGui {

    private Rectangle bounds;
    private String researchName;
    private ResearchTree researchTree;
    private TalentTree talentTree;

    private ResearchWidget researchWidget;
    private ScrollableContainer descriptionComponent;
    private Button researchButton;

    public ResearchDialog(ResearchTree researchTree, TalentTree talentTree) {
        this.researchName = null;
        this.researchTree = researchTree;
        this.talentTree = talentTree;
        refreshWidgets();
    }

    public void refreshWidgets() {
        if (this.researchName != null) {
            SkillStyle researchStyle = ModConfigs.RESEARCHES_GUI.getStyles()
                    .get(researchName);
            this.researchWidget = new ResearchWidget(researchName, researchTree, researchStyle);

            Research research = ModConfigs.RESEARCHES.getByName(researchName);

            String buttonText = researchTree.isResearched(researchName) ? "Researched"
                    : "Research (" + research.getCost() + ")";

            this.researchButton = new Button(
                    10, bounds.getHeight() - 40,
                    bounds.getWidth() - 30, 20,
                    new StringTextComponent(buttonText),
                    (button) -> { research(); },
                    (button, matrixStack, x, y) -> {}
            );

            this.descriptionComponent = new ScrollableContainer(this::renderDescriptions);

            this.researchButton.active = !researchTree.isResearched(researchName)
                    && (research.usesKnowledge()
                    ? VaultBarOverlay.unspentKnowledgePoints >= research.getCost()
                    : VaultBarOverlay.unspentSkillPoints >= research.getCost());
        }
    }

    public void setResearchName(String researchName) {
        this.researchName = researchName;
        refreshWidgets();
    }

    public ResearchDialog setBounds(Rectangle bounds) {
        this.bounds = bounds;
        return this;
    }

    public Rectangle getHeadingBounds() {
        Rectangle researchBounds = researchWidget.getClickableBounds();
        Rectangle headingBounds = new Rectangle();
        headingBounds.x0 = 5;
        headingBounds.y0 = 5;
        headingBounds.x1 = headingBounds.x0 + bounds.getWidth() - 20;
        headingBounds.y1 = headingBounds.y0 + researchBounds.getHeight() + 5;
        return headingBounds;
    }

    public Rectangle getDescriptionsBounds() {
        Rectangle headingBounds = getHeadingBounds();
        Rectangle descriptionsBounds = new Rectangle();
        descriptionsBounds.x0 = headingBounds.x0;
        descriptionsBounds.y0 = headingBounds.y1 + 10;
        descriptionsBounds.x1 = headingBounds.x1;
        descriptionsBounds.y1 = bounds.getHeight() - 50;
        return descriptionsBounds;
    }

    public void research() {
        Research research = ModConfigs.RESEARCHES.getByName(researchName);

        int cost = research.getCost();
        int unspentPoints = research.usesKnowledge()
                ? VaultBarOverlay.unspentKnowledgePoints
                : VaultBarOverlay.unspentSkillPoints;

        if (cost > unspentPoints)
            return;

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null) {
            minecraft.player.playSound(
                    ModSounds.SKILL_TREE_LEARN_SFX,
                    1f, 1f
            );
        }

        researchTree.research(researchName);
        refreshWidgets();

        ModNetwork.CHANNEL.sendToServer(new ResearchMessage(researchName));
    }

    public void mouseMoved(int screenX, int screenY) {
        if (bounds == null) return;

        int containerX = screenX - bounds.x0;
        int containerY = screenY - bounds.y0;

        if (this.researchButton != null) {
            this.researchButton.mouseMoved(containerX, containerY);
        }
    }

    public void mouseClicked(int screenX, int screenY, int button) {
        int containerX = screenX - bounds.x0;
        int containerY = screenY - bounds.y0;

        if (this.researchButton != null) {
            this.researchButton.mouseClicked(containerX, containerY, button);
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!bounds.contains((int) mouseX, (int) mouseY)) return;
        descriptionComponent.mouseScrolled(mouseX, mouseY, delta);
    }

    public void
    render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        matrixStack.push();

        renderBackground(matrixStack, mouseX, mouseY, partialTicks);

        if (researchName == null) return;

        matrixStack.translate(bounds.x0 + 5, bounds.y0 + 5, 0);

        renderHeading(matrixStack, mouseX, mouseY, partialTicks);

        descriptionComponent.setBounds(getDescriptionsBounds());
        descriptionComponent.render(matrixStack, mouseX, mouseY, partialTicks);

        renderFooter(matrixStack, mouseX, mouseY, partialTicks);

        matrixStack.push();
    }

    private void
    renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().getTextureManager().bindTexture(SkillTreeScreen.UI_RESOURCE);
        fill(matrixStack,
                bounds.x0 + 5, bounds.y0 + 5,
                bounds.x1 - 5, bounds.y1 - 5,
                0xFF_C6C6C6);

        blit(matrixStack,
                bounds.x0, bounds.y0,
                0, 44, 5, 5);
        blit(matrixStack,
                bounds.x1 - 5, bounds.y0,
                8, 44, 5, 5);
        blit(matrixStack,
                bounds.x0, bounds.y1 - 5,
                0, 52, 5, 5);
        blit(matrixStack,
                bounds.x1 - 5, bounds.y1 - 5,
                8, 52, 5, 5);

        matrixStack.push();
        matrixStack.translate(bounds.x0 + 5, bounds.y0, 0);
        matrixStack.scale(bounds.getWidth() - 10, 1, 1);
        blit(matrixStack, 0, 0,
                6, 44, 1, 5);
        matrixStack.translate(0, bounds.getHeight() - 5, 0);
        blit(matrixStack, 0, 0,
                6, 52, 1, 5);
        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(bounds.x0, bounds.y0 + 5, 0);
        matrixStack.scale(1, bounds.getHeight() - 10, 1);
        blit(matrixStack, 0, 0,
                0, 50, 5, 1);
        matrixStack.translate(bounds.getWidth() - 5, 0, 0);
        blit(matrixStack, 0, 0,
                8, 50, 5, 1);
        matrixStack.pop();
    }

    private void
    renderHeading(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().getTextureManager().bindTexture(SkillTreeScreen.UI_RESOURCE);

        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        SkillStyle abilityStyle = ModConfigs.RESEARCHES_GUI.getStyles()
                .get(researchName);

        Rectangle abilityBounds = researchWidget.getClickableBounds();

        UIHelper.renderContainerBorder(this, matrixStack,
                getHeadingBounds(),
                14, 44,
                2, 2, 2, 2,
                0xFF_8B8B8B);

        boolean researched = researchTree.getResearchesDone().contains(researchName);

        String subText = !researched
                ? "Not Researched"
                : "Researched";

        int gap = 5;
        int contentWidth = abilityBounds.getWidth() + gap
                + Math.max(fontRenderer.getStringWidth(researchName), fontRenderer.getStringWidth(subText));

        matrixStack.push();
        matrixStack.translate(10, 0, 0);
        FontHelper.drawStringWithBorder(matrixStack,
                researchName,
                abilityBounds.getWidth() + gap, 13,
                !researched ? 0xFF_FFFFFF : 0xFF_fff8c7,
                !researched ? 0xFF_000000 : 0xFF_3b3300);

        FontHelper.drawStringWithBorder(matrixStack,
                subText,
                abilityBounds.getWidth() + gap, 23,
                !researched ? 0xFF_FFFFFF : 0xFF_fff8c7,
                !researched ? 0xFF_000000 : 0xFF_3b3300);

        matrixStack.translate(-abilityStyle.x, -abilityStyle.y, 0); // Nullify the viewport style
        matrixStack.translate(abilityBounds.getWidth() / 2f, 0, 0);
        matrixStack.translate(0, 23, 0);
        researchWidget.render(matrixStack, mouseX, mouseY, partialTicks);
        matrixStack.pop();
    }

    private void
    renderDescriptions(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Rectangle renderableBounds = descriptionComponent.getRenderableBounds();

        IFormattableTextComponent description = ModConfigs.SKILL_DESCRIPTIONS.getDescriptionFor(researchName);

        int renderedLineCount = UIHelper.renderWrappedText(matrixStack,
                description, renderableBounds.getWidth(), 10);

        descriptionComponent.setInnerHeight(renderedLineCount * 10 + 20);

        RenderSystem.enableDepthTest();
    }

    private void
    renderFooter(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int containerX = mouseX - bounds.x0;
        int containerY = mouseY - bounds.y0;

        this.researchButton.render(matrixStack, containerX, containerY, partialTicks);

        Minecraft.getInstance().getTextureManager().bindTexture(SkillTreeScreen.UI_RESOURCE);

        Research research = ModConfigs.RESEARCHES.getByName(researchName);
        boolean researched = researchTree.getResearchesDone().contains(researchName);

        if (!researched) {
            blit(matrixStack,
                    13, bounds.getHeight() - 40 - 2,
                    121 + (research.usesKnowledge() ? 15 : 30), 0, 15, 23);
        }
    }

}

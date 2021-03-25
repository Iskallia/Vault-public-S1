package iskallia.vault.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.widget.AbilityWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.AbilityUpgradeMessage;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.type.PlayerAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class AbilityDialog extends AbstractGui {

    private Rectangle bounds;
    private AbilityGroup<?> abilityGroup;
    private AbilityTree abilityTree;

    private AbilityWidget abilityWidget;
    private ScrollableContainer descriptionComponent;
    private Button abilityUpgradeButton;

    public AbilityDialog(AbilityTree abilityTree) {
        this.abilityGroup = null;
        this.abilityTree = abilityTree;
        refreshWidgets();
    }

    public void refreshWidgets() {
        if (this.abilityGroup != null) {
            SkillStyle abilityStyle = ModConfigs.ABILITIES_GUI.getStyles()
                    .get(abilityGroup.getParentName());
            this.abilityWidget = new AbilityWidget(abilityGroup, abilityTree, abilityStyle);

            AbilityNode<?> abilityNode = abilityTree.getNodeOf(abilityGroup);

            String buttonText = !abilityNode.isLearned() ? "Learn (" + abilityGroup.learningCost() + ")" :
                    abilityNode.getLevel() >= abilityGroup.getMaxLevel() ? "Fully Learned"
                            : "Upgrade (" + abilityGroup.cost(abilityNode.getLevel() + 1) + ")";

            this.abilityUpgradeButton = new Button(
                    10, bounds.getHeight() - 40,
                    bounds.getWidth() - 30, 20,
                    new StringTextComponent(buttonText),
                    (button) -> { upgradeAbility(); },
                    (button, matrixStack, x, y) -> { }
            );

            this.descriptionComponent = new ScrollableContainer(this::renderDescriptions);

            PlayerAbility ability = abilityNode.getAbility();
            int cost = ability == null ? abilityGroup.learningCost() : abilityGroup.cost(abilityNode.getLevel() + 1);
            this.abilityUpgradeButton.active = cost <= VaultBarOverlay.unspentSkillPoints
                    && abilityNode.getLevel() < abilityGroup.getMaxLevel();
        }
    }

    public void setAbilityGroup(AbilityGroup<?> abilityGroup) {
        this.abilityGroup = abilityGroup;
        refreshWidgets();
    }

    public AbilityDialog setBounds(Rectangle bounds) {
        this.bounds = bounds;
        return this;
    }

    public Rectangle getHeadingBounds() {
        Rectangle abilityBounds = abilityWidget.getClickableBounds();
        Rectangle headingBounds = new Rectangle();
        headingBounds.x0 = 5;
        headingBounds.y0 = 5;
        headingBounds.x1 = headingBounds.x0 + bounds.getWidth() - 20;
        headingBounds.y1 = headingBounds.y0 + abilityBounds.getHeight() + 5;
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

    public void mouseMoved(int screenX, int screenY) {
        if (bounds == null) return;

        int containerX = screenX - bounds.x0;
        int containerY = screenY - bounds.y0;

        if (this.abilityUpgradeButton != null) {
            this.abilityUpgradeButton.mouseMoved(containerX, containerY);
        }
    }

    public void mouseClicked(int screenX, int screenY, int button) {
        int containerX = screenX - bounds.x0;
        int containerY = screenY - bounds.y0;

        if (this.abilityUpgradeButton != null) {
            this.abilityUpgradeButton.mouseClicked(containerX, containerY, button);
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!bounds.contains((int) mouseX, (int) mouseY)) return;
        descriptionComponent.mouseScrolled(mouseX, mouseY, delta);
    }

    public void upgradeAbility() {
        AbilityNode<?> abilityNode = this.abilityTree.getNodeOf(abilityGroup);

        if (abilityNode.getLevel() >= abilityGroup.getMaxLevel())
            return;

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null) {
            minecraft.player.playSound(
                    abilityNode.isLearned() ? ModSounds.SKILL_TREE_UPGRADE_SFX
                            : ModSounds.SKILL_TREE_LEARN_SFX,
                    1f, 1f
            );
        }

        abilityTree.upgradeAbility(null, abilityNode);
        refreshWidgets();

        ModNetwork.CHANNEL.sendToServer(new AbilityUpgradeMessage(this.abilityGroup.getParentName()));
    }

    public void
    render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        matrixStack.pushPose();

        renderBackground(matrixStack, mouseX, mouseY, partialTicks);

        if (abilityGroup == null) return;

        matrixStack.translate(bounds.x0 + 5, bounds.y0 + 5, 0);

        renderHeading(matrixStack, mouseX, mouseY, partialTicks);

        descriptionComponent.setBounds(getDescriptionsBounds());
        descriptionComponent.render(matrixStack, mouseX, mouseY, partialTicks);

        renderFooter(matrixStack, mouseX, mouseY, partialTicks);

        matrixStack.pushPose();
    }

    private void
    renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().getTextureManager().bind(SkillTreeScreen.UI_RESOURCE);
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

        matrixStack.pushPose();
        matrixStack.translate(bounds.x0 + 5, bounds.y0, 0);
        matrixStack.scale(bounds.getWidth() - 10, 1, 1);
        blit(matrixStack, 0, 0,
                6, 44, 1, 5);
        matrixStack.translate(0, bounds.getHeight() - 5, 0);
        blit(matrixStack, 0, 0,
                6, 52, 1, 5);
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(bounds.x0, bounds.y0 + 5, 0);
        matrixStack.scale(1, bounds.getHeight() - 10, 1);
        blit(matrixStack, 0, 0,
                0, 50, 5, 1);
        matrixStack.translate(bounds.getWidth() - 5, 0, 0);
        blit(matrixStack, 0, 0,
                8, 50, 5, 1);
        matrixStack.popPose();
    }

    private void
    renderHeading(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().getTextureManager().bind(SkillTreeScreen.UI_RESOURCE);

        FontRenderer fontRenderer = Minecraft.getInstance().font;
        SkillStyle abilityStyle = ModConfigs.ABILITIES_GUI.getStyles()
                .get(abilityGroup.getParentName());

        AbilityNode<?> abilityNode = abilityTree.getNodeByName(abilityGroup.getParentName());

        Rectangle abilityBounds = abilityWidget.getClickableBounds();

        UIHelper.renderContainerBorder(this, matrixStack,
                getHeadingBounds(),
                14, 44,
                2, 2, 2, 2,
                0xFF_8B8B8B);

        String abilityName = abilityNode.getLevel() == 0
                ? abilityNode.getGroup().getName(1)
                : abilityNode.getName();

        String subText = abilityNode.getLevel() == 0
                ? "Not Learned Yet"
                : "Learned";

        int gap = 5;
        int contentWidth = abilityBounds.getWidth() + gap
                + Math.max(fontRenderer.width(abilityName), fontRenderer.width(subText));

        matrixStack.pushPose();
        matrixStack.translate(10, 0, 0);
        FontHelper.drawStringWithBorder(matrixStack,
                abilityName,
                abilityBounds.getWidth() + gap, 13,
                abilityNode.getLevel() == 0 ? 0xFF_FFFFFF : 0xFF_fff8c7,
                abilityNode.getLevel() == 0 ? 0xFF_000000 : 0xFF_3b3300);

        FontHelper.drawStringWithBorder(matrixStack,
                subText,
                abilityBounds.getWidth() + gap, 23,
                abilityNode.getLevel() == 0 ? 0xFF_FFFFFF : 0xFF_fff8c7,
                abilityNode.getLevel() == 0 ? 0xFF_000000 : 0xFF_3b3300);

//        FontHelper.drawStringWithBorder(matrixStack,
//                abilityGroup.getMaxLevel() + " Max Level(s)",
//                abilityBounds.getWidth() + gap, 54,
//                abilityNode.getLevel() == 0 ? 0xFF_FFFFFF : 0xFF_fff8c7,
//                abilityNode.getLevel() == 0 ? 0xFF_000000 : 0xFF_3b3300);

        matrixStack.translate(-abilityStyle.x, -abilityStyle.y, 0); // Nullify the viewport style
        matrixStack.translate(abilityBounds.getWidth() / 2f, 0, 0);
        matrixStack.translate(0, 23, 0);
        abilityWidget.render(matrixStack, mouseX, mouseY, partialTicks);
        matrixStack.popPose();
    }

    private void
    renderDescriptions(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Rectangle renderableBounds = descriptionComponent.getRenderableBounds();

        IFormattableTextComponent description = ModConfigs.SKILL_DESCRIPTIONS.getDescriptionFor(abilityGroup.getParentName());

        int renderedLineCount = UIHelper.renderWrappedText(matrixStack,
                description, renderableBounds.getWidth(), 10);

        descriptionComponent.setInnerHeight(renderedLineCount * 10 + 20);

        RenderSystem.enableDepthTest();
    }

    private void
    renderFooter(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int containerX = mouseX - bounds.x0;
        int containerY = mouseY - bounds.y0;

        this.abilityUpgradeButton.render(matrixStack, containerX, containerY, partialTicks);

        Minecraft.getInstance().getTextureManager().bind(SkillTreeScreen.UI_RESOURCE);

        AbilityNode<?> abilityNode = abilityTree.getNodeOf(abilityGroup);

        if (abilityNode.isLearned() && abilityNode.getLevel() < abilityGroup.getMaxLevel()) {
            blit(matrixStack,
                    13, bounds.getHeight() - 40 - 2,
                    121, 0, 15, 23);
        }
    }

}

package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.Vault;
import iskallia.vault.client.gui.component.AbilityDialog;
import iskallia.vault.client.gui.component.ResearchDialog;
import iskallia.vault.client.gui.component.TalentDialog;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.tab.AbilitiesTab;
import iskallia.vault.client.gui.tab.ResearchesTab;
import iskallia.vault.client.gui.tab.SkillTab;
import iskallia.vault.client.gui.tab.TalentsTab;
import iskallia.vault.container.SkillTreeContainer;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.talent.TalentTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkillTreeScreen extends ContainerScreen<SkillTreeContainer> {

    public static final ResourceLocation HUD_RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/vault-hud.png");
    public static final ResourceLocation UI_RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/ability-tree.png");
    public static final ResourceLocation BACKGROUNDS_RESOURCE = new ResourceLocation(Vault.MOD_ID, "textures/gui/ability-tree-bgs.png");

    public static final int TAB_WIDTH = 28;
    public static final int GAP = 3;

    protected SkillTab activeTab;
    protected TalentDialog talentDialog;
    protected AbilityDialog abilityDialog;
    protected ResearchDialog researchDialog;

    public SkillTreeScreen(SkillTreeContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, new StringTextComponent("Ability Tree Screen!"));

        this.activeTab = new AbilitiesTab(this);
        AbilityTree abilityTree = getContainer().getAbilityTree();
        TalentTree talentTree = getContainer().getTalentTree();
        ResearchTree researchTree = getContainer().getResearchTree();
        this.abilityDialog = new AbilityDialog(abilityTree);
        this.talentDialog = new TalentDialog(talentTree);
        this.researchDialog = new ResearchDialog(researchTree, talentTree);
        refreshWidgets();

        xSize = 270;
        ySize = 200;
    }

    @Override
    protected void init() {
        xSize = width; // <-- Be goneee, JEI!
        super.init();
    }

    public void refreshWidgets() {
        this.activeTab.refresh();
        if (this.talentDialog != null) {
            this.talentDialog.refreshWidgets();
        }
        if (this.researchDialog != null) {
            this.researchDialog.refreshWidgets();
        }
        if (this.abilityDialog != null) {
            this.abilityDialog.refreshWidgets();
        }
    }

    public Rectangle getContainerBounds() {
        Rectangle bounds = new Rectangle();
        bounds.x0 = 30; //px
        bounds.y0 = 60; //px
        bounds.x1 = (int) (width * 0.55); // Responsiveness ayyyyy
        bounds.y1 = height - 30;
        return bounds;
    }

    public Rectangle getTabBounds(int index, boolean active) {
        Rectangle containerBounds = getContainerBounds();
        Rectangle bounds = new Rectangle();
        bounds.x0 = containerBounds.x0 + 5 + index * (TAB_WIDTH + GAP);
        bounds.y0 = containerBounds.y0 - 25 - (active ? 21 : 17);
        bounds.setWidth(TAB_WIDTH);
        bounds.setHeight(active ? 32 : 25);
        return bounds;
    }

    public TalentDialog getTalentDialog() {
        return talentDialog;
    }

    public ResearchDialog getResearchDialog() {
        return researchDialog;
    }

    public AbilityDialog getAbilityDialog() {
        return abilityDialog;
    }

    /* --------------------------------------------------- */

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Rectangle containerBounds = getContainerBounds();

        if (containerBounds.contains((int) mouseX, (int) mouseY)) {
            this.activeTab.mouseClicked(mouseX, mouseY, button);

        } else {
            Rectangle abilitiesTabBounds = getTabBounds(0, activeTab instanceof AbilitiesTab);
            Rectangle talentsTabBounds = getTabBounds(1, activeTab instanceof TalentsTab);
            Rectangle researchesTabBounds = getTabBounds(2, activeTab instanceof ResearchesTab);

            if (abilitiesTabBounds.contains((int) mouseX, (int) mouseY)) {
                this.activeTab.onClose();
                this.activeTab = new AbilitiesTab(this);
                this.refreshWidgets();

            } else if (talentsTabBounds.contains(((int) mouseX), ((int) mouseY))) {
                this.activeTab.onClose();
                this.activeTab = new TalentsTab(this);
                this.refreshWidgets();

            } else if (researchesTabBounds.contains(((int) mouseX), ((int) mouseY))) {
                this.activeTab.onClose();
                this.activeTab = new ResearchesTab(this);
                this.refreshWidgets();

            } else if (activeTab instanceof ResearchesTab) {
                this.researchDialog.mouseClicked((int) mouseX, (int) mouseY, button);

            } else if (activeTab instanceof TalentsTab) {
                this.talentDialog.mouseClicked((int) mouseX, (int) mouseY, button);

            } else if (activeTab instanceof AbilitiesTab) {
                this.abilityDialog.mouseClicked((int) mouseX, (int) mouseY, button);
            }
        }


        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.activeTab.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.activeTab.mouseMoved(mouseX, mouseY);

        if (activeTab instanceof ResearchesTab) {
            this.researchDialog.mouseMoved((int) mouseX, (int) mouseY);

        } else if (activeTab instanceof TalentsTab) {
            this.talentDialog.mouseMoved((int) mouseX, (int) mouseY);

        } else if (activeTab instanceof AbilitiesTab) {
            this.abilityDialog.mouseMoved((int) mouseX, (int) mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (getContainerBounds().contains((int) mouseX, (int) mouseY)) {
            this.activeTab.mouseScrolled(mouseX, mouseY, delta);

        } else if (activeTab instanceof ResearchesTab) {
            this.researchDialog.mouseScrolled(mouseX, mouseY, delta);

        } else if (activeTab instanceof TalentsTab) {
            this.talentDialog.mouseScrolled(mouseX, mouseY, delta);

        } else if (activeTab instanceof AbilitiesTab) {
            this.abilityDialog.mouseScrolled(mouseX, mouseY, delta);
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        this.activeTab.onClose();
    }

    /* --------------------------------------------------- */

    @Override
    protected void
    drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        renderBackground(matrixStack);
    }

    @Override
    protected void
    drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        // For some reason, without this it won't render :V
        this.font.func_243248_b(matrixStack,
                new StringTextComponent(""),
                (float) this.titleX, (float) this.titleY,
                4210752);
    }

    @Override
    public void
    render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        UIHelper.renderOverflowHidden(matrixStack,
                this::renderContainerBackground,
                ms -> activeTab.render(ms, mouseX, mouseY, partialTicks));

        Rectangle containerBounds = getContainerBounds();

        if (VaultBarOverlay.unspentSkillPoints > 0) {
            getMinecraft().getTextureManager().bindTexture(HUD_RESOURCE);
            int toastWidth = 160;
            int right = getMinecraft().getMainWindow().getScaledWidth();
            String unspentText = VaultBarOverlay.unspentSkillPoints == 1
                    ? " unspent skill point"
                    : " unspent skill points";
            String unspentPointsText = VaultBarOverlay.unspentSkillPoints + "";
            int unspentPointsWidth = minecraft.fontRenderer.getStringWidth(unspentPointsText);
            int unspentWidth = minecraft.fontRenderer.getStringWidth(unspentText);
            int gap = 5;
            int yOffset = 18;
            minecraft.fontRenderer.drawStringWithShadow(matrixStack, VaultBarOverlay.unspentSkillPoints + "",
                    right - unspentWidth - unspentPointsWidth - gap, yOffset,
                    0xFF_ffd800
            );
            minecraft.fontRenderer.drawStringWithShadow(matrixStack, unspentText,
                    right - unspentWidth - gap, yOffset,
                    0xFF_ffffff
            );
        }

        if (VaultBarOverlay.unspentKnowledgePoints > 0) {
            getMinecraft().getTextureManager().bindTexture(HUD_RESOURCE);
            int right = getMinecraft().getMainWindow().getScaledWidth();
            String unspentText = VaultBarOverlay.unspentKnowledgePoints == 1
                    ? " unspent knowledge point"
                    : " unspent knowledge points";
            String unspentPointsText = VaultBarOverlay.unspentKnowledgePoints + "";
            int unspentPointsWidth = minecraft.fontRenderer.getStringWidth(unspentPointsText);
            int unspentWidth = minecraft.fontRenderer.getStringWidth(unspentText);
            int gap = 5;
            int yOffset = 18;
            matrixStack.push();
            if (VaultBarOverlay.unspentSkillPoints > 0) {
                matrixStack.translate(0, 12, 0);
            }
            minecraft.fontRenderer.drawStringWithShadow(matrixStack, VaultBarOverlay.unspentKnowledgePoints + "",
                    right - unspentWidth - unspentPointsWidth - gap, yOffset,
                    0xFF_40d7b1
            );
            minecraft.fontRenderer.drawStringWithShadow(matrixStack, unspentText,
                    right - unspentWidth - gap, yOffset,
                    0xFF_ffffff
            );
            matrixStack.pop();
        }

        renderContainerBorders(matrixStack);
        renderContainerTabs(matrixStack);
        Rectangle dialogBounds = new Rectangle();
        dialogBounds.x0 = containerBounds.x1 + 15;
        dialogBounds.y0 = containerBounds.y0 - 18;
        dialogBounds.x1 = width - 21;
        dialogBounds.y1 = height - 21;

        abilityDialog.setBounds(dialogBounds);
        researchDialog.setBounds(dialogBounds);
        talentDialog.setBounds(dialogBounds);

        if (activeTab instanceof ResearchesTab) {
            researchDialog.render(matrixStack, mouseX, mouseY, partialTicks);

        } else if (activeTab instanceof TalentsTab) {
            talentDialog.render(matrixStack, mouseX, mouseY, partialTicks);

        } else if (activeTab instanceof AbilitiesTab) {
            abilityDialog.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    private void
    renderContainerTabs(MatrixStack matrixStack) {
        Rectangle containerBounds = getContainerBounds();

        // Abilities
        Rectangle abilitiesTabBounds = getTabBounds(0, activeTab instanceof AbilitiesTab);
        blit(matrixStack,
                abilitiesTabBounds.x0,
                abilitiesTabBounds.y0,
                63, (activeTab instanceof AbilitiesTab) ? 28 : 0,
                abilitiesTabBounds.getWidth(), abilitiesTabBounds.getHeight());
        blit(matrixStack,
                abilitiesTabBounds.x0 + 6,
                containerBounds.y0 - 25 - 11,
                32, 60, 16, 16);

        // Talents
        Rectangle talentsTabBounds = getTabBounds(1, activeTab instanceof TalentsTab);
        blit(matrixStack,
                talentsTabBounds.x0,
                talentsTabBounds.y0,
                63, (activeTab instanceof TalentsTab) ? 28 : 0,
                talentsTabBounds.getWidth(), talentsTabBounds.getHeight());
        blit(matrixStack,
                talentsTabBounds.x0 + 6,
                containerBounds.y0 - 25 - 11,
                16, 60, 16, 16);

        // Research
        Rectangle researchesTabBounds = getTabBounds(2, activeTab instanceof ResearchesTab);
        blit(matrixStack,
                researchesTabBounds.x0,
                researchesTabBounds.y0,
                63, (activeTab instanceof ResearchesTab) ? 28 : 0,
                researchesTabBounds.getWidth(), researchesTabBounds.getHeight());
        blit(matrixStack,
                researchesTabBounds.x0 + 6,
                containerBounds.y0 - 25 - 11,
                0, 60, 16, 16);

        Minecraft minecraft = getMinecraft();

        if (activeTab instanceof AbilitiesTab) {
            minecraft.fontRenderer.drawString(matrixStack, "Abilities",
                    containerBounds.x0, containerBounds.y0 - 12, 0xFF_3f3f3f);

        } else if (activeTab instanceof TalentsTab) {
            minecraft.fontRenderer.drawString(matrixStack, "Talents",
                    containerBounds.x0, containerBounds.y0 - 12, 0xFF_3f3f3f);

        } else if (activeTab instanceof ResearchesTab) {
            minecraft.fontRenderer.drawString(matrixStack, "Researches",
                    containerBounds.x0, containerBounds.y0 - 12, 0xFF_3f3f3f);
        }

        minecraft.textureManager.bindTexture(VaultBarOverlay.RESOURCE);

        String text = String.valueOf(VaultBarOverlay.vaultLevel);
        int textWidth = minecraft.fontRenderer.getStringWidth(text);
        int barWidth = 85;
        float expPercentage = (float) VaultBarOverlay.vaultExp / VaultBarOverlay.tnl;

        int barX = containerBounds.x1 - barWidth - 5;
        int barY = containerBounds.y0 - 10;

        minecraft.getProfiler().startSection("vaultBar");
        minecraft.ingameGUI.blit(matrixStack,
                barX, barY,
                1, 1, barWidth, 5);
        minecraft.ingameGUI.blit(matrixStack,
                barX, barY,
                1, 7, (int) (barWidth * expPercentage), 5);
        FontHelper.drawStringWithBorder(matrixStack,
                text,
                barX - textWidth - 1, barY - 1,
                0xFF_ffe637, 0xFF_3e3e3e);

        minecraft.getProfiler().endSection();

    }

    private void
    renderContainerBorders(MatrixStack matrixStack) {
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(UI_RESOURCE);

        Rectangle containerBounds = getContainerBounds();

        RenderSystem.enableBlend();

        blit(matrixStack, containerBounds.x0 - 9, containerBounds.y0 - 18,
                0, 0, 15, 24);
        blit(matrixStack, containerBounds.x1 - 7, containerBounds.y0 - 18,
                18, 0, 15, 24);
        blit(matrixStack, containerBounds.x0 - 9, containerBounds.y1 - 7,
                0, 27, 15, 16);
        blit(matrixStack, containerBounds.x1 - 7, containerBounds.y1 - 7,
                18, 27, 15, 16);

        matrixStack.push();
        matrixStack.translate(containerBounds.x0 + 6, containerBounds.y0 - 18, 0);
        matrixStack.scale(containerBounds.x1 - containerBounds.x0 - 13, 1, 1);
        blit(matrixStack, 0, 0,
                16, 0, 1, 24);
        matrixStack.translate(0, containerBounds.y1 - containerBounds.y0 + 11, 0);
        blit(matrixStack, 0, 0,
                16, 27, 1, 16);
        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(containerBounds.x0 - 9, containerBounds.y0 + 6, 0);
        matrixStack.scale(1, containerBounds.y1 - containerBounds.y0 - 13, 1);
        blit(matrixStack, 0, 0,
                0, 25, 15, 1);
        matrixStack.translate(containerBounds.x1 - containerBounds.x0 + 2, 0, 0);
        blit(matrixStack, 0, 0,
                18, 25, 15, 1);
        matrixStack.pop();
    }

    private void
    renderContainerBackground(MatrixStack matrixStack) {
        assert this.minecraft != null;

        this.minecraft.getTextureManager().bindTexture(BACKGROUNDS_RESOURCE);

        Rectangle containerBounds = getContainerBounds();

        // TODO: Include scale param
        int textureSize = 16;
        int currentX = containerBounds.x0;
        int currentY = containerBounds.y0;
        int uncoveredWidth = containerBounds.getWidth();
        int uncoveredHeight = containerBounds.getHeight();
        while (uncoveredWidth > 0) {
            while (uncoveredHeight > 0) {
                blit(matrixStack, currentX, currentY,
                        16 * 5, 0, // TODO: <-- depends on tab
                        Math.min(textureSize, uncoveredWidth),
                        Math.min(textureSize, uncoveredHeight)
                );
                uncoveredHeight -= textureSize;
                currentY += textureSize;
            }

            // Decrement
            uncoveredWidth -= textureSize;
            currentX += textureSize;

            // Reset
            uncoveredHeight = containerBounds.getHeight();
            currentY = containerBounds.y0;
        }
    }

}

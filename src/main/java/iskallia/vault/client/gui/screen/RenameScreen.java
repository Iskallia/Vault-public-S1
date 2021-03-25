package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.RenameUIMessage;
import iskallia.vault.util.RenameType;
import iskallia.vault.util.nbt.NBTSerializer;
import iskallia.vault.vending.TraderCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class RenameScreen extends ContainerScreen<RenamingContainer> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Vault.MOD_ID, "textures/gui/rename_screen.png");
    private String name;
    private CompoundNBT data;
    private RenameType renameType;
    private Button renameButton;
    private ItemStack traderCircuit;
    private TraderCore core;
    private BlockPos chamberPos;

    private TextFieldWidget nameField;


    public RenameScreen(RenamingContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, new StringTextComponent("Rename Player"));

        font = Minecraft.getInstance().font;
        imageWidth = 118;
        imageHeight = 61;

        titleLabelX = 59;
        titleLabelY = 7;

        renameType = screenContainer.getRenameType();
        data = screenContainer.getNbt();

        if (renameType == RenameType.PLAYER_STATUE) {
            name = data.getString("PlayerNickname");
        } else if (renameType == RenameType.TRADER_CORE) {
            traderCircuit = ItemStack.of(data);
            CompoundNBT stackNbt = traderCircuit.getOrCreateTag();
            try {

                core = NBTSerializer.deserialize(TraderCore.class, stackNbt.getCompound("core"));
                name = core.getName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (renameType == RenameType.CRYO_CHAMBER) {
            chamberPos = NBTUtil.readBlockPos(data.getCompound("BlockPos"));
            name = data.getString("EternalName");
        }
    }

    @Override
    protected void init() {
        super.init();
        initFields();
    }

    protected void initFields() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.nameField = new TextFieldWidget(this.font, i + 7, j + 26, 103, 12, new StringTextComponent(name));
        this.nameField.setCanLoseFocus(false);
        this.nameField.setTextColor(-1);
        this.nameField.setTextColorUneditable(-1);
        this.nameField.setBordered(false);
        this.nameField.setMaxLength(16);
        this.nameField.setResponder(this::rename);
        this.children.add(this.nameField);
        this.setInitialFocus(this.nameField);
        this.nameField.setValue(name);

        this.renameButton = new Button(i + 4, j + 41, 110, 16, new StringTextComponent("Confirm"), this::confirmPressed);
        this.addButton(renameButton);
    }

    private void confirmPressed(Button button) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("RenameType", renameType.ordinal());
        if (renameType == RenameType.PLAYER_STATUE) {
            data.putString("PlayerNickname", name);
            nbt.put("Data", data);
        } else if (renameType == RenameType.TRADER_CORE) {
            try {
                CompoundNBT stackNbt = traderCircuit.getOrCreateTag();
                core.setName(name);
                CompoundNBT coreNbt = NBTSerializer.serialize(core);
                stackNbt.put("core", coreNbt);
                traderCircuit.setTag(stackNbt);
                nbt.put("Data", traderCircuit.serializeNBT());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (renameType == RenameType.CRYO_CHAMBER) {
            CompoundNBT data = new CompoundNBT();
            data.put("BlockPos", NBTUtil.writeBlockPos(chamberPos));
            data.putString("EternalName", name);
            nbt.put("Data", data);
        }
        ModNetwork.CHANNEL.sendToServer(RenameUIMessage.updateName(this.renameType, nbt));
        //TODO: Send Packet.
        this.onClose();
    }

    private void rename(String name) {
        if (!name.isEmpty()) {
            this.name = name;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (this.minecraft != null && this.minecraft.player != null)
                this.minecraft.player.closeContainer();
        } else if (keyCode == 257) {
            Minecraft.getInstance().getSoundManager()
                    .play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
            this.confirmPressed(this.renameButton);
        } else if (keyCode == 69) {
            return true;
        }

        return this.nameField.keyPressed(keyCode, scanCode, modifiers)
                || this.nameField.canConsumeInput()
                || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);

        float midX = width / 2f;
        float midY = height / 2f;
        minecraft.getTextureManager().bind(TEXTURE);
        blit(matrixStack, (int) (midX - imageWidth / 2), (int) (midY - imageHeight / 2),
                0, 0, imageWidth, imageHeight,
                256, 256);

        renderTitle(matrixStack);
        renderNameField(matrixStack, mouseX, mouseY, partialTicks);
        this.renameButton.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
    }

    private void renderTitle(MatrixStack matrixStack) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        float startX = (i + this.titleLabelX) - (this.font.width("Rename Player") / 2);
        float startY = j + (float) this.titleLabelY;
        this.font.draw(matrixStack, this.title, startX, startY, 4210752);

    }

    public void renderNameField(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.nameField.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}

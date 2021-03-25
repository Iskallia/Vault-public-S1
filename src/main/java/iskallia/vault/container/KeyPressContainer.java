package iskallia.vault.container;

import iskallia.vault.container.inventory.KeyPressInventory;
import iskallia.vault.init.ModContainers;
import iskallia.vault.util.EntityHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class KeyPressContainer extends Container {

    private KeyPressInventory internalInventory;
    private IItemHandler playerInventory;

    public KeyPressContainer(int windowId, PlayerEntity player) {
        super(ModContainers.KEY_PRESS_CONTAINER, windowId);

        internalInventory = new KeyPressInventory();
        playerInventory = new InvWrapper(player.inventory);

        this.addSlot(new Slot(internalInventory, KeyPressInventory.KEY_SLOT, 27, 47));
        this.addSlot(new Slot(internalInventory, KeyPressInventory.CLUSTER_SLOT, 76, 47));
        this.addSlot(new Slot(internalInventory, KeyPressInventory.RESULT_SLOT, 134, 47) {
            @Override
            public ItemStack onTake(PlayerEntity player, ItemStack stack) {
                ItemStack itemStack = super.onTake(player, stack);

                if (!player.level.isClientSide && !itemStack.isEmpty()) {
                    player.level.levelEvent(1030, player.blockPosition(), 0);
                }

                return itemStack;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return false; // Do not accept any item in
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(player.inventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        Slot slot = slots.get(index);

        if (slot == null || !slot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stackOnSlot = slot.getItem();
        ItemStack copiedStack = stackOnSlot.copy();

        // Picking the result item
        if (index == KeyPressInventory.RESULT_SLOT) {
            if (moveItemStackTo(stackOnSlot, 3, 39, false)) {
                internalInventory.removeItem(KeyPressInventory.KEY_SLOT, 1);
                internalInventory.removeItem(KeyPressInventory.CLUSTER_SLOT, 1);
                player.level.levelEvent(1030, player.blockPosition(), 0);
                return copiedStack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        // Picking ingredient items
        if (index == KeyPressInventory.KEY_SLOT || index == KeyPressInventory.CLUSTER_SLOT) {
            if (moveItemStackTo(stackOnSlot, 3, 39, false)) {
                internalInventory.updateResult();
                return copiedStack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        // Picking from actual player inventory
        if (!moveItemStackTo(stackOnSlot, 0, 2, false))
            return ItemStack.EMPTY;

        if (stackOnSlot.isEmpty())
            slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        if (stackOnSlot.getCount() == copiedStack.getCount())
            return ItemStack.EMPTY;

        return copiedStack;
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);

        ItemStack keyStack = internalInventory.getItem(KeyPressInventory.KEY_SLOT);
        ItemStack clusterStack = internalInventory.getItem(KeyPressInventory.CLUSTER_SLOT);

        if (!keyStack.isEmpty()) {
            EntityHelper.giveItem(player, keyStack);
            EntityHelper.giveItem(player, clusterStack);
        }
    }

}

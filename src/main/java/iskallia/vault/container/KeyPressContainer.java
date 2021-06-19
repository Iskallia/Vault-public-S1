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

                if (!player.world.isRemote && !itemStack.isEmpty()) {
                    player.world.playEvent(1030, player.getPosition(), 0);
                }

                return itemStack;
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
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
    public boolean canInteractWith(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
        Slot slot = inventorySlots.get(index);

        if (slot == null || !slot.getHasStack())
            return ItemStack.EMPTY;

        ItemStack stackOnSlot = slot.getStack();
        ItemStack copiedStack = stackOnSlot.copy();

        // Picking the result item
        if (index == KeyPressInventory.RESULT_SLOT) {
            if (mergeItemStack(stackOnSlot, 3, 39, false)) {
                internalInventory.decrStackSize(KeyPressInventory.KEY_SLOT, 1);
                internalInventory.decrStackSize(KeyPressInventory.CLUSTER_SLOT, 1);
                player.world.playEvent(1030, player.getPosition(), 0);
                return copiedStack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        // Picking ingredient items
        if (index == KeyPressInventory.KEY_SLOT || index == KeyPressInventory.CLUSTER_SLOT) {
            if (mergeItemStack(stackOnSlot, 3, 39, false)) {
                internalInventory.updateResult();
                return copiedStack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        // Picking from actual player inventory
        if (!mergeItemStack(stackOnSlot, 0, 2, false))
            return ItemStack.EMPTY;

        if (stackOnSlot.isEmpty())
            slot.putStack(ItemStack.EMPTY);
        else slot.onSlotChanged();

        if (stackOnSlot.getCount() == copiedStack.getCount())
            return ItemStack.EMPTY;

        return copiedStack;
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        super.onContainerClosed(player);

        ItemStack keyStack = internalInventory.getStackInSlot(KeyPressInventory.KEY_SLOT);
        ItemStack clusterStack = internalInventory.getStackInSlot(KeyPressInventory.CLUSTER_SLOT);
       
        if (!keyStack.isEmpty()||!clusterStack.isEmpty()) { //fixes #354
            EntityHelper.giveItem(player, keyStack);
            EntityHelper.giveItem(player, clusterStack);
        }
    }

}

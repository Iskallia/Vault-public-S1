package iskallia.vault.container.inventory;

import iskallia.vault.init.ModConfigs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class KeyPressInventory implements IInventory {

    public static final int KEY_SLOT = 0;
    public static final int CLUSTER_SLOT = 1;
    public static final int RESULT_SLOT = 2;

    private final NonNullList<ItemStack> slots = NonNullList.withSize(3, ItemStack.EMPTY);

    @Override
    public int getSizeInventory() {
        return this.slots.size();
    }

    @Override
    public boolean isEmpty() {
        return this.slots.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.slots.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemStack = slots.get(index);

        if (index == RESULT_SLOT && !itemStack.isEmpty()) {
            ItemStack andSplit = ItemStackHelper.getAndSplit(slots, index, itemStack.getCount());
            decrStackSize(KEY_SLOT, 1);
            decrStackSize(CLUSTER_SLOT, 1);
            updateResult();
            return andSplit;
        }

        ItemStack splitStack = ItemStackHelper.getAndSplit(slots, index, count);
        updateResult();
        return splitStack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack andRemove = ItemStackHelper.getAndRemove(this.slots, index);
        updateResult();
        return andRemove;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        slots.set(index, stack);
        updateResult();
    }

    @Override
    public void markDirty() { }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    public void updateResult() {
        Item keyItem = getStackInSlot(KEY_SLOT).getItem();
        Item clusterItem = getStackInSlot(CLUSTER_SLOT).getItem();

        ItemStack result = ModConfigs.KEY_PRESS.getResultFor(keyItem, clusterItem);
        slots.set(RESULT_SLOT, result);
    }

    @Override
    public void clear() {
        this.slots.clear();
    }

}

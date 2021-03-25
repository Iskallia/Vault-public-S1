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
    public int getContainerSize() {
        return this.slots.size();
    }

    @Override
    public boolean isEmpty() {
        return this.slots.isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return this.slots.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemStack = slots.get(index);

        if (index == RESULT_SLOT && !itemStack.isEmpty()) {
            ItemStack andSplit = ItemStackHelper.removeItem(slots, index, itemStack.getCount());
            removeItem(KEY_SLOT, 1);
            removeItem(CLUSTER_SLOT, 1);
            updateResult();
            return andSplit;
        }

        ItemStack splitStack = ItemStackHelper.removeItem(slots, index, count);
        updateResult();
        return splitStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack andRemove = ItemStackHelper.takeItem(this.slots, index);
        updateResult();
        return andRemove;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        slots.set(index, stack);
        updateResult();
    }

    @Override
    public void setChanged() { }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    public void updateResult() {
        Item keyItem = getItem(KEY_SLOT).getItem();
        Item clusterItem = getItem(CLUSTER_SLOT).getItem();

        ItemStack result = ModConfigs.KEY_PRESS.getResultFor(keyItem, clusterItem);
        slots.set(RESULT_SLOT, result);
    }

    @Override
    public void clearContent() {
        this.slots.clear();
    }

}

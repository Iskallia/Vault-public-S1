package iskallia.vault.container.inventory;

import iskallia.vault.block.entity.VendingMachineTileEntity;
import iskallia.vault.vending.Product;
import iskallia.vault.vending.Trade;
import iskallia.vault.vending.TraderCore;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class VendingInventory implements IInventory {

    public static final int BUY_SLOT = 0;
    public static final int EXTRA_SLOT = 1;
    public static final int SELL_SLOT = 2;

    private final NonNullList<ItemStack> slots = NonNullList.withSize(3, ItemStack.EMPTY);
    private VendingMachineTileEntity tileEntity;
    private TraderCore selectedCore;

    public void updateSelectedCore(VendingMachineTileEntity tileEntity, TraderCore core) {
        this.tileEntity = tileEntity;
        this.selectedCore = core;
    }

    public TraderCore getSelectedCore() {
        return selectedCore;
    }

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

        if (index == SELL_SLOT && !itemStack.isEmpty()) {
            ItemStack andSplit = ItemStackHelper.removeItem(slots, index, itemStack.getCount());
            removeItem(BUY_SLOT, selectedCore.getTrade().getBuy().getAmount());
            selectedCore.getTrade().onTraded();
            tileEntity.sendUpdates();
            updateRecipe();
            return andSplit;
        }

        ItemStack splitStack = ItemStackHelper.removeItem(slots, index, count);
        updateRecipe();
        return splitStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack andRemove = ItemStackHelper.takeItem(this.slots, index);
        updateRecipe();
        return andRemove;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        slots.set(index, stack);
        updateRecipe();
    }

    @Override
    public void setChanged() { }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    public void updateRecipe() {
        if (selectedCore == null) return;

        Trade trade = selectedCore.getTrade();
        Product buy = trade.getBuy();
        Product sell = trade.getSell();

        if (slots.get(BUY_SLOT).getItem() != buy.getItem()) {
            slots.set(SELL_SLOT, ItemStack.EMPTY);
        } else if (slots.get(BUY_SLOT).getCount() < buy.getAmount()) {
            slots.set(SELL_SLOT, ItemStack.EMPTY);
        } else {
            slots.set(SELL_SLOT, sell.toStack());
        }

        if (trade.getTradesLeft() == 0) {
            slots.set(SELL_SLOT, ItemStack.EMPTY);
        }
    }

    @Override
    public void clearContent() {
        this.slots.clear();
    }

}

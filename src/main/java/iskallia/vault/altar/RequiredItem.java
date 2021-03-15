package iskallia.vault.altar;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class RequiredItem {

    private ItemStack item;
    private int currentAmount;
    private int amountRequired;

    public RequiredItem(ItemStack stack, int currentAmount, int amountRequired) {
        this.item = stack;
        this.currentAmount = currentAmount;
        this.amountRequired = amountRequired;
    }

    public static CompoundNBT serializeNBT(RequiredItem requiredItem) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("item", requiredItem.getItem().serializeNBT());
        nbt.putInt("currentAmount", requiredItem.getCurrentAmount());
        nbt.putInt("amountRequired", requiredItem.getAmountRequired());
        return nbt;
    }

    public static RequiredItem deserializeNBT(CompoundNBT nbt) {
        if (!nbt.contains("item"))
            return null;
        return new RequiredItem(ItemStack.read(nbt.getCompound("item")), nbt.getInt("currentAmount"), nbt.getInt("amountRequired"));
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }

    public void addAmount(int amount) {
        currentAmount += amount;
    }

    public int getAmountRequired() {
        return amountRequired;
    }

    public void setAmountRequired(int amountRequired) {
        this.amountRequired = amountRequired;
    }

    public boolean reachedAmountRequired() {
        if (this.getCurrentAmount() == this.getAmountRequired()) {
            return true;
        }
        return false;
    }

    public int getRemainder(int amount) {
        if (this.getCurrentAmount() + amount >= this.getAmountRequired()) {
            return this.getCurrentAmount() + amount - this.getAmountRequired();
        } else {
            return 0;
        }
    }

    public boolean isItemEqual(ItemStack stack) {
        return ItemStack.areItemsEqualIgnoreDurability(this.getItem(), stack);
    }

}

package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;

public class ItemEntry {

    @Expose public String ITEM;
    @Expose public int AMOUNT;
    @Expose public String NBT;

    public ItemEntry(String item, int amount, String nbt) {
        this.ITEM = item;
        this.AMOUNT = amount;
        this.NBT = nbt;
    }

}

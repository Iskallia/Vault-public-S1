package iskallia.vault.vending;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.nbt.INBTSerializable;
import iskallia.vault.util.nbt.NBTSerialize;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class Product implements INBTSerializable {

    protected Item itemCache;
    protected CompoundNBT nbtCache;

    @Expose
    @NBTSerialize
    protected String id;
    @Expose
    @NBTSerialize
    protected String nbt;
    @Expose
    @NBTSerialize
    protected int amount;

    public Product() {

    }

    public Product(Item item, int amount, CompoundNBT nbt) {
        this.itemCache = item;
        if (this.itemCache != null)
            this.id = item.getRegistryName().toString();
        this.nbtCache = nbt;
        if (this.nbtCache != null)
            this.nbt = this.nbtCache.toString();
        this.amount = amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        else if (obj == this)
            return true;
        else if (this.getClass() != obj.getClass())
            return false;

        Product product = (Product) obj;

        boolean similarNBT;

        if (this.getNBT() != null && product.getNBT() != null) {
            similarNBT = this.getNBT().equals(product.getNBT());
        } else {
            similarNBT = true;
        }

        return product.getItem() == this.getItem() && similarNBT;
    }

    public int getAmount() {
        return this.amount;
    }

    public Item getItem() {
        if (this.itemCache != null)
            return this.itemCache;
        this.itemCache = ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.id));
        if (this.itemCache == null)
            System.out.println("Unknown item " + this.id + ".");
        return this.itemCache;
    }

    public String getId() {
        return this.id;
    }

    public CompoundNBT getNBT() {
        if (this.nbt == null)
            return null;
        try {
            if (this.nbtCache == null)
                this.nbtCache = JsonToNBT.getTagFromJson(this.nbt);
        } catch (Exception e) {
            this.nbtCache = null;
            System.out.println("Unknown NBT for item " + this.id + ".");
        }
        return this.nbtCache;
    }

    public boolean isValid() {
        if (this.getAmount() <= 0)
            return false;
        if (this.getItem() == null)
            return false;
        if (this.getItem() == Items.AIR)
            return false;
        if (this.getAmount() > this.getItem().getMaxStackSize())
            return false;
        if (this.nbt != null && this.getNBT() == null)
            return false;
        return true;
    }

    public ItemStack toStack() {
        ItemStack stack = new ItemStack(this.getItem(), this.getAmount());
        stack.setTag(this.getNBT());
        return stack;
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + id + '\'' +
                ", nbt='" + nbt + '\'' +
                ", amount=" + amount +
                '}';
    }
}

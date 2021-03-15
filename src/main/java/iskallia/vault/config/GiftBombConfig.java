package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.item.ItemGiftBomb;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GiftBombConfig extends Config {

    @Expose private List<ItemEntry> GIFT_LOOTS;
    @Expose private List<ItemEntry> SUPER_GIFT_LOOTS;
    @Expose private List<ItemEntry> MEGA_GIFT_LOOTS;
    @Expose private List<ItemEntry> OMEGA_GIFT_LOOTS;

    @Override
    public String getName() {
        return "gift_bomb";
    }

    @Override
    protected void reset() {
        this.GIFT_LOOTS = new LinkedList<>();
        this.GIFT_LOOTS.add(new ItemEntry("minecraft:golden_apple", 2, "{display:{Name:'{\"text\":\"Fancier Apple\"}'}}"));
        this.GIFT_LOOTS.add(new ItemEntry("minecraft:iron_sword", 1, "{Enchantments:[{id:\"minecraft:sharpness\",lvl:10s}]}"));

        this.SUPER_GIFT_LOOTS = new LinkedList<>();
        this.MEGA_GIFT_LOOTS = new LinkedList<>();
        this.OMEGA_GIFT_LOOTS = new LinkedList<>();
    }

    public ItemStack randomLoot(ItemGiftBomb.Variant variant) {
        switch (variant) {
            case NORMAL:
                return getRandom(GIFT_LOOTS);
            case SUPER:
                return getRandom(SUPER_GIFT_LOOTS);
            case MEGA:
                return getRandom(MEGA_GIFT_LOOTS);
            case OMEGA:
                return getRandom(OMEGA_GIFT_LOOTS);
        }

        throw new InternalError("Unknown Gift Bomb variant: " + variant);
    }

    private ItemStack getRandom(List<ItemEntry> loottable) {
        Random rand = new Random();
        ItemStack stack = ItemStack.EMPTY;

        if (loottable == null || loottable.isEmpty())
            return stack;

        ItemEntry randomEntry = loottable.get(rand.nextInt(loottable.size()));

        try {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(randomEntry.ITEM));
            stack = new ItemStack(item, randomEntry.AMOUNT);
            if(randomEntry.NBT != null) {
                CompoundNBT nbt = JsonToNBT.getTagFromJson(randomEntry.NBT);
                stack.setTag(nbt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stack;
    }

}

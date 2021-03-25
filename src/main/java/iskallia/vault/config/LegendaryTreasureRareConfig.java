package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.SingleItemEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LegendaryTreasureRareConfig extends Config {

    @Expose
    public List<SingleItemEntry> ITEMS = new ArrayList<>();

    @Override
    public String getName() {
        return "legendary_treasure_rare";
    }

    @Override
    protected void reset() {
        ITEMS.add(new SingleItemEntry("minecraft:apple", "{display:{Name:'{\"text\":\"Fancy Apple\"}'}}"));
        ITEMS.add(new SingleItemEntry("minecraft:golden_sword", "{Enchantments:[{id:\"minecraft:sharpness\",lvl:5s}]}"));
    }

    public ItemStack getRandom() {
        Random rand = new Random();
        ItemStack stack = ItemStack.EMPTY;

        SingleItemEntry singleItemEntry = ITEMS.get(rand.nextInt(ITEMS.size()));

        try {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(singleItemEntry.ITEM));
            stack = new ItemStack(item);
            CompoundNBT nbt = JsonToNBT.parseTag(singleItemEntry.NBT);
            stack.setTag(nbt);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return stack;
    }

}

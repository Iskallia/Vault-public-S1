package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.SingleItemEntry;
import iskallia.vault.util.StatueType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class StatueLootConfig extends Config {

    @Expose private int MAX_ACCELERATION_CHIPS;
    @Expose private HashMap<Integer, Integer> INTERVAL_DECREASE_PER_CHIP = new HashMap<>();

    @Expose private List<SingleItemEntry> GIFT_NORMAL_STATUE_LOOT;
    @Expose private int GIFT_NORMAL_STATUE_INTERVAL;
    @Expose private List<SingleItemEntry> GIFT_MEGA_STATUE_LOOT;
    @Expose private int GIFT_MEGA_STATUE_INTERVAL;
    @Expose private List<SingleItemEntry> VAULT_BOSS_STATUE_LOOT;
    @Expose private int VAULT_BOSS_STATUE_INTERVAL;
    @Expose private List<SingleItemEntry> ARENA_CHAMPION_STATUE_LOOT;
    @Expose private int ARENA_CHAMPION_STATUE_INTERVAL;

    @Override
    public String getName() {
        return "statue_loot";
    }

    @Override
    protected void reset() {
        this.MAX_ACCELERATION_CHIPS = 4;
        this.INTERVAL_DECREASE_PER_CHIP.put(1, 50);
        this.INTERVAL_DECREASE_PER_CHIP.put(2, 100);
        this.INTERVAL_DECREASE_PER_CHIP.put(3, 200);
        this.INTERVAL_DECREASE_PER_CHIP.put(4, 500);

        this.GIFT_NORMAL_STATUE_LOOT = new LinkedList<>();
        this.GIFT_NORMAL_STATUE_LOOT.add(new SingleItemEntry("minecraft:apple", "{display:{Name:'{\"text\":\"Fancy Apple\"}'}}"));
        this.GIFT_NORMAL_STATUE_LOOT.add(new SingleItemEntry("minecraft:wooden_sword", "{Enchantments:[{id:\"minecraft:sharpness\",lvl:10s}]}"));
        this.GIFT_NORMAL_STATUE_INTERVAL = 500;

        this.GIFT_MEGA_STATUE_LOOT = new LinkedList<>();
        this.GIFT_MEGA_STATUE_LOOT.add(new SingleItemEntry("minecraft:golden_apple", "{display:{Name:'{\"text\":\"Fancier Apple\"}'}}"));
        this.GIFT_MEGA_STATUE_LOOT.add(new SingleItemEntry("minecraft:diamond_sword", "{Enchantments:[{id:\"minecraft:sharpness\",lvl:10s}]}"));
        this.GIFT_MEGA_STATUE_INTERVAL = 1000;
        this.VAULT_BOSS_STATUE_LOOT = new LinkedList<>();
        this.VAULT_BOSS_STATUE_LOOT.add(new SingleItemEntry("minecraft:enchanted_golden_apple", "{display:{Name:'{\"text\":\"Fanciest Apple\"}'}}"));
        this.VAULT_BOSS_STATUE_LOOT.add(new SingleItemEntry("minecraft:netherite_sword", "{Enchantments:[{id:\"minecraft:sharpness\",lvl:10s}]}"));
        this.VAULT_BOSS_STATUE_INTERVAL = 500;
        this.ARENA_CHAMPION_STATUE_LOOT = new LinkedList<>();
        this.ARENA_CHAMPION_STATUE_LOOT.add(new SingleItemEntry("minecraft:enchanted_golden_apple", "{display:{Name:'{\"text\":\"Fanciestest Apple\"}'}}"));
        this.ARENA_CHAMPION_STATUE_LOOT.add(new SingleItemEntry("minecraft:netherite_sword", "{display:{Name:'{\"text\":\"Over 9000!\"}'},Enchantments:[{id:\"minecraft:sharpness\",lvl:9001s}]}"));
        this.ARENA_CHAMPION_STATUE_INTERVAL = 500;
    }

    public ItemStack randomLoot(StatueType type) {
        switch (type) {
            case GIFT_NORMAL:
                return getRandom(GIFT_NORMAL_STATUE_LOOT);
            case GIFT_MEGA:
                return getRandom(GIFT_MEGA_STATUE_LOOT);
            case VAULT_BOSS:
                return getRandom(VAULT_BOSS_STATUE_LOOT);
            case ARENA_CHAMPION:
                return getRandom(ARENA_CHAMPION_STATUE_LOOT);
        }

        throw new InternalError("Unknown Statue variant: " + type);
    }

    public int getInterval(StatueType type) {
        switch (type) {
            case GIFT_NORMAL:
                return GIFT_NORMAL_STATUE_INTERVAL;
            case GIFT_MEGA:
                return GIFT_MEGA_STATUE_INTERVAL;
            case VAULT_BOSS:
                return VAULT_BOSS_STATUE_INTERVAL;
            case ARENA_CHAMPION:
                return ARENA_CHAMPION_STATUE_INTERVAL;
        }

        throw new InternalError("Unknown Statue variant: " + type);
    }

    public int getMaxAccelerationChips() {
        return this.MAX_ACCELERATION_CHIPS;
    }

    private ItemStack getRandom(List<SingleItemEntry> loottable) {
        Random rand = new Random();
        ItemStack stack = ItemStack.EMPTY;

        if (loottable == null || loottable.isEmpty())
            return stack;

        SingleItemEntry randomEntry = loottable.get(rand.nextInt(loottable.size()));

        try {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(randomEntry.ITEM));
            stack = new ItemStack(item);
            if (randomEntry.NBT != null) {
                CompoundNBT nbt = JsonToNBT.getTagFromJson(randomEntry.NBT);
                stack.setTag(nbt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stack;
    }

    public void dumpAll(PlayerEntity player) {
        for (SingleItemEntry entry : GIFT_NORMAL_STATUE_LOOT) {
            player.dropItem(getItem(entry), false);
        }
        for (SingleItemEntry entry : GIFT_MEGA_STATUE_LOOT) {
            player.dropItem(getItem(entry), false);
        }
        for (SingleItemEntry entry : ARENA_CHAMPION_STATUE_LOOT) {
            player.dropItem(getItem(entry), false);
        }
        for (SingleItemEntry entry : VAULT_BOSS_STATUE_LOOT) {
            player.dropItem(getItem(entry), false);
        }
    }

    private ItemStack getItem(SingleItemEntry entry) {
        ItemStack stack = ItemStack.EMPTY;
        try {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.ITEM));
            stack = new ItemStack(item);
            if (entry.NBT != null) {
                CompoundNBT nbt = JsonToNBT.getTagFromJson(entry.NBT);
                stack.setTag(nbt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stack;
    }

    public int getIntervalDecrease(int chipCount) {
        return INTERVAL_DECREASE_PER_CHIP.get(chipCount);
    }
}

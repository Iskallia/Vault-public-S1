package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.Vault;
import iskallia.vault.altar.RequiredItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class VaultAltarConfig extends Config {

    @Expose
    public List<AltarConfigItem> ITEMS = new ArrayList<>();
    @Expose
    public float PULL_SPEED;
    @Expose
    public double PLAYER_RANGE_CHECK;
    @Expose
    public double ITEM_RANGE_CHECK;
    @Expose
    public int INFUSION_TIME;


    private Random rand = new Random();

    @Override
    public String getName() {
        return "vault_altar";
    }

    @Override
    protected void reset() {

        ITEMS.add(new AltarConfigItem("minecraft:cobblestone", 1000, 6000));
        ITEMS.add(new AltarConfigItem("minecraft:gold_ingot", 300, 900));
        ITEMS.add(new AltarConfigItem("minecraft:iron_ingot", 400, 1300));
        ITEMS.add(new AltarConfigItem("minecraft:sugar_cane", 800, 1600));
        ITEMS.add(new AltarConfigItem("minecraft:oak_log", 400, 800));
        ITEMS.add(new AltarConfigItem("minecraft:spruce_log", 400, 800));
        ITEMS.add(new AltarConfigItem("minecraft:acacia_log", 400, 800));
        ITEMS.add(new AltarConfigItem("minecraft:jungle_log", 400, 800));
        ITEMS.add(new AltarConfigItem("minecraft:dark_oak_log", 400, 800));
        ITEMS.add(new AltarConfigItem("minecraft:apple", 400, 800));
        ITEMS.add(new AltarConfigItem("minecraft:redstone", 400, 1000));
        ITEMS.add(new AltarConfigItem("minecraft:ink_sac", 300, 600));
        ITEMS.add(new AltarConfigItem("minecraft:slime_ball", 200, 800));
        ITEMS.add(new AltarConfigItem("minecraft:rotten_flesh", 500, 1500));
        ITEMS.add(new AltarConfigItem("minecraft:blaze_rod", 80, 190));
        ITEMS.add(new AltarConfigItem("minecraft:brick", 500, 1500));
        ITEMS.add(new AltarConfigItem("minecraft:bone", 500, 1500));
        ITEMS.add(new AltarConfigItem("minecraft:spider_eye", 150, 400));
        ITEMS.add(new AltarConfigItem("minecraft:melon_slice", 1000, 5000));
        ITEMS.add(new AltarConfigItem("minecraft:pumpkin", 1000, 5000));
        ITEMS.add(new AltarConfigItem("minecraft:sand", 1000, 5000));
        ITEMS.add(new AltarConfigItem("minecraft:gravel", 1000, 5000));
        ITEMS.add(new AltarConfigItem("minecraft:wheat", 1000, 2000));
        ITEMS.add(new AltarConfigItem("minecraft:wheat_seeds", 1000, 2000));
        ITEMS.add(new AltarConfigItem("minecraft:carrot", 1000, 2000));
        ITEMS.add(new AltarConfigItem("minecraft:potato", 1000, 2000));
        ITEMS.add(new AltarConfigItem("minecraft:obsidian", 100, 300));
        ITEMS.add(new AltarConfigItem("minecraft:leather", 300, 800));
        ITEMS.add(new AltarConfigItem("minecraft:string", 500, 1200));

        PULL_SPEED = 1f;
        PLAYER_RANGE_CHECK = 32d;
        ITEM_RANGE_CHECK = 8d;
        INFUSION_TIME = 5;

    }

    public List<RequiredItem> getRequiredItemsFromConfig(ServerWorld world, PlayerEntity player) {
        /*
        List<RequiredItem> requiredItems = new ArrayList<>();

        List<AltarConfigItem> configItems = new ArrayList<>(ITEMS);

        for (int i = 0; i < 4; i++) {
            AltarConfigItem configItem = configItems.remove(rand.nextInt(configItems.size()));
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(configItem.ITEM_ID));

            requiredItems.add(new RequiredItem(new ItemStack(item), 0, getRandomInt(configItem.MIN, configItem.MAX)));
        }

        return requiredItems;*/

        LootContext ctx = new LootContext.Builder(world)
                .withParameter(LootParameters.THIS_ENTITY, player)
                .withRandom(world.rand).withLuck(player.getLuck())
                .build(LootParameterSets.field_237453_h_); //Barter set

        List<ItemStack> stacks = world.getServer().getLootTableManager().getLootTableFromLocation(Vault.id("chest/altar")).generate(ctx);

        List<RequiredItem> items = stacks.stream()
                .map(stack -> new RequiredItem(new ItemStack(stack.getItem()), 0, stack.getCount()))
                .sorted(Comparator.comparingInt(o -> o.getItem().getItem().getRegistryName().hashCode()))
                .collect(Collectors.toList());

        List<RequiredItem> stackedItems = new ArrayList<>();

        RequiredItem lastItem = null;

        for(RequiredItem item: items) {
            if(lastItem == null) {
                lastItem = item;
                continue;
            }

            if(item.getItem().getItem() == lastItem.getItem().getItem()) {
                lastItem = new RequiredItem(lastItem.getItem(), 0, lastItem.getAmountRequired() + item.getAmountRequired());
            } else {
                stackedItems.add(lastItem);
                lastItem = item;
            }
        }

        stackedItems.add(lastItem);
        return stackedItems.stream().limit(4).collect(Collectors.toList());
    }

    private int getRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    public class AltarConfigItem {

        @Expose
        public String ITEM_ID;
        @Expose
        public int MIN;
        @Expose
        public int MAX;

        public AltarConfigItem(String item, int min, int max) {
            ITEM_ID = item;
            MIN = min;
            MAX = max;
        }

    }

}

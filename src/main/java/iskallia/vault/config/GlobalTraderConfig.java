package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.WeightedList;
import iskallia.vault.vending.Product;
import iskallia.vault.vending.Trade;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class GlobalTraderConfig extends Config {

    @Expose public WeightedList<Trade> POOL = new WeightedList<>();
    @Expose public int TOTAL_TRADE_COUNT;
    @Expose public int MAX_TRADES;
    @Expose public int SKIN_UPDATE_RATE_SECONDS;

    @Override
    public String getName() {
        return "global_trader";
    }

    @Override
    protected void reset() {
        SKIN_UPDATE_RATE_SECONDS = 60;
        TOTAL_TRADE_COUNT = 3;
        MAX_TRADES = 1;

        this.POOL.add(
                new Trade(
                        new Product(Items.APPLE, 8, null), null,
                        new Product(Items.GOLDEN_APPLE, 1, null)),
                20);

        this.POOL.add(
                new Trade(
                        new Product(Items.GOLDEN_APPLE, 8, null), null,
                        new Product(Items.ENCHANTED_GOLDEN_APPLE, 1, null)),
                3);

        this.POOL.add(
                new Trade(
                        new Product(Items.STONE, 64, null), null,
                        new Product(Items.COBBLESTONE, 64, null)),
                20);

        this.POOL.add(
                new Trade(
                        new Product(Items.DIORITE, 64, null), null,
                        new Product(Items.DIAMOND, 8, null)),
                20);


        CompoundNBT nbt = new CompoundNBT();
        ListNBT enchantments = new ListNBT();
        CompoundNBT knockback = new CompoundNBT();
        knockback.putString("id", "minecraft:knockback");
        knockback.putInt("lvl", 10);
        enchantments.add(knockback);
        nbt.put("Enchantments", enchantments);
        nbt.put("ench", enchantments);

        this.POOL.add(new Trade(
                        new Product(Items.ENCHANTED_GOLDEN_APPLE, 8, null), null,
                        new Product(Items.STICK, 1, nbt)),
                1);
    }

}

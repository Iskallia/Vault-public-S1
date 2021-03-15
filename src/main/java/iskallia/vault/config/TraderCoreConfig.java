package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.vending.Product;
import iskallia.vault.vending.Trade;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import java.util.ArrayList;
import java.util.List;

public class TraderCoreConfig extends Config {

    @Expose
    public List<Trade> TRADES = new ArrayList<>();

    @Override
    public String getName() {
        return "trader_core";
    }

    @Override
    protected void reset() {

        this.TRADES.add(
                new Trade(new Product(Items.APPLE, 8, null), null, new Product(Items.GOLDEN_APPLE, 1, null)));
        this.TRADES.add(new Trade(new Product(Items.GOLDEN_APPLE, 8, null), null,
                new Product(Items.ENCHANTED_GOLDEN_APPLE, 1, null)));
        CompoundNBT nbt = new CompoundNBT();
        ListNBT enchantments = new ListNBT();
        CompoundNBT knockback = new CompoundNBT();
        knockback.putString("id", "minecraft:knockback");
        knockback.putInt("lvl", 10);
        enchantments.add(knockback);
        nbt.put("Enchantments", enchantments);

        nbt.put("ench", enchantments);

        this.TRADES
                .add(new Trade(new Product(Items.ENCHANTED_GOLDEN_APPLE, 8, null), null, new Product(Items.STICK, 1, nbt)));
    }

    public static class TraderCoreCommonConfig extends TraderCoreConfig {

        @Override
        public String getName() {
            return "trader_core_common";
        }
    }
    public static class TraderCoreOmegaConfig extends TraderCoreConfig {

        @Override
        public String getName() {
            return "trader_core_omega";
        }
    }
    public static class TraderCoreRaffleConfig extends TraderCoreConfig {

        @Override
        public String getName() {
            return "trader_core_raffle";
        }
    }


}

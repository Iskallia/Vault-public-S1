package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.vending.Product;
import iskallia.vault.vending.Trade;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import java.util.ArrayList;
import java.util.List;

public class VaultVendingConfig extends Config {

    @Expose
    public int MAX_CIRCUITS;
    @Expose
    public List<Trade> TRADES = new ArrayList<>();

    @Override
    public String getName() {
        return "vault_vending";
    }

    @Override
    protected void reset() {
        this.MAX_CIRCUITS = 16;

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


}

package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class RelicItem extends Item {

    public RelicItem(ItemGroup group, ResourceLocation id) {
        super(new Item.Properties()
                .group(group)
                .maxStackSize(64));

        this.setRegistryName(id);
    }

    public static ItemStack withCustomModelData(int customModelData) {
        ItemStack itemStack = new ItemStack(ModItems.VAULT_RELIC);

        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("CustomModelData", customModelData);
        itemStack.setTag(nbt);

        return itemStack;
    }

}

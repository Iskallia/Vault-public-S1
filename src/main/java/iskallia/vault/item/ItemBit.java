package iskallia.vault.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;

import net.minecraft.item.Item.Properties;

public class ItemBit extends Item {

    protected int value;

    public ItemBit(ItemGroup group, ResourceLocation id, int value) {
        super(new Properties()
                .tab(group)
                .stacksTo(64));

        this.setRegistryName(id);

        this.value = value;
    }

    public int getValue() {
        return value;
    }

}

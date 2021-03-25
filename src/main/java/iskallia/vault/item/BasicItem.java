package iskallia.vault.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.minecraft.item.Item.Properties;

public class BasicItem extends Item {

    public BasicItem(ResourceLocation id, Properties properties) {
        super(properties);

        this.setRegistryName(id);
    }

}

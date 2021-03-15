package iskallia.vault.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BasicItem extends Item {

    public BasicItem(ResourceLocation id, Properties properties) {
        super(properties);

        this.setRegistryName(id);
    }

}

package iskallia.vault.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;

import net.minecraft.item.Item.Properties;

public class ItemSkillShard extends Item {

    public ItemSkillShard(ItemGroup group, ResourceLocation id) {
        super(new Properties()
                .tab(group)
                .stacksTo(64));

        this.setRegistryName(id);
    }

}

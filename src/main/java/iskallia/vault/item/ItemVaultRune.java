package iskallia.vault.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;

public class ItemVaultRune extends Item {

    public ItemVaultRune(ItemGroup group, ResourceLocation id) {
        super(new Properties()
                .group(group)
                .maxStackSize(1));

        this.setRegistryName(id);
    }

}

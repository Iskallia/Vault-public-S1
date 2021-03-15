package iskallia.vault.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;

public class ObeliskInscriptionItem extends Item {

	public ObeliskInscriptionItem(ItemGroup group, ResourceLocation id) {
		super(new Properties().group(group));
		this.setRegistryName(id);
	}

}

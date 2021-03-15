package iskallia.vault.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class VaultBedrockBlock extends Block {

	public VaultBedrockBlock() {
		super(AbstractBlock.Properties.create(Material.ROCK)
				.hardnessAndResistance(-1.0F, 3600000.0F)
				.noDrops().setAllowsSpawn((a, b, c, d) -> false));
	}

}

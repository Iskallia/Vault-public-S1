package iskallia.vault.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

import net.minecraft.block.AbstractBlock.Properties;

public class StatueDragonHeadBlock extends Block {

    public StatueDragonHeadBlock() {
        super(Properties.of(Material.STONE, MaterialColor.STONE)
                .strength(1.0F, 3600000.0F)
                .noOcclusion()
                .noCollission());
    }

}

package iskallia.vault.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class StatueDragonHeadBlock extends Block {

    public StatueDragonHeadBlock() {
        super(Properties.create(Material.ROCK, MaterialColor.STONE)
                .hardnessAndResistance(1.0F, 3600000.0F)
                .notSolid()
                .doesNotBlockMovement());
    }

}

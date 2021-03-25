package iskallia.vault.block;

import iskallia.vault.init.ModSounds;
import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

import net.minecraft.block.AbstractBlock.Properties;

public class VaultOreBlock extends OreBlock {

    public VaultOreBlock() {
        super(Properties.of(Material.STONE, MaterialColor.DIAMOND)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> 9)
                .strength(3f, 3f)
                .sound(ModSounds.VAULT_GEM)
        );
    }

    @Override
    protected int xpOnDrop(Random rand) {
        return MathHelper.nextInt(rand, 3, 7);
    }

}

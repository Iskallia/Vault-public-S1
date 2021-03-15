package iskallia.vault.block;

import iskallia.vault.init.ModSounds;
import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class VaultOreBlock extends OreBlock {

    public VaultOreBlock() {
        super(Properties.create(Material.ROCK, MaterialColor.DIAMOND)
                .setRequiresTool()
                .setLightLevel(state -> 9)
                .hardnessAndResistance(3f, 3f)
                .sound(ModSounds.VAULT_GEM)
        );
    }

    @Override
    protected int getExperience(Random rand) {
        return MathHelper.nextInt(rand, 3, 7);
    }

}

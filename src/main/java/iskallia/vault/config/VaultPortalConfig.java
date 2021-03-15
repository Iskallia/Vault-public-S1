package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultPortalConfig extends Config {

    @Expose
    public String[] VALID_BLOCKS;


    @Override
    public String getName() {
        return "vault_portal";
    }

    @Override
    protected void reset() {

        VALID_BLOCKS = new String[]{
                Blocks.BLACKSTONE.getRegistryName().toString(),
                Blocks.POLISHED_BLACKSTONE.getRegistryName().toString(),
                Blocks.POLISHED_BLACKSTONE_BRICKS.getRegistryName().toString(),
                Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getRegistryName().toString(),
        };

    }

    public Block[] getValidFrameBlocks() {
        Block[] blocks = new Block[VALID_BLOCKS.length];
        int i = 0;
        for (String s : VALID_BLOCKS) {
            ResourceLocation res = new ResourceLocation(s);
            blocks[i++] = ForgeRegistries.BLOCKS.getValue(res);
        }
        return blocks;
    }


}

package iskallia.vault.block;

import iskallia.vault.block.entity.CapacitorTileEntity;
import iskallia.vault.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

public class CapacitorBlock extends Block {

    public CapacitorBlock() {
        super(Properties.of(Material.METAL, MaterialColor.METAL)
                .strength(5.0F, 3600000.0F)
                .sound(SoundType.METAL));

    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModBlocks.CAPACITOR_TILE_ENTITY.create();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isClientSide) return super.use(state, world, pos, player, hand, hit);
        CapacitorTileEntity te = getCapacitorTileEntity(world, pos);
        if (te != null) {
            player.displayClientMessage(new StringTextComponent("Energy Stored: " + te.getEnergyStorage().getEnergyStored()), true);
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    public static CapacitorTileEntity getCapacitorTileEntity(World world, BlockPos pos) {

        TileEntity tileEntity = world.getBlockEntity(pos);

        if ((!(tileEntity instanceof CapacitorTileEntity)))
            return null;

        return (CapacitorTileEntity) tileEntity;
    }

}

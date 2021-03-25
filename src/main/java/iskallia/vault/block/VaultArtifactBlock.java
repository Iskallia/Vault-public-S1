package iskallia.vault.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VaultArtifactBlock extends Block {

    public static final VoxelShape EAST_SHAPE = Block.box(15.75, 0, 0, 16, 16, 16);
    public static final VoxelShape NORTH_SHAPE = Block.box(0, 0, 0, 16, 16, 0.25);
    public static final VoxelShape WEST_SHAPE = Block.box(0, 0, 0, 0.25, 16, 16);
    public static final VoxelShape SOUTH_SHAPE = Block.box(0, 0, 15.75, 16, 16, 16);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected int order;

    public VaultArtifactBlock(int order) {
        super(AbstractBlock.Properties.of(Material.CLAY, MaterialColor.WOOD).sound(SoundType.WOOL).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH));
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(FACING)) {
            case EAST:
                return EAST_SHAPE;
            case NORTH:
                return NORTH_SHAPE;
            case WEST:
                return WEST_SHAPE;
            default:
                return SOUTH_SHAPE;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
        // TODO: Transforming thingy
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return new ArrayList<>(Collections.singletonList(new ItemStack(Registry.ITEM.getOptional(this.getRegistryName()).orElse(Items.AIR))));
    }

}

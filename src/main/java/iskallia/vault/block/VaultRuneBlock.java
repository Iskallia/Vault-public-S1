package iskallia.vault.block;

import iskallia.vault.block.entity.VaultRuneTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class VaultRuneBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty RUNE_PLACED = BooleanProperty.create("rune_placed");

    public VaultRuneBlock() {
        super(Properties.of(Material.STONE, MaterialColor.STONE)
                .strength(Float.MAX_VALUE, Float.MAX_VALUE)
                .noOcclusion());

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(RUNE_PLACED, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(RUNE_PLACED, false);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(RUNE_PLACED);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModBlocks.VAULT_RUNE_TILE_ENTITY.create();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);

            if (tileEntity instanceof VaultRuneTileEntity) {
                VaultRuneTileEntity vaultRuneTE = (VaultRuneTileEntity) tileEntity;
                String playerNick = player.getDisplayName().getString();

                if (vaultRuneTE.getBelongsTo().equals(playerNick)) {
                    ItemStack heldStack = player.getItemInHand(hand);

                    if (heldStack.getItem() == ModItems.VAULT_RUNE) {
                        BlockState blockState = world.getBlockState(pos);
                        world.setBlock(pos, blockState.setValue(RUNE_PLACED, true), 3);
                        heldStack.shrink(1);
                        ((ServerWorld) world).playSound(
                                null,
                                pos.getX(),
                                pos.getY(),
                                pos.getZ(),
                                SoundEvents.END_PORTAL_FRAME_FILL,
                                SoundCategory.BLOCKS,
                                1f, 1f
                        );
                    }

                } else {
                    StringTextComponent text = new StringTextComponent(vaultRuneTE.getBelongsTo() + " is responsible with this block.");
                    text.setStyle(Style.EMPTY.withColor(Color.fromRgb(0xFF_ff9966)));
                    player.displayClientMessage(text, true);
                }
            }
        }

        return super.use(state, world, pos, player, hand, hit);
    }

}

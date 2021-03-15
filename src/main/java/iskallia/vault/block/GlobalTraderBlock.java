package iskallia.vault.block;

import iskallia.vault.container.GlobalTraderContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.GlobalTraderData;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class GlobalTraderBlock extends VendingMachineBlock {

    public GlobalTraderBlock() {
        super(Properties.create(Material.IRON, MaterialColor.IRON)
                .hardnessAndResistance(-1.0f, 3600000.0F)
                .notSolid()
                .sound(SoundType.METAL));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.get(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return state.get(HALF) == DoubleBlockHalf.LOWER ? ModBlocks.GLOBAL_TRADER_TILE_ENTITY.create() : null;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {

    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote) {
            playOpenSound();
            return ActionResultType.SUCCESS;
        }

        ListNBT playerTrades = GlobalTraderData.get((ServerWorld) world).getPlayerTradesAsNbt(player);

        NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new StringTextComponent("Global Trader");
                    }

                    @Nullable
                    @Override
                    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        BlockState blockState = world.getBlockState(pos);
                        BlockPos position = getTileEntityPos(blockState, pos);
                        return new GlobalTraderContainer(windowId, world, position, playerInventory, playerEntity, playerTrades);
                    }
                },
                (buffer) -> {
                    CompoundNBT nbt = new CompoundNBT();
                    nbt.put("PlayerTradesList", playerTrades);
                    BlockState blockState = world.getBlockState(pos);
                    buffer.writeBlockPos(getTileEntityPos(blockState, pos));
                    buffer.writeCompoundTag(nbt);
                }
        );
        return ActionResultType.SUCCESS;
    }
}

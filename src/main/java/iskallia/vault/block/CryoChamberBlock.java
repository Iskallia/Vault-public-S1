package iskallia.vault.block;

import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemTraderCore;
import iskallia.vault.util.RenameType;
import iskallia.vault.vending.TraderCore;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class CryoChamberBlock extends Block {

    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<ChamberState> CHAMBER_STATE = EnumProperty.create("chamber_state", ChamberState.class);

    public CryoChamberBlock() {
        super(Properties.of(Material.METAL, MaterialColor.METAL)
                .strength(5.0F, 3600000.0F)
                .sound(SoundType.METAL)
                .noOcclusion()
                .isRedstoneConductor(CryoChamberBlock::isntSolid)
                .isViewBlocking(CryoChamberBlock::isntSolid));

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(CHAMBER_STATE, ChamberState.NONE));
    }

    private static boolean isntSolid(BlockState state, IBlockReader reader, BlockPos pos) {
        return false;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER)
            return true;

        return false;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER)
            return ModBlocks.CRYO_CHAMBER_TILE_ENTITY.create();

        return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();
        if (pos.getY() < 255 && world.getBlockState(pos.above()).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HALF, DoubleBlockHalf.LOWER).setValue(CHAMBER_STATE, ChamberState.NONE);
        } else {
            return null;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HALF);
        builder.add(FACING);
        builder.add(CHAMBER_STATE);
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!worldIn.isClientSide && player.isCreative()) {
            DoubleBlockHalf half = state.getValue(HALF);
            if (half == DoubleBlockHalf.UPPER) {
                BlockPos blockpos = pos.below();
                BlockState blockstate = worldIn.getBlockState(blockpos);
                if (blockstate.getBlock() == state.getBlock() && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    worldIn.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    worldIn.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }

        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf half = stateIn.getValue(HALF);
        if (facing.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
            return facingState.is(this) && facingState.getValue(HALF) != half ? stateIn.setValue(FACING, facingState.getValue(FACING)) : Blocks.AIR.defaultBlockState();
        } else {
            return half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        worldIn.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);

        if(placer != null) {
            CryoChamberTileEntity te = getCryoChamberTileEntity(worldIn, pos, state);
            te.setOwner(placer.getUUID());
            te.setMaxCores(ModConfigs.CRYO_CHAMBER.getPlayerCoreCount(placer.getDisplayName().getString()));
        }
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (worldIn.isClientSide) return;
        if (!newState.isAir()) return;

        CryoChamberTileEntity chamber = getCryoChamberTileEntity(worldIn, pos, state);
        if (chamber == null) return;

        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            dropCryoChamber(worldIn, pos, chamber);
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    private void dropCryoChamber(World world, BlockPos pos, CryoChamberTileEntity te) {
        ItemStack chamberStack = new ItemStack(ModBlocks.CRYO_CHAMBER);
        CompoundNBT nbt = chamberStack.getOrCreateTag();
        nbt.put("BlockEntityTag", te.serializeNBT());
        chamberStack.setTag(nbt);
        ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), chamberStack);
        world.addFreshEntity(entity);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            CryoChamberTileEntity chamber = getCryoChamberTileEntity(world, pos, state);
            ItemStack heldStack = player.getItemInHand(hand);

            if (chamber == null) return ActionResultType.SUCCESS;
            if (heldStack == ItemStack.EMPTY && player.isShiftKeyDown()) {
                if(chamber.getEternal() == null) return ActionResultType.SUCCESS;
                CompoundNBT nbt = new CompoundNBT();
                nbt.putInt("RenameType", RenameType.CRYO_CHAMBER.ordinal());
                nbt.put("Data", chamber.getRenameNBT());

                NetworkHooks.openGui(
                        (ServerPlayerEntity) player,
                        new INamedContainerProvider() {
                            @Override
                            public ITextComponent getDisplayName() {
                                return new StringTextComponent("Cryo Chamber");
                            }

                            @Nullable
                            @Override
                            public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                                return new RenamingContainer(windowId, nbt);
                            }
                        },
                        (buffer) -> {
                            buffer.writeNbt(nbt);
                        }
                );
                return ActionResultType.SUCCESS;
                // add chip
            }
            if (heldStack.getItem() == ModItems.TRADER_CORE) {
                TraderCore coreToInsert = ItemTraderCore.getCoreFromStack(heldStack);
                if (chamber.getOwner() == null) {
                    chamber.setOwner(player.getUUID());
                    chamber.setMaxCores(ModConfigs.CRYO_CHAMBER.getPlayerCoreCount(player.getDisplayName().getString()));
                }

                if (chamber.addTraderCore(coreToInsert)) {
                    heldStack.shrink(1);
                    chamber.sendUpdates();
                }
            } else {
                chamber.onItemClicked(heldStack, player);
                chamber.sendUpdates();
            }
        }

        return ActionResultType.SUCCESS;
    }

    public static BlockPos getCryoChamberPos(BlockState state, BlockPos pos) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER
                ? pos.below() : pos;
    }

    public static CryoChamberTileEntity getCryoChamberTileEntity(World world, BlockPos pos, BlockState state) {
        BlockPos cryoChamberPos = getCryoChamberPos(state, pos);

        TileEntity tileEntity = world.getBlockEntity(cryoChamberPos);

        if ((!(tileEntity instanceof CryoChamberTileEntity)))
            return null;

        return (CryoChamberTileEntity) tileEntity;
    }

    public enum ChamberState implements IStringSerializable {
        NONE("none"),
        GENERATOR("generator"),
        MINER("miner"),
        LOOTER("looter");

        private String name;

        ChamberState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

}

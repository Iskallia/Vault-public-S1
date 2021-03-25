package iskallia.vault.block;

import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.PlayerVaultAltarData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class VaultAltarBlock extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public VaultAltarBlock() {
        super(Properties.of(Material.STONE, MaterialColor.DIAMOND).requiresCorrectToolForDrops().strength(3f, 3600000.0F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.FALSE));

    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(POWERED, Boolean.FALSE);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModBlocks.VAULT_ALTAR_TILE_ENTITY.create();
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isClientSide || handIn != Hand.MAIN_HAND) return ActionResultType.SUCCESS;
        ItemStack heldItem = player.getMainHandItem();

        VaultAltarTileEntity altar = getAltarTileEntity(worldIn, pos);
        if (altar == null || altar.isInfusing()) return ActionResultType.SUCCESS;

        if (player.isShiftKeyDown() && altar.containsVaultRock()) {
            return onRemoveVaultRock(player, altar);
        }

        if (heldItem.getItem() != ModItems.VAULT_ROCK) return ActionResultType.SUCCESS;

        PlayerVaultAltarData data = PlayerVaultAltarData.get((ServerWorld) worldIn);

        return onAddVaultRock((ServerWorld) worldIn, player, altar, heldItem, data);
    }

    private ActionResultType onAddVaultRock(ServerWorld worldIn, PlayerEntity player, VaultAltarTileEntity altar, ItemStack heldItem, PlayerVaultAltarData data) {
        AltarInfusionRecipe recipe = data.getRecipe(worldIn, player);

        altar.setRecipe(recipe);
        altar.setContainsVaultRock(true);

        if (!player.isCreative()) heldItem.setCount(heldItem.getCount() - 1);
        altar.sendUpdates();
        return ActionResultType.SUCCESS;
    }

    private ActionResultType onRemoveVaultRock(PlayerEntity player, VaultAltarTileEntity altar) {
        altar.setContainsVaultRock(false);
        altar.sendUpdates();

        player.setItemInHand(Hand.MAIN_HAND, new ItemStack(ModItems.VAULT_ROCK));
        return ActionResultType.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (worldIn.isClientSide) return;
        boolean powered = worldIn.hasNeighborSignal(pos);
        if (powered != state.getValue(POWERED)) {
            if (powered) {
                VaultAltarTileEntity altar = getAltarTileEntity(worldIn, pos);
                if (altar != null && altar.containsVaultRock()) {
                    if (altar.isInfusing() || altar.getOwner() == null) return;
                    PlayerVaultAltarData data = PlayerVaultAltarData.get((ServerWorld) worldIn);
                    if (data.hasRecipe(altar.getOwner())) {
                        AltarInfusionRecipe recipe = data.getRecipe(altar.getOwner());
                        if (recipe.isComplete()) {
                            data = data.remove(altar.getOwner());
                            altar.startInfusionTimer(ModConfigs.VAULT_ALTAR.INFUSION_TIME);
                            altar.setInfusing(true);
                        }
                    }
                }
            }
        }
        worldIn.setBlock(pos, state.setValue(POWERED, powered), 3);
    }


    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    private VaultAltarTileEntity getAltarTileEntity(World worldIn, BlockPos pos) {
        TileEntity te = worldIn.getBlockEntity(pos);
        if (te == null || !(te instanceof VaultAltarTileEntity))
            return null;
        VaultAltarTileEntity altar = (VaultAltarTileEntity) worldIn.getBlockEntity(pos);
        return altar;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        VaultAltarTileEntity altar = getAltarTileEntity(world, pos);
        if (altar == null) return;

        if (newState.getBlock() != Blocks.AIR) return;

        if (altar.containsVaultRock()) {
            ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.VAULT_ROCK));
            world.addFreshEntity(entity);
        }
        ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModBlocks.VAULT_ALTAR));
        world.addFreshEntity(entity);

        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (worldIn.isClientSide) return;

        VaultAltarTileEntity altar = (VaultAltarTileEntity) worldIn.getBlockEntity(pos);
        if (altar == null || !(placer instanceof PlayerEntity)) return;

        altar.setOwner(placer.getUUID());

        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }
}

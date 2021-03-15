package iskallia.vault.block;

import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.altar.RequiredItem;
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
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import java.util.List;

public class VaultAltarBlock extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public VaultAltarBlock() {
        super(Properties.create(Material.ROCK, MaterialColor.DIAMOND).setRequiresTool().hardnessAndResistance(3f, 3600000.0F).notSolid());
        this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, Boolean.FALSE));

    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(POWERED, Boolean.FALSE);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
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
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote)
            return ActionResultType.SUCCESS;
        if (handIn != Hand.MAIN_HAND)
            return ActionResultType.SUCCESS;

        VaultAltarTileEntity altar = getAltarTileEntity(worldIn, pos);
        if (altar == null)
            return ActionResultType.SUCCESS;

        //infusion in process.. do nothing
        if (altar.getInfusionTimer() != -1) return ActionResultType.SUCCESS;

        //remove vault rock
        if (player.isSneaking() && altar.containsVaultRock() && player.getHeldItemMainhand().getItem() == Items.AIR) {

            player.setHeldItem(Hand.MAIN_HAND, new ItemStack(ModItems.VAULT_ROCK));
            altar.setContainsVaultRock(false);

            altar.sendUpdates();
            return ActionResultType.SUCCESS;
        }

        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.getItem() != ModItems.VAULT_ROCK)
            return ActionResultType.SUCCESS;

        PlayerVaultAltarData data = PlayerVaultAltarData.get((ServerWorld) worldIn);

        // player has no recipe, give them one.
        if (!data.getRecipes().containsKey(player.getUniqueID())) {
            List<RequiredItem> items = ModConfigs.VAULT_ALTAR.getRequiredItemsFromConfig((ServerWorld)worldIn, player);
            data.add(player.getUniqueID(), new AltarInfusionRecipe(player.getUniqueID(), items));
        }

        altar.setContainsVaultRock(true);

        heldItem.setCount(heldItem.getCount() - 1);
        altar.sendUpdates();
        return ActionResultType.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (worldIn.isRemote) return;
        boolean powered = worldIn.isBlockPowered(pos);
        if (powered != state.get(POWERED)) {
            if (powered) {
                VaultAltarTileEntity altar = getAltarTileEntity(worldIn, pos);
                if (altar != null && altar.containsVaultRock()) {
                    if (altar.getInfusionTimer() != -1) return;
                    PlayerEntity player = worldIn.getClosestPlayer(altar.getPos().getX(), altar.getPos().getY(), altar.getPos().getZ(), ModConfigs.VAULT_ALTAR.PLAYER_RANGE_CHECK, null);
                    if (player != null) {
                        PlayerVaultAltarData data = PlayerVaultAltarData.get((ServerWorld) worldIn);
                        AltarInfusionRecipe recipe = data.getRecipe(player);
                        if (recipe != null && recipe.isComplete()) {
                            data.remove(player.getUniqueID());
                            altar.startInfusionTimer(ModConfigs.VAULT_ALTAR.INFUSION_TIME);
                        }
                    }
                }
            }
        }
        worldIn.setBlockState(pos, state.with(POWERED, powered), 3);
    }


    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    private VaultAltarTileEntity getAltarTileEntity(World worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te == null || !(te instanceof VaultAltarTileEntity))
            return null;
        VaultAltarTileEntity altar = (VaultAltarTileEntity) worldIn.getTileEntity(pos);
        return altar;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        VaultAltarTileEntity altar = getAltarTileEntity(world, pos);
        if (altar == null) return;

        if (newState.getBlock() != Blocks.AIR) return;

        if (altar.containsVaultRock()) {
            ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.VAULT_ROCK));
            world.addEntity(entity);
        }
        ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModBlocks.VAULT_ALTAR));
        world.addEntity(entity);

        super.onReplaced(state, world, pos, newState, isMoving);
    }
}

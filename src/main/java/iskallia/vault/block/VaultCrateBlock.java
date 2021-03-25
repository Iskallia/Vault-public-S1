package iskallia.vault.block;

import iskallia.vault.Vault;
import iskallia.vault.block.entity.VaultCrateTileEntity;
import iskallia.vault.container.VaultCrateContainer;
import iskallia.vault.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class VaultCrateBlock extends Block {


    public VaultCrateBlock() {
        super(Properties.of(Material.METAL, MaterialColor.METAL).strength(2.0F, 3600000.0F).sound(SoundType.METAL));
    }

    public static ItemStack getCrateWithLoot(VaultCrateBlock crateType, NonNullList<ItemStack> items) {
        if(items.size() > 54) {
            Vault.LOGGER.error("Attempted to get a crate with more than 54 items. Check crate loot table.");
            items = NonNullList.of(ItemStack.EMPTY, items.stream().limit(54).toArray(ItemStack[]::new));
        }

        ItemStack crate = new ItemStack(crateType);
        CompoundNBT nbt = new CompoundNBT();
        ItemStackHelper.saveAllItems(nbt, items);
        if (!nbt.isEmpty()) {
            crate.addTagElement("BlockEntityTag", nbt);
        }
        return crate;
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModBlocks.VAULT_CRATE_TILE_ENTITY.create();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof VaultCrateTileEntity) {
                INamedContainerProvider containerProvider = new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("container.vault.vault_crate");
                    }

                    @Override
                    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        return new VaultCrateContainer(i, world, pos, playerInventory, playerEntity);
                    }
                };
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Our named container provider is missing!");
            }
        }
        return ActionResultType.SUCCESS;
    }

    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if (worldIn.isClientSide) super.playerWillDestroy(worldIn, pos, state, player);

        VaultCrateBlock block = getBlockVariant();
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof VaultCrateTileEntity) {
            VaultCrateTileEntity crate = (VaultCrateTileEntity) tileentity;

            ItemStack itemstack = new ItemStack(block);
            CompoundNBT compoundnbt = crate.saveToNbt();
            if (!compoundnbt.isEmpty()) {
                itemstack.addTagElement("BlockEntityTag", compoundnbt);
            }

            ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, itemstack);
            itementity.setDefaultPickUpDelay();
            worldIn.addFreshEntity(itementity);

        }

        super.playerWillDestroy(worldIn, pos, state, player);
    }

    private VaultCrateBlock getBlockVariant() {
        if (this.getBlock() == ModBlocks.VAULT_CRATE) return ModBlocks.VAULT_CRATE;
        else return ModBlocks.VAULT_CRATE_ARENA;
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (worldIn.isClientSide) return;

        CompoundNBT compoundnbt = stack.getTagElement("BlockEntityTag");
        if (compoundnbt == null) return;

        VaultCrateTileEntity crate = getCrateTileEntity(worldIn, pos);
        if (crate == null) return;

        crate.loadFromNBT(compoundnbt);


        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    private VaultCrateTileEntity getCrateTileEntity(World worldIn, BlockPos pos) {
        TileEntity te = worldIn.getBlockEntity(pos);
        if (te == null || !(te instanceof VaultCrateTileEntity))
            return null;
        VaultCrateTileEntity crate = (VaultCrateTileEntity) worldIn.getBlockEntity(pos);
        return crate;
    }
}

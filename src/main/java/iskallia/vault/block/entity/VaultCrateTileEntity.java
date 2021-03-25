package iskallia.vault.block.entity;

import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VaultCrateTileEntity extends TileEntity {

    private ItemStackHandler itemHandler = createHandler();
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.itemHandler);

    public VaultCrateTileEntity() {
        super(ModBlocks.VAULT_CRATE_TILE_ENTITY);
    }


    public void sendUpdates() {
        this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        this.level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
        setChanged();
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.put("inv", itemHandler.serializeNBT());
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        nbt.getCompound("inv").remove("Size");
        itemHandler.deserializeNBT(nbt.getCompound("inv"));
        super.load(state, nbt);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(54) {
            @Override
            protected void onContentsChanged(int slot) {
                sendUpdates();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if(Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock ||
                        Block.byItem(stack.getItem()) instanceof VaultCrateBlock) {
                    return false;
                }
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    public CompoundNBT saveToNbt() {
        return itemHandler.serializeNBT();
    }

    public void loadFromNBT(CompoundNBT nbt) {
        itemHandler.deserializeNBT(nbt);
    }
}

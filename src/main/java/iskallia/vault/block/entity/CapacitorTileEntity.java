package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapacitorTileEntity extends TileEntity {


    private CapacitorEnergyStorage energyStorage = createEnergyStorage();

    private CapacitorEnergyStorage createEnergyStorage() {
        return new CapacitorEnergyStorage(1000000000, 10000) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    public CapacitorEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    private LazyOptional<IEnergyStorage> handler = LazyOptional.of(() -> energyStorage);

    public CapacitorTileEntity() {
        super(ModBlocks.CAPACITOR_TILE_ENTITY);
    }


    public void sendUpdates() {
        this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        this.level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
        setChanged();
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.put("energy", energyStorage.serializeNBT());
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        energyStorage.deserializeNBT(nbt.getCompound("energy"));
        super.load(state, nbt);
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    public static class CapacitorEnergyStorage extends EnergyStorage implements INBTSerializable<CompoundNBT> {

        public CapacitorEnergyStorage(int capacity, int maxTransfer) {
            super(capacity, maxTransfer);
        }

        protected void onEnergyChanged() {

        }

        public void setEnergy(int energy) {
            this.energy = energy;
            onEnergyChanged();
        }

        public void addEnergy(int energy) {
            this.energy += energy;
            if (this.energy > getMaxEnergyStored()) {
                this.energy = getEnergyStored();
            }
            onEnergyChanged();
        }

        public void consumeEnergy(int energy) {
            this.energy -= energy;
            if (this.energy < 0) {
                this.energy = 0;
            }
            onEnergyChanged();
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("energy", getEnergyStored());
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            setEnergy(nbt.getInt("energy"));
        }
    }


}

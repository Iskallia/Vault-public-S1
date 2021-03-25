package iskallia.vault.block.entity;

import iskallia.vault.util.SkinProfile;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

public abstract class SkinnableTileEntity extends TileEntity {

    protected SkinProfile skin;

    public SkinnableTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        skin = new SkinProfile();
    }

    public SkinProfile getSkin() {
        return skin;
    }

    protected abstract void updateSkin();

    public void sendUpdates() {
        this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 0b11);
        this.level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
        setChanged();
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        load(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        handleUpdateTag(getBlockState(), nbt);
    }
}

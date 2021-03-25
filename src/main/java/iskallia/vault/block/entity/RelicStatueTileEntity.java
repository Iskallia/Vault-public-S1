package iskallia.vault.block.entity;

import iskallia.vault.Vault;
import iskallia.vault.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RelicStatueTileEntity extends TileEntity {

    protected ResourceLocation relicSet = Vault.id("none");

    public RelicStatueTileEntity() {
        super(ModBlocks.RELIC_STATUE_TILE_ENTITY);
    }

    public ResourceLocation getRelicSet() {
        return relicSet;
    }

    public void setRelicSet(ResourceLocation relicSet) {
        this.relicSet = relicSet;
    }

    public void sendUpdates() {
        this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        this.level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
        setChanged();
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putString("RelicSet", this.relicSet.toString());
        return super.save(nbt);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        this.relicSet = new ResourceLocation(nbt.getString("RelicSet"));
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        nbt.putString("RelicSet", this.relicSet.toString());
        return nbt;
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

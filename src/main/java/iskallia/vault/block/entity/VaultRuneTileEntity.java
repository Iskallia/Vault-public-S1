package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class VaultRuneTileEntity extends TileEntity {

    protected String belongsTo;

    public VaultRuneTileEntity() {
        super(ModBlocks.VAULT_RUNE_TILE_ENTITY);
        this.belongsTo = "";
    }

    public String getBelongsTo() {
        return belongsTo;
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.putString("BelongsTo", belongsTo);
        return super.write(nbt);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        this.belongsTo = nbt.getString("BelongsTo");
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        nbt.putString("BelongsTo", belongsTo);
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        this.belongsTo = nbt.getString("BelongsTo");
        super.handleUpdateTag(state, nbt);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getNbtCompound();
        handleUpdateTag(getBlockState(), tag);
    }

}

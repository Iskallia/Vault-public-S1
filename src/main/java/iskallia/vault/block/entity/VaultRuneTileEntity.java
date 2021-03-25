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
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putString("BelongsTo", belongsTo);
        return super.save(nbt);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        this.belongsTo = nbt.getString("BelongsTo");
        super.load(state, nbt);
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
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getTag();
        handleUpdateTag(getBlockState(), tag);
    }

}

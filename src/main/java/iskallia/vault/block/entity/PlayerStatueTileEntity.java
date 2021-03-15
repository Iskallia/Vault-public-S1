package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.SkinProfile;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class PlayerStatueTileEntity extends TileEntity {

    protected SkinProfile skin;
    protected boolean hasCrown;

    public PlayerStatueTileEntity() {
        super(ModBlocks.PLAYER_STATUE_TILE_ENTITY);
        skin = new SkinProfile();
    }

    public SkinProfile getSkin() {
        return skin;
    }

    public boolean hasCrown() {
        return hasCrown;
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        String nickname = skin.getLatestNickname();
        nbt.putString("PlayerNickname", nickname == null ? "" : nickname);
        nbt.putBoolean("HasCrown", hasCrown);
        return super.write(nbt);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        String nickname = nbt.getString("PlayerNickname");
        skin.updateSkin(nickname);
        this.hasCrown = nbt.getBoolean("HasCrown");
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();

        String nickname = skin.getLatestNickname();
        nbt.putString("PlayerNickname", nickname == null ? "" : nickname);
        nbt.putBoolean("HasCrown", hasCrown);

        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        read(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getNbtCompound();
        handleUpdateTag(getBlockState(), nbt);
    }
    public void sendUpdates() {
        this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 0b11);
        this.world.notifyNeighborsOfStateChange(pos, this.getBlockState().getBlock());
        markDirty();
    }

}

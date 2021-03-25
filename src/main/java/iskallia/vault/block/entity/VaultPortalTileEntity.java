package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.CrystalData;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public class VaultPortalTileEntity extends TileEntity {

    private String playerBossName;
    private CrystalData data;

    public VaultPortalTileEntity() {
        super(ModBlocks.VAULT_PORTAL_TILE_ENTITY);
    }


    public void sendUpdates() {
        this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        this.level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
        setChanged();
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        if(this.playerBossName != null)compound.putString("playerBossName", playerBossName);
        if(data != null)compound.put("Data", this.data.serializeNBT());
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        if(nbt.contains("playerBossName", Constants.NBT.TAG_STRING)) {
            this.playerBossName = nbt.getString("playerBossName");
        }

        if(nbt.contains("Data", Constants.NBT.TAG_COMPOUND)) {
            this.data = new CrystalData(null);
            this.data.deserializeNBT(nbt.getCompound("Data"));
        }

        super.load(state, nbt);
    }

    public String getPlayerBossName() {
        return playerBossName;
    }

    public CrystalData getData() {
        return this.data;
    }

    public void setPlayerBossName(String name) {
        this.playerBossName = name;
    }

    public void setCrystalData(CrystalData data) {
        this.data = data;
    }

}

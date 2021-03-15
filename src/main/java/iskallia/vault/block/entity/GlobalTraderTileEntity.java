package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.EternalsData;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.world.server.ServerWorld;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class GlobalTraderTileEntity extends SkinnableTileEntity implements ITickableTileEntity {

    private String lastName = "Player1";

    public GlobalTraderTileEntity() {
        super(ModBlocks.GLOBAL_TRADER_TILE_ENTITY);
        updateLastName();
        updateSkin();
    }

    @Override
    protected void updateSkin() {
        if (this.getWorld() == null) return;
        if (this.getWorld().isRemote) {
            skin.updateSkin(lastName);
        }
    }

    private void updateLastName() {
        if (this.getWorld() == null || this.getWorld().isRemote) return;
        List<String> names = EternalsData.get((ServerWorld) this.getWorld()).getAllEternalNamesExcept(lastName);
        if (names.isEmpty()) {
            lastName = "Player1";
        }
        Collections.shuffle(names);
        lastName = names.stream().findFirst().orElse("Player1");
        sendUpdates();
    }

    @Override
    public void tick() {
        if (this.getWorld() == null) return;
        if (this.getWorld().isRemote) {
            if (skin.getLatestNickname() == null) skin.updateSkin("Player1");
            if (skin.getLatestNickname().equalsIgnoreCase(lastName)) return;
            updateSkin();
        } else {
            if (this.getWorld().getGameTime() % 20 != 0) return;

            long time = Instant.now().getEpochSecond();

            if (time % ModConfigs.GLOBAL_TRADER.SKIN_UPDATE_RATE_SECONDS == 0) updateLastName();
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.putString("Name", lastName);
        return super.write(nbt);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        lastName = nbt.getString("Name");
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        nbt.putString("Name", lastName);
        return nbt;
    }
}

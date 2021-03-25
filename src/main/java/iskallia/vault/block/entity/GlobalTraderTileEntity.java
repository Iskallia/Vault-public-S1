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
        if (this.getLevel() == null) return;
        if (this.getLevel().isClientSide) {
            skin.updateSkin(lastName);
        }
    }

    private void updateLastName() {
        if (this.getLevel() == null || this.getLevel().isClientSide) return;
        List<String> names = EternalsData.get((ServerWorld) this.getLevel()).getAllEternalNamesExcept(lastName);
        if (names.isEmpty()) {
            lastName = "Player1";
        }
        Collections.shuffle(names);
        lastName = names.stream().findFirst().orElse("Player1");
        sendUpdates();
    }

    @Override
    public void tick() {
        if (this.getLevel() == null) return;
        if (this.getLevel().isClientSide) {
            if (skin.getLatestNickname() == null) skin.updateSkin("Player1");
            if (skin.getLatestNickname().equalsIgnoreCase(lastName)) return;
            updateSkin();
        } else {
            if (this.getLevel().getGameTime() % 20 != 0) return;

            long time = Instant.now().getEpochSecond();

            if (time % ModConfigs.GLOBAL_TRADER.SKIN_UPDATE_RATE_SECONDS == 0) updateLastName();
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putString("Name", lastName);
        return super.save(nbt);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        lastName = nbt.getString("Name");
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        nbt.putString("Name", lastName);
        return nbt;
    }
}

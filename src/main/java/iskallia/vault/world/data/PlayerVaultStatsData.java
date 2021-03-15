package iskallia.vault.world.data;

import iskallia.vault.Vault;
import iskallia.vault.skill.PlayerVaultStats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerVaultStatsData extends WorldSavedData {

    protected static final String DATA_NAME = Vault.MOD_ID + "_PlayerVaultLevels";

    private Map<UUID, PlayerVaultStats> playerMap = new HashMap<>();

    public PlayerVaultStatsData() {
        super(DATA_NAME);
    }

    public PlayerVaultStatsData(String name) {
        super(name);
    }

    public PlayerVaultStats getVaultStats(PlayerEntity player) {
        return getVaultStats(player.getUniqueID());
    }

    public PlayerVaultStats getVaultStats(UUID uuid) {
        return this.playerMap.computeIfAbsent(uuid, PlayerVaultStats::new);
    }

    /* ------------------------------- */

    public PlayerVaultStatsData setVaultLevel(ServerPlayerEntity player, int level) {
        this.getVaultStats(player).setVaultLevel(player.getServer(), level);

        markDirty();
        return this;
    }

    public PlayerVaultStatsData addVaultExp(ServerPlayerEntity player, int exp) {
        this.getVaultStats(player).addVaultExp(player.getServer(), exp);

        markDirty();
        return this;
    }

    public PlayerVaultStatsData spendSkillPts(ServerPlayerEntity player, int amount) {
        this.getVaultStats(player).spendSkillPoints(player.getServer(), amount);

        markDirty();
        return this;
    }

    public PlayerVaultStatsData spendKnowledgePts(ServerPlayerEntity player, int amount) {
        this.getVaultStats(player).spendKnowledgePoints(player.getServer(), amount);

        markDirty();
        return this;
    }

    public PlayerVaultStatsData addSkillPoint(ServerPlayerEntity player, int amount) {
        this.getVaultStats(player)
                .addSkillPoints(amount)
                .sync(player.getServerWorld().getServer());

        markDirty();
        return this;
    }

    public PlayerVaultStatsData addKnowledgePoints(ServerPlayerEntity player, int amount) {
        this.getVaultStats(player)
                .addKnowledgePoints(amount)
                .sync(player.getServerWorld().getServer());

        markDirty();
        return this;
    }

    public PlayerVaultStatsData reset(ServerPlayerEntity player) {
        this.getVaultStats(player).reset(player.getServer());

        markDirty();
        return this;
    }

    /* ------------------------------- */

    @Override
    public void read(CompoundNBT nbt) {
        ListNBT playerList = nbt.getList("PlayerEntries", Constants.NBT.TAG_STRING);
        ListNBT statEntries = nbt.getList("StatEntries", Constants.NBT.TAG_COMPOUND);

        if (playerList.size() != statEntries.size()) {
            throw new IllegalStateException("Map doesn't have the same amount of keys as values");
        }

        for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getVaultStats(playerUUID).deserializeNBT(statEntries.getCompound(i));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        ListNBT playerList = new ListNBT();
        ListNBT statsList = new ListNBT();

        this.playerMap.forEach((uuid, stats) -> {
            playerList.add(StringNBT.valueOf(uuid.toString()));
            statsList.add(stats.serializeNBT());
        });

        nbt.put("PlayerEntries", playerList);
        nbt.put("StatEntries", statsList);

        return nbt;
    }

    public static PlayerVaultStatsData get(ServerWorld world) {
        return world.getServer().func_241755_D_()
                .getSavedData().getOrCreate(PlayerVaultStatsData::new, DATA_NAME);
    }

}

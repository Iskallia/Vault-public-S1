package iskallia.vault.world.data;

import iskallia.vault.Vault;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
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

public class PlayerResearchesData extends WorldSavedData {

    protected static final String DATA_NAME = Vault.MOD_ID + "_PlayerResearches";

    private Map<UUID, ResearchTree> playerMap = new HashMap<>();

    public PlayerResearchesData() {
        super(DATA_NAME);
    }

    public PlayerResearchesData(String name) {
        super(name);
    }

    public ResearchTree getResearches(PlayerEntity player) {
        return getResearches(player.getUUID());
    }

    public ResearchTree getResearches(UUID uuid) {
        return this.playerMap.computeIfAbsent(uuid, ResearchTree::new);
    }

    /* ------------------------------- */

    public PlayerResearchesData research(ServerPlayerEntity player, Research research) {
        ResearchTree researchTree = getResearches(player);
        researchTree.research(research.getName());

        researchTree.sync(player.getServer());

        setDirty();
        return this;
    }

    public PlayerResearchesData resetResearchTree(ServerPlayerEntity player) {
        ResearchTree researchTree = getResearches(player);
        researchTree.resetAll();

        researchTree.sync(player.getServer());

        setDirty();
        return this;
    }

    /* ------------------------------- */

    @Override
    public void load(CompoundNBT nbt) {
        ListNBT playerList = nbt.getList("PlayerEntries", Constants.NBT.TAG_STRING);
        ListNBT researchesList = nbt.getList("ResearchEntries", Constants.NBT.TAG_COMPOUND);

        if (playerList.size() != researchesList.size()) {
            throw new IllegalStateException("Map doesn't have the same amount of keys as values");
        }

        for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getResearches(playerUUID).deserializeNBT(researchesList.getCompound(i));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        ListNBT playerList = new ListNBT();
        ListNBT researchesList = new ListNBT();

        this.playerMap.forEach((uuid, researchTree) -> {
            playerList.add(StringNBT.valueOf(uuid.toString()));
            researchesList.add(researchTree.serializeNBT());
        });

        nbt.put("PlayerEntries", playerList);
        nbt.put("ResearchEntries", researchesList);

        return nbt;
    }

    public static PlayerResearchesData get(ServerWorld world) {
        return world.getServer().overworld()
                .getDataStorage().computeIfAbsent(PlayerResearchesData::new, DATA_NAME);
    }

}

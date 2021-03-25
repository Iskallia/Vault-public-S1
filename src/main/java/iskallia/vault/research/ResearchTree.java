package iskallia.vault.research;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ResearchTreeMessage;
import iskallia.vault.research.type.Research;
import iskallia.vault.util.NetcodeUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ResearchTree implements INBTSerializable<CompoundNBT> {

    protected UUID playerUUID;
    protected List<String> researchesDone;

    public ResearchTree(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.researchesDone = new LinkedList<>();
    }

    public List<String> getResearchesDone() {
        return researchesDone;
    }

    public boolean isResearched(String researchName) {
        return this.researchesDone.contains(researchName);
    }

    public void research(String researchName) {
        this.researchesDone.add(researchName);
    }

    public void resetAll() {
        this.researchesDone.clear();
    }

    public String restrictedBy(Item item, Restrictions.Type restrictionType) {
        for (Research research : ModConfigs.RESEARCHES.getAll()) {
            if (researchesDone.contains(research.getName())) continue;
            if (research.restricts(item, restrictionType)) return research.getName();
        }
        return null;
    }

    public String restrictedBy(Block block, Restrictions.Type restrictionType) {
        for (Research research : ModConfigs.RESEARCHES.getAll()) {
            if (researchesDone.contains(research.getName())) continue;
            if (research.restricts(block, restrictionType)) return research.getName();
        }
        return null;
    }

    public String restrictedBy(EntityType<?> entityType, Restrictions.Type restrictionType) {
        for (Research research : ModConfigs.RESEARCHES.getAll()) {
            if (researchesDone.contains(research.getName())) continue;
            if (research.restricts(entityType, restrictionType)) return research.getName();
        }
        return null;
    }

    public void sync(MinecraftServer server) {
        NetcodeUtils.runIfPresent(server, this.playerUUID, player -> {
            ModNetwork.CHANNEL.sendTo(
                    new ResearchTreeMessage(this, player.getUUID()),
                    player.connection.connection,
                    NetworkDirection.PLAY_TO_CLIENT
            );
        });
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putUUID("playerUUID", playerUUID);

        ListNBT researches = new ListNBT();
        for (int i = 0; i < researchesDone.size(); i++) {
            CompoundNBT research = new CompoundNBT();
            research.putString("name", researchesDone.get(i));
            researches.add(i, research);
        }
        nbt.put("researches", researches);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.playerUUID = nbt.getUUID("playerUUID");

        ListNBT researches = nbt.getList("researches", Constants.NBT.TAG_COMPOUND);
        this.researchesDone = new LinkedList<>();
        for (int i = 0; i < researches.size(); i++) {
            CompoundNBT researchNBT = researches.getCompound(i);
            String name = researchNBT.getString("name");
            this.researchesDone.add(name);
        }
    }
}

package iskallia.vault.world.data;

import iskallia.vault.Vault;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerTalentsData extends WorldSavedData {

    protected static final String DATA_NAME = Vault.MOD_ID + "_PlayerTalents";

    private Map<UUID, TalentTree> playerMap = new HashMap<>();

    public PlayerTalentsData() {
        this(DATA_NAME);
    }

    public PlayerTalentsData(String name) {
        super(name);
    }

    public TalentTree getTalents(PlayerEntity player) {
        return this.getTalents(player.getUUID());
    }

    public TalentTree getTalents(UUID uuid) {
        return this.playerMap.computeIfAbsent(uuid, TalentTree::new);
    }

    /* ------------------------------- */

    public PlayerTalentsData add(ServerPlayerEntity player, TalentNode<?>... nodes) {
        this.getTalents(player).add(player.getServer(), nodes);

        setDirty();
        return this;
    }

    public PlayerTalentsData remove(ServerPlayerEntity player, TalentNode<?>... nodes) {
        this.getTalents(player).remove(player.getServer(), nodes);

        setDirty();
        return this;
    }

    public PlayerTalentsData upgradeTalent(ServerPlayerEntity player, TalentNode<?> talentNode) {
        this.getTalents(player).upgradeTalent(player.getServer(), talentNode);

        setDirty();
        return this;
    }

    public PlayerTalentsData resetTalentTree(ServerPlayerEntity player) {
        UUID uniqueID = player.getUUID();

        TalentTree oldTalentTree = playerMap.get(uniqueID);
        if (oldTalentTree != null) {
            for (TalentNode<?> node : oldTalentTree.getNodes()) {
                if (node.isLearned())
                    node.getTalent().onRemoved(player);
            }
        }

        TalentTree talentTree = new TalentTree(uniqueID);
        this.playerMap.put(uniqueID, talentTree);

        setDirty();
        return this;
    }

    /* ------------------------------- */

    public PlayerTalentsData tick(MinecraftServer server) {
        this.playerMap.values().forEach(abilityTree -> abilityTree.tick(server));
        return this;
    }

    @SubscribeEvent
    public static void onTick(TickEvent.WorldTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
            get((ServerWorld) event.world).tick(((ServerWorld) event.world).getServer());
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
            get((ServerWorld) event.player.level).getTalents(event.player);
        }
    }

    /* ------------------------------- */

    @Override
    public void load(CompoundNBT nbt) {
        ListNBT playerList = nbt.getList("PlayerEntries", Constants.NBT.TAG_STRING);
        ListNBT talentList = nbt.getList("TalentEntries", Constants.NBT.TAG_COMPOUND);

        if (playerList.size() != talentList.size()) {
            throw new IllegalStateException("Map doesn't have the same amount of keys as values");
        }

        for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getTalents(playerUUID).deserializeNBT(talentList.getCompound(i));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        ListNBT playerList = new ListNBT();
        ListNBT talentList = new ListNBT();

        this.playerMap.forEach((uuid, abilityTree) -> {
            playerList.add(StringNBT.valueOf(uuid.toString()));
            talentList.add(abilityTree.serializeNBT());
        });

        nbt.put("PlayerEntries", playerList);
        nbt.put("TalentEntries", talentList);

        return nbt;
    }

    public static PlayerTalentsData get(ServerWorld world) {
        return world.getServer().overworld()
                .getDataStorage().computeIfAbsent(PlayerTalentsData::new, DATA_NAME);
    }

}

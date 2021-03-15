package iskallia.vault.world.data;

import iskallia.vault.Vault;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
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
public class PlayerSetsData extends WorldSavedData {

    protected static final String DATA_NAME = Vault.MOD_ID + "_PlayerSets";

    private Map<UUID, SetTree> playerMap = new HashMap<>();

    public PlayerSetsData() {
        this(DATA_NAME);
    }

    public PlayerSetsData(String name) {
        super(name);
    }

    public SetTree getSets(PlayerEntity player) {
        return this.getSets(player.getUniqueID());
    }

    public SetTree getSets(UUID uuid) {
        return this.playerMap.computeIfAbsent(uuid, SetTree::new);
    }

    /* ------------------------------- */

    public PlayerSetsData add(ServerPlayerEntity player, SetNode<?>... nodes) {
        this.getSets(player).add(player.getServer(), nodes);

        markDirty();
        return this;
    }

    public PlayerSetsData remove(ServerPlayerEntity player, SetNode<?>... nodes) {
        this.getSets(player).remove(player.getServer(), nodes);
        markDirty();
        return this;
    }

    public PlayerSetsData resetSetTree(ServerPlayerEntity player) {
        UUID uniqueID = player.getUniqueID();

        SetTree oldTalentTree = playerMap.get(uniqueID);

        if (oldTalentTree != null) {
            for (SetNode<?> node : oldTalentTree.getNodes()) {
                if (node.isActive())
                    node.getSet().onRemoved(player);
            }
        }

        SetTree setTree = new SetTree(uniqueID);
        this.playerMap.put(uniqueID, setTree);

        markDirty();
        return this;
    }

    /* ------------------------------- */

    public PlayerSetsData tick(MinecraftServer server) {
        this.playerMap.values().forEach(setTree -> setTree.tick(server));
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
            get((ServerWorld) event.player.world).getSets(event.player);
        }
    }

    /* ------------------------------- */

    @Override
    public void read(CompoundNBT nbt) {
        ListNBT playerList = nbt.getList("PlayerEntries", Constants.NBT.TAG_STRING);
        ListNBT talentList = nbt.getList("SetEntries", Constants.NBT.TAG_COMPOUND);

        if (playerList.size() != talentList.size()) {
            throw new IllegalStateException("Map doesn't have the same amount of keys as values");
        }

        for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getSets(playerUUID).deserializeNBT(talentList.getCompound(i));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        ListNBT playerList = new ListNBT();
        ListNBT talentList = new ListNBT();

        this.playerMap.forEach((uuid, abilityTree) -> {
            playerList.add(StringNBT.valueOf(uuid.toString()));
            talentList.add(abilityTree.serializeNBT());
        });

        nbt.put("PlayerEntries", playerList);
        nbt.put("SetEntries", talentList);

        return nbt;
    }

    public static PlayerSetsData get(ServerWorld world) {
        return world.getServer().func_241755_D_()
                .getSavedData().getOrCreate(PlayerSetsData::new, DATA_NAME);
    }

}

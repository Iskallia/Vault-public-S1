package iskallia.vault.world.data;

import iskallia.vault.Vault;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
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
public class PlayerAbilitiesData extends WorldSavedData {

    protected static final String DATA_NAME = Vault.MOD_ID + "_PlayerAbilities";

    private Map<UUID, AbilityTree> playerMap = new HashMap<>();

    public PlayerAbilitiesData() {
        super(DATA_NAME);
    }

    public PlayerAbilitiesData(String name) {
        super(name);
    }

    public AbilityTree getAbilities(PlayerEntity player) {
        return this.getAbilities(player.getUniqueID());
    }

    public AbilityTree getAbilities(UUID uuid) {
        return this.playerMap.computeIfAbsent(uuid, AbilityTree::new);
    }

    /* ---------------------------------------------- */

    public PlayerAbilitiesData add(ServerPlayerEntity player, AbilityNode<?>... nodes) {
        this.getAbilities(player).add(player.getServer(), nodes);

        markDirty();
        return this;
    }

    public PlayerAbilitiesData remove(ServerPlayerEntity player, AbilityNode<?>... nodes) {
        this.getAbilities(player).remove(player.getServer(), nodes);

        markDirty();
        return this;
    }

    public PlayerAbilitiesData upgradeAbility(ServerPlayerEntity player, AbilityNode<?> abilityNode) {
        AbilityTree abilityTree = this.getAbilities(player);

        abilityTree.upgradeAbility(player.getServer(), abilityNode);

        abilityTree.sync(player.server);
        markDirty();
        return this;
    }

    public PlayerAbilitiesData resetAbilityTree(ServerPlayerEntity player) {
        UUID uniqueID = player.getUniqueID();

        AbilityTree oldAbilityTree = playerMap.get(uniqueID);
        if (oldAbilityTree != null) {
            for (AbilityNode<?> node : oldAbilityTree.getNodes()) {
                if (node.isLearned())
                    node.getAbility().onRemoved(player);
            }
        }

        AbilityTree abilityTree = new AbilityTree(uniqueID);
        this.playerMap.put(uniqueID, abilityTree);

        abilityTree.sync(player.server);
        markDirty();
        return this;
    }

    /* ---------------------------------------------- */

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if(event.phase != TickEvent.Phase.START) return;
        if (event.side == LogicalSide.SERVER) {
            AbilityTree abilities = get((ServerWorld) event.player.world)
                    .getAbilities(event.player);
            abilities.tick(event);
        }
    }

    /* ---------------------------------------------- */

    @Override
    public void read(CompoundNBT nbt) {
        ListNBT playerList = nbt.getList("PlayerEntries", Constants.NBT.TAG_STRING);
        ListNBT abilitiesList = nbt.getList("AbilityEntries", Constants.NBT.TAG_COMPOUND);

        if (playerList.size() != abilitiesList.size()) {
            throw new IllegalStateException("Map doesn't have the same amount of keys as values");
        }

        for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getAbilities(playerUUID).deserializeNBT(abilitiesList.getCompound(i));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        ListNBT playerList = new ListNBT();
        ListNBT abilitiesList = new ListNBT();

        this.playerMap.forEach((uuid, researchTree) -> {
            playerList.add(StringNBT.valueOf(uuid.toString()));
            abilitiesList.add(researchTree.serializeNBT());
        });

        nbt.put("PlayerEntries", playerList);
        nbt.put("AbilityEntries", abilitiesList);

        return nbt;
    }

    public static PlayerAbilitiesData get(ServerWorld world) {
        return world.getServer().func_241755_D_()
                .getSavedData().getOrCreate(PlayerAbilitiesData::new, DATA_NAME);
    }

}

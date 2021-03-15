package iskallia.vault.world.data;

import com.mojang.datafixers.util.Either;
import iskallia.vault.Vault;
import iskallia.vault.entity.EternalData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

public class EternalsData extends WorldSavedData {

    protected static final String DATA_NAME = Vault.MOD_ID + "_Eternals";

    private Map<UUID, EternalGroup> playerMap = new HashMap<>();

    public EternalsData() {
        this(DATA_NAME);
    }

    public EternalsData(String name) {
        super(name);
    }

    public int getTotalEternals() {
        int total = 0;

        for(EternalGroup group: this.playerMap.values()) {
            total += group.getEternals().size();
        }

        return total;
    }

    public EternalGroup getEternals(PlayerEntity player) {
        return this.getEternals(player.getUniqueID());
    }

    public EternalGroup getEternals(UUID player) {
        return this.playerMap.computeIfAbsent(player, uuid -> new EternalGroup());
    }

    public EternalGroup getEternals(Either<UUID, PlayerEntity> owner) {
        if (owner.left().isPresent()) {
            return this.getEternals(owner.left().get());
        }

        return this.getEternals(owner.right().get());
    }

    /**
     * @param current Name to ignore when getting a new list of eternals for Global Trader. If current is null or empty this method will return all trader names.
     * @return A list of unique trader names for use with the Global Trader.
     */
    public List<String> getAllEternalNamesExcept(String current) {
        Set<String> names = new HashSet<>();
        for (UUID id : playerMap.keySet()) {
            EternalGroup group = playerMap.get(id);
            for (EternalData data : group.getEternals()) {
                names.add(data.getName());
            }
        }
        if (current != null && !current.isEmpty()) names.remove(current);
        return new ArrayList<>(names);
    }

    /* ---------------------------------------------- */

    public UUID add(UUID owner, String name) {
        UUID eternalId = this.getEternals(owner).addEternal(name);
        this.markDirty();
        return eternalId;
    }

    public UUID getOwnerOf(UUID eternalId) {
        return this.playerMap.entrySet().stream()
                .filter(e -> e.getValue().getEternals().stream().map(EternalData::getId).filter(id -> id.equals(eternalId))
                        .findFirst().orElse(null) != null).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    /*
    public EternalsData remove(ServerPlayerEntity player, Entity eternal) {
        this.getEternals(player).removeEternal(eternal.serializeNBT());
        this.markDirty();
        return this;
    }

    public EternalsData removeByName(ServerPlayerEntity player, String eternalName) {
        this.getEternals(player).removeEternalByName(eternalName);
        this.markDirty();
        return this;
    }*/

    /* ---------------------------------------------- */

    @Override
    public void read(CompoundNBT nbt) {
        ListNBT playerList = nbt.getList("PlayerEntries", Constants.NBT.TAG_STRING);
        ListNBT eternalsList = nbt.getList("EternalEntries", Constants.NBT.TAG_COMPOUND);

        if (playerList.size() != eternalsList.size()) {
            throw new IllegalStateException("Map doesn't have the same amount of keys as values");
        }

        for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getEternals(playerUUID).deserializeNBT(eternalsList.getCompound(i));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        ListNBT playerList = new ListNBT();
        ListNBT eternalsList = new ListNBT();

        this.playerMap.forEach((uuid, eternalGroup) -> {
            playerList.add(StringNBT.valueOf(uuid.toString()));
            eternalsList.add(eternalGroup.serializeNBT());
        });

        nbt.put("PlayerEntries", playerList);
        nbt.put("EternalEntries", eternalsList);

        return nbt;
    }

    public static EternalsData get(ServerWorld world) {
        return world.getServer().func_241755_D_().getSavedData().getOrCreate(EternalsData::new, DATA_NAME);
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    /* ---------------------------------------------- */

    public static class EternalGroup implements INBTSerializable<CompoundNBT> {
        private Map<UUID, EternalData> eternals = new HashMap<>();

        public EternalGroup() {
        }

        public List<EternalData> getEternals() {
            return new ArrayList<>(this.eternals.values());
        }

        public UUID addEternal(String name) {
            return this.addEternal(new EternalData(name)).getId();
        }

        private EternalData addEternal(EternalData eternal) {
            this.eternals.put(eternal.getId(), eternal);
            return eternal;
        }

        public EternalData getFromId(UUID eternalId) {
            return this.eternals.get(eternalId);
        }



        /*
        public boolean removeEternal(CompoundNBT e) {
            return this.eternals.remove(e);
        }

        public boolean removeEternalByName(String name) {
            for (INBT nbt : eternals) {
                CompoundNBT entityData = (CompoundNBT) nbt;
                String customName = entityData.getString("CustomName");
                if (customName.isEmpty()) continue;
                return this.removeEternal(entityData);
            }
            return false;
        }*/

        public EternalData getRandom(Random random) {
            if (this.eternals.isEmpty()) return null;
            return this.getEternals().get(random.nextInt(this.eternals.size()));
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            ListNBT eternalsList = new ListNBT();
            this.eternals.values().forEach(eternal -> eternalsList.add(eternal.serializeNBT()));
            nbt.put("EternalsList", eternalsList);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            this.eternals.clear();
            ListNBT eternalsList = nbt.getList("EternalsList", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < eternalsList.size(); i++) {
                this.addEternal(EternalData.fromNBT(eternalsList.getCompound(i)));
            }
        }
    }

}

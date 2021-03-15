package iskallia.vault.world.data;

import iskallia.vault.Vault;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.RelicPartItem;
import iskallia.vault.util.RelicSet;
import iskallia.vault.util.nbt.NBTHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VaultSetsData extends WorldSavedData {

    protected static final String DATA_NAME = Vault.MOD_ID + "_VaultSets";

    private Map<UUID, Set<String>> playerData = new HashMap<>();

    public VaultSetsData() {
        super(DATA_NAME);
    }

    public VaultSetsData(String name) {
        super(name);
    }

    public Set<String> getCraftedSets(UUID playerId) {
        return this.playerData.computeIfAbsent(playerId, uuid -> new HashSet<>());
    }

    public int getExtraTime(UUID playerId) {
        return getCraftedSets(playerId).size() * ModConfigs.VAULT_RELICS.getExtraTickPerSet();
    }

    public boolean markSetAsCrafted(UUID playerId, RelicSet relicSet) {
        Set<String> craftedSets = getCraftedSets(playerId);
        this.markDirty();
        return craftedSets.add(relicSet.getId().toString());
    }

    @SubscribeEvent
    public static void onCrafted(PlayerEvent.ItemCraftedEvent event) {
        PlayerEntity player = event.getPlayer();

        if (player.world.isRemote) return;

        IInventory craftingMatrix = event.getInventory();
        ItemStack craftedItemstack = event.getCrafting();

        if (craftedItemstack.getItem() != ModBlocks.RELIC_STATUE_BLOCK_ITEM)
            return;

        for (int i = 0; i < craftingMatrix.getSizeInventory(); i++) {
            ItemStack stackInSlot = craftingMatrix.getStackInSlot(i);
            if (stackInSlot == ItemStack.EMPTY) continue;
            Item item = stackInSlot.getItem();
            if (item instanceof RelicPartItem) {
                RelicPartItem relicPart = (RelicPartItem) item;
                VaultSetsData vaultSetsData = VaultSetsData.get((ServerWorld) player.world);
                vaultSetsData.markSetAsCrafted(player.getUniqueID(), relicPart.getRelicSet());
                break;
            }
        }
    }

    @Override
    public void read(CompoundNBT nbt) {
        this.playerData = NBTHelper.readMap(nbt, "Sets", ListNBT.class, list -> {
            return IntStream.range(0, list.size()).mapToObj(list::getString).collect(Collectors.toSet());
        });
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        NBTHelper.writeMap(compound, "Sets", this.playerData, ListNBT.class, strings -> {
            ListNBT list = new ListNBT();
            strings.forEach(s -> list.add(StringNBT.valueOf(s)));
            return list;
        });

        return compound;
    }

    public static VaultSetsData get(ServerWorld world) {
        return world.getServer().func_241755_D_()
                .getSavedData().getOrCreate(VaultSetsData::new, DATA_NAME);
    }

}

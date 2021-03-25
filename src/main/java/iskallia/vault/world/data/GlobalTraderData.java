package iskallia.vault.world.data;

import iskallia.vault.Vault;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.nbt.NBTSerializer;
import iskallia.vault.vending.Trade;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.time.Instant;
import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GlobalTraderData extends WorldSavedData {

    protected static final String DATA_NAME = Vault.MOD_ID + "_GlobalTrader";

    private static final Map<UUID, List<Trade>> playerMap = new HashMap<>();

    public GlobalTraderData() {
        this(DATA_NAME);
    }

    public GlobalTraderData(String name) {
        super(name);
    }

    public List<Trade> getPlayerTrades(PlayerEntity player) {
        return this.getPlayerTrades(player.getUUID());
    }

    public List<Trade> getPlayerTrades(UUID uuid) {
        List<Trade> trades = playerMap.computeIfAbsent(uuid, id -> getNewTrades());
        this.setDirty();
        return trades;
    }

    public ListNBT getPlayerTradesAsNbt(PlayerEntity player) {
        ListNBT playerTradesList = new ListNBT();
        for (Trade trade : this.getPlayerTrades(player)) {
            try {
                playerTradesList.add(NBTSerializer.serialize(trade));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return playerTradesList;
    }

    public void updatePlayerTrades(PlayerEntity playerEntity, List<Trade> trades) {
        playerMap.replace(playerEntity.getUUID(), trades);
        this.setDirty();
    }

    @SubscribeEvent
    public static void onTick(TickEvent.WorldTickEvent event) {
        if (event.world.getGameTime() % 20 != 0 || event.phase == TickEvent.Phase.END || event.world.dimension() != World.OVERWORLD)
            return;

        long time = Instant.now().getEpochSecond();
        if (time % (24 * 60 * 60) == 0) {
            resetTrades();
            GlobalTraderData.get((ServerWorld) event.world).setDirty();
        }
    }

    private static void resetTrades() {
        playerMap.clear();
    }

    private static List<Trade> getNewTrades() {

        List<Trade> possibleTrades = new ArrayList<>();
        Random rand = new Random();
        for (int tradeCount = 0; tradeCount < ModConfigs.GLOBAL_TRADER.TOTAL_TRADE_COUNT; tradeCount++) {

            if (tradeCount == 0) {
                Trade trade = ModConfigs.GLOBAL_TRADER.POOL.getRandom(rand).copy();
                trade.setMaxTrades(ModConfigs.GLOBAL_TRADER.MAX_TRADES);
                possibleTrades.add(trade);
                continue;
            }

            Trade potentialTrade = ModConfigs.GLOBAL_TRADER.POOL.getRandom(rand).copy();
            while (possibleTrades.size() < ModConfigs.GLOBAL_TRADER.TOTAL_TRADE_COUNT) {
                if (possibleTrades.contains(potentialTrade)) {
                    potentialTrade = ModConfigs.GLOBAL_TRADER.POOL.getRandom(rand).copy();
                } else {
                    potentialTrade.setMaxTrades(ModConfigs.GLOBAL_TRADER.MAX_TRADES);
                    possibleTrades.add(potentialTrade);
                }
            }
        }

        return possibleTrades;
    }


    @Override
    public void load(CompoundNBT nbt) {
        ListNBT playerList = nbt.getList("PlayerList", Constants.NBT.TAG_STRING);
        ListNBT playerTradesList = nbt.getList("PlayerTradesList", Constants.NBT.TAG_LIST);

        if (playerList.size() != playerTradesList.size()) {
            throw new IllegalStateException("Map doesn't have the same amount of keys as values");
        }
        try {

            for (int i = 0; i < playerList.size(); i++) {
                UUID playerUUID = UUID.fromString(playerList.getString(i));
                List<Trade> trades = new ArrayList<>();

                for (INBT tradeData : playerTradesList.getList(i)) {
                    trades.add(NBTSerializer.deserialize(Trade.class, (CompoundNBT) tradeData));
                }

                playerMap.put(playerUUID, trades);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        ListNBT playerList = new ListNBT();
        ListNBT playerTradesList = new ListNBT();

        playerMap.forEach((uuid, t) -> {
            ListNBT trades = new ListNBT();
            playerList.add(StringNBT.valueOf(uuid.toString()));
            for (Trade trade : playerMap.get(uuid)) {
                try {
                    trades.add(NBTSerializer.serialize(trade));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            playerTradesList.add(trades);
        });
        compound.put("PlayerList", playerList);
        compound.put("PlayerTradesList", playerTradesList);

        return compound;
    }


    public static GlobalTraderData get(ServerWorld world) {
        return world.getServer().overworld().getDataStorage().computeIfAbsent(GlobalTraderData::new, DATA_NAME);
    }

    public void reset() {
        playerMap.clear();
        this.setDirty();
    }
}

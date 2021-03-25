package iskallia.vault.container;

import iskallia.vault.block.GlobalTraderBlock;
import iskallia.vault.block.entity.GlobalTraderTileEntity;
import iskallia.vault.container.inventory.TraderInventory;
import iskallia.vault.container.slot.VendingSellSlot;
import iskallia.vault.init.ModContainers;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.nbt.NBTSerializer;
import iskallia.vault.vending.Trade;
import iskallia.vault.world.data.GlobalTraderData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public class GlobalTraderContainer extends Container {

    protected GlobalTraderTileEntity tileEntity;
    protected TraderInventory traderInventory;
    protected PlayerInventory playerInventory;
    protected List<Trade> playerTrades = new ArrayList<>();

    public GlobalTraderContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, ListNBT playerTrades) {
        super(ModContainers.TRADER_CONTAINER, windowId);

        BlockState blockState = world.getBlockState(pos);
        this.tileEntity = (GlobalTraderTileEntity) GlobalTraderBlock.getBlockTileEntity(world, pos, blockState);
        this.playerInventory = playerInventory;

        this.traderInventory = new TraderInventory();
        this.addSlot(new Slot(traderInventory, TraderInventory.BUY_SLOT, 210, 43) {
            @Override
            public void setChanged() {
                super.setChanged();
                traderInventory.updateRecipe();
                if (hasTraded()) {
                    lockAllTrades();
                }

            }

            @Override
            public void onQuickCraft(ItemStack oldStackIn, ItemStack newStackIn) {
                super.onQuickCraft(oldStackIn, newStackIn);
                traderInventory.updateRecipe();
                if (hasTraded()) {
                    lockAllTrades();
                }
            }
        });
        this.addSlot(new VendingSellSlot(traderInventory, TraderInventory.SELL_SLOT, 268, 43) {
            @Override
            public void setChanged() {
                super.setChanged();

                if (hasTraded()) {
                    lockAllTrades();
                }
            }
        });

        // Player Inventory
        for (int i1 = 0; i1 < 3; ++i1) {
            for (int k1 = 0; k1 < 9; ++k1) {
                this.addSlot(new Slot(playerInventory, k1 + i1 * 9 + 9,
                        167 + k1 * 18,
                        86 + i1 * 18));
            }
        }

        // Player Hotbar
        for (int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(playerInventory, j1, 167 + j1 * 18, 144));
        }

        if (playerTrades == null) return;

        playerTrades.forEach(data -> {
            try {
                CompoundNBT tradeData = (CompoundNBT) data;
                this.playerTrades.add(NBTSerializer.deserialize(Trade.class, tradeData));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    public GlobalTraderTileEntity getTileEntity() {
        return tileEntity;
    }

    public Trade getSelectedTrade() {
        return traderInventory.getSelectedTrade();
    }

    public void selectTrade(int index) {
        if (index < 0 || index >= this.playerTrades.size()) return;

        Trade trade = this.playerTrades.get(index);

        traderInventory.updateTrade(trade);
        traderInventory.updateRecipe();

        if (traderInventory.getItem(TraderInventory.BUY_SLOT) != ItemStack.EMPTY) {
            ItemStack buyStack = traderInventory.removeItemNoUpdate(TraderInventory.BUY_SLOT);
            playerInventory.add(buyStack);
        }

        if (trade.getTradesLeft() <= 0) return;

        int slot = slotForItem(trade.getBuy().getItem());
        if (slot != -1) {
            ItemStack buyStack = playerInventory.removeItemNoUpdate(slot);
            traderInventory.setItem(TraderInventory.BUY_SLOT, buyStack);
        }
        World world = this.tileEntity.getLevel();
        if (world != null && !world.isClientSide) {
            GlobalTraderData.get((ServerWorld) world).updatePlayerTrades(this.playerInventory.player, this.playerTrades);
        }

    }

    private boolean hasTraded() {
        for (Trade t : this.playerTrades) {
            if (t.getTimesTraded() >= t.getMaxTrades()) {
                return true;
            }
        }
        return false;
    }

    private void lockAllTrades() {
        for (Trade t : this.playerTrades) {
            t.setTimesTraded(t.getMaxTrades());
        }
    }

    private int slotForItem(Item item) {
        for (int i = 0; i < playerInventory.getContainerSize(); i++) {
            if (playerInventory.getItem(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);

        ItemStack buy = traderInventory.getItem(0);

        if (!buy.isEmpty()) {
            EntityHelper.giveItem(player, buy);
        }

        if (!player.level.isClientSide) {
            if (hasTraded()) lockAllTrades();
            GlobalTraderData.get((ServerWorld) player.level).updatePlayerTrades(player, this.getPlayerTrades());
        }

    }

    public List<Trade> getPlayerTrades() {
        return this.playerTrades;
    }
}

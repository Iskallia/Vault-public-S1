package iskallia.vault.container;

import iskallia.vault.block.AdvancedVendingBlock;
import iskallia.vault.block.entity.AdvancedVendingTileEntity;
import iskallia.vault.container.inventory.AdvancedVendingInventory;
import iskallia.vault.container.slot.VendingSellSlot;
import iskallia.vault.init.ModContainers;
import iskallia.vault.item.ItemTraderCore;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.vending.TraderCore;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class AdvancedVendingContainer extends Container {

    protected AdvancedVendingTileEntity tileEntity;
    protected AdvancedVendingInventory vendingInventory;
    protected PlayerInventory playerInventory;

    public AdvancedVendingContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(ModContainers.ADVANCED_VENDING_MACHINE_CONTAINER, windowId);

        BlockState blockState = world.getBlockState(pos);
        this.tileEntity = AdvancedVendingBlock.getAdvancedVendingMachineTile(world, pos, blockState);
        this.playerInventory = playerInventory;

        this.vendingInventory = new AdvancedVendingInventory();
        this.addSlot(new Slot(vendingInventory, AdvancedVendingInventory.BUY_SLOT, 210, 43) {
            @Override
            public void onSlotChanged() {
                super.onSlotChanged();
                vendingInventory.updateRecipe();
            }

            @Override
            public void onSlotChange(ItemStack oldStackIn, ItemStack newStackIn) {
                super.onSlotChange(oldStackIn, newStackIn);
                vendingInventory.updateRecipe();
            }
        });
        this.addSlot(new VendingSellSlot(vendingInventory, AdvancedVendingInventory.SELL_SLOT, 268, 43));

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
    }

    public AdvancedVendingTileEntity getTileEntity() {
        return tileEntity;
    }

    public TraderCore getSelectedTrade() {
        return vendingInventory.getSelectedCore();
    }

    public void selectTrade(int index) {
        List<TraderCore> cores = tileEntity.getCores();
        if (index < 0 || index >= cores.size()) return;

        TraderCore traderCore = cores.get(index);

        vendingInventory.updateSelectedCore(tileEntity, traderCore);
        vendingInventory.updateRecipe();
        getTileEntity().updateSkin(traderCore.getName());

        if (vendingInventory.getStackInSlot(AdvancedVendingInventory.BUY_SLOT) != ItemStack.EMPTY) {
            ItemStack buyStack = vendingInventory.removeStackFromSlot(AdvancedVendingInventory.BUY_SLOT);
            playerInventory.addItemStackToInventory(buyStack);
        }

        if (traderCore.getTrade().getTradesLeft() <= 0) return;

        int slot = slotForItem(traderCore.getTrade().getBuy().getItem());
        if (slot != -1) {
            ItemStack buyStack = playerInventory.removeStackFromSlot(slot);
            vendingInventory.setInventorySlotContents(AdvancedVendingInventory.BUY_SLOT, buyStack);
        }
    }

    private int slotForItem(Item item) {
        for (int i = 0; i < playerInventory.getSizeInventory(); i++) {
            if (playerInventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return true;
    }

    public void deselectTrades() {
        if (vendingInventory.getStackInSlot(AdvancedVendingInventory.BUY_SLOT) != ItemStack.EMPTY) {
            ItemStack buyStack = vendingInventory.removeStackFromSlot(AdvancedVendingInventory.BUY_SLOT);
            playerInventory.addItemStackToInventory(buyStack);
        }

        vendingInventory.updateSelectedCore(tileEntity, null);
    }

    public void ejectCore(int index) {
        List<TraderCore> cores = tileEntity.getCores();
        if (index < 0 || index >= cores.size()) return;

        deselectTrades();

        TraderCore ejectedCore = tileEntity.getCores().remove(index);
        ItemStack itemStack = ItemTraderCore.getStackFromCore(ejectedCore, ejectedCore.getType());
        playerInventory.player.dropItem(itemStack, false, true);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        super.onContainerClosed(player);

        ItemStack buy = vendingInventory.getStackInSlot(0);

        if (!buy.isEmpty()) {
            EntityHelper.giveItem(player, buy);
        }
    }

}

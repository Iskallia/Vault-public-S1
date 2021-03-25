package iskallia.vault.container;

import iskallia.vault.block.VendingMachineBlock;
import iskallia.vault.block.entity.VendingMachineTileEntity;
import iskallia.vault.container.inventory.VendingInventory;
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

public class VendingMachineContainer extends Container {

    protected VendingMachineTileEntity tileEntity;
    protected VendingInventory vendingInventory;
    protected PlayerInventory playerInventory;

    public VendingMachineContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(ModContainers.VENDING_MACHINE_CONTAINER, windowId);

        BlockState blockState = world.getBlockState(pos);
        this.tileEntity = (VendingMachineTileEntity) VendingMachineBlock.getBlockTileEntity(world, pos, blockState);
        this.playerInventory = playerInventory;

        this.vendingInventory = new VendingInventory();
        this.addSlot(new Slot(vendingInventory, VendingInventory.BUY_SLOT, 210, 43) {
            @Override
            public void setChanged() {
                super.setChanged();
                vendingInventory.updateRecipe();
            }

            @Override
            public void onQuickCraft(ItemStack oldStackIn, ItemStack newStackIn) {
                super.onQuickCraft(oldStackIn, newStackIn);
                vendingInventory.updateRecipe();
            }
        });
        this.addSlot(new VendingSellSlot(vendingInventory, VendingInventory.SELL_SLOT, 268, 43));

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

    public VendingMachineTileEntity getTileEntity() {
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

        if (vendingInventory.getItem(VendingInventory.BUY_SLOT) != ItemStack.EMPTY) {
            ItemStack buyStack = vendingInventory.removeItemNoUpdate(VendingInventory.BUY_SLOT);
            playerInventory.add(buyStack);
        }

        if (traderCore.getTrade().getTradesLeft() <= 0) return;

        int slot = slotForItem(traderCore.getTrade().getBuy().getItem());
        if (slot != -1) {
            ItemStack buyStack = playerInventory.removeItemNoUpdate(slot);
            vendingInventory.setItem(VendingInventory.BUY_SLOT, buyStack);
        }
    }

    public void deselectTrades() {
        if (vendingInventory.getItem(VendingInventory.BUY_SLOT) != ItemStack.EMPTY) {
            ItemStack buyStack = vendingInventory.removeItemNoUpdate(VendingInventory.BUY_SLOT);
            playerInventory.add(buyStack);
        }

        vendingInventory.updateSelectedCore(tileEntity, null);
    }

    public void ejectCore(int index) {
        List<TraderCore> cores = tileEntity.getCores();
        if (index < 0 || index >= cores.size()) return;

        deselectTrades();

        TraderCore ejectedCore = tileEntity.getCores().remove(index);
        ItemStack itemStack = ItemTraderCore.getStackFromCore(ejectedCore, ejectedCore.getType());
        playerInventory.player.drop(itemStack, false, true);
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

        ItemStack buy = vendingInventory.getItem(0);

        if (!buy.isEmpty()) {
            EntityHelper.giveItem(player, buy);
        }
    }

}

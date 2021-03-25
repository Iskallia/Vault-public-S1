package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.ItemTraderCore;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.util.nbt.NBTSerializer;
import iskallia.vault.vending.TraderCore;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VendingMachineTileEntity extends SkinnableTileEntity {

    private List<TraderCore> cores = new ArrayList<>();

    public VendingMachineTileEntity() {
        super(ModBlocks.VENDING_MACHINE_TILE_ENTITY);
    }

    public <T extends VendingMachineTileEntity> VendingMachineTileEntity(TileEntityType<T> type) {
        super(type);
        skin = new SkinProfile();
    }


    public List<TraderCore> getCores() {
        return cores;
    }

    public void addCore(TraderCore core) {
        this.cores.add(core);
        updateSkin();
        sendUpdates();
    }

    public TraderCore getLastCore() {
        if (cores == null || cores.size() == 0) return null;
        return cores.get(cores.size() - 1);
    }

    public ItemStack getTraderCoreStack() {
        TraderCore lastCore = this.getLastCore();
        if (lastCore == null) return ItemStack.EMPTY;
        ItemStack stack = ItemTraderCore.getStackFromCore(lastCore, lastCore.getType());
        cores.remove(lastCore);
        return stack;
    }

    public TraderCore getRenderCore() {
        if (cores == null || cores.size() == 0) return null;
        TraderCore renderCore = null;
        for (TraderCore core : cores) {
            if (renderCore == null || renderCore.getValue() < core.getValue()) {
                renderCore = core;
                if (renderCore.isMegahead()) break;
            }
        }
        return renderCore;
    }

    @Override
    public void updateSkin() {
        TraderCore lastCore = getLastCore();
        if (lastCore == null) return;
        skin.updateSkin(lastCore.getName());
    }


    @Override
    public CompoundNBT save(CompoundNBT compound) {
        ListNBT list = new ListNBT();
        for (TraderCore core : cores) {
            try {
                list.add(NBTSerializer.serialize(core));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        compound.put("coresList", list);
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        ListNBT list = nbt.getList("coresList", Constants.NBT.TAG_COMPOUND);
        this.cores = new LinkedList<>();
        for (INBT tag : list) {
            TraderCore core = null;
            try {
                core = NBTSerializer.deserialize(TraderCore.class, (CompoundNBT) tag);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cores.add(core);
        }
        updateSkin();
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();

        ListNBT list = new ListNBT();
        for (TraderCore core : cores) {
            try {
                list.add(NBTSerializer.serialize(core));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        nbt.put("coresList", list);

        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        load(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        handleUpdateTag(getBlockState(), nbt);
    }
}

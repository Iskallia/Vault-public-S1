package iskallia.vault.block.entity;

import iskallia.vault.block.LootStatueBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.util.StatueType;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class LootStatueTileEntity extends TileEntity implements ITickableTileEntity {

    private int interval = 0;
    private int currentTick = 0;
    private ItemStack lootItem;
    protected SkinProfile skin;
    private StatueType statueType;
    private boolean hasCrown;

    private int chipCount = 0;

    public LootStatueTileEntity() {
        super(ModBlocks.LOOT_STATUE_TILE_ENTITY);
        skin = new SkinProfile();
    }

    public int getInterval() {
        return this.interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getCurrentTick() {
        return this.currentTick;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    public ItemStack getLootItem() {
        return this.lootItem;
    }

    public void setLootItem(ItemStack stack) {
        this.lootItem = stack;
    }

    public SkinProfile getSkin() {
        return skin;
    }

    public StatueType getStatueType() {
        return statueType;
    }

    public void setStatueType(StatueType statueType) {
        this.statueType = statueType;
    }

    public boolean hasCrown() {
        return hasCrown;
    }

    public void setHasCrown(boolean hasCrown) {
        this.hasCrown = hasCrown;
    }

    public boolean addChip() {
        if(chipCount >= ModConfigs.STATUE_LOOT.getMaxAccelerationChips()) {
            return false;
        }
        chipCount++;
        sendUpdates();
        return true;
    }

    public ItemStack removeChip() {
        ItemStack stack = ItemStack.EMPTY;
        if (chipCount > 0) {
            chipCount--;
            stack = new ItemStack(ModItems.ACCELERATION_CHIP, 1);
            sendUpdates();
        }
        return stack;
    }

    public int getChipCount() {
        return chipCount;
    }


    @Override
    public void tick() {
        if (world.isRemote) return;
        if (currentTick++ >= getModifiedInterval()) {
            currentTick = 0;

            ItemStack stack = lootItem.copy();
            if (poopItem(stack, true) != ItemStack.EMPTY) {
                stack = poopItem(stack, false);
                if (lootItem.getCount() + stack.getCount() > lootItem.getMaxStackSize()) {
                    lootItem.setCount(lootItem.getMaxStackSize());
                } else {
                    lootItem.setCount(stack.getCount());
                }
            } else {
                poopItem(stack, false);
            }
        }
    }

    private int getModifiedInterval() {
        if (getChipCount() == 0) return interval;
        return interval - ModConfigs.STATUE_LOOT.getIntervalDecrease(this.getChipCount());
    }

    public ItemStack poopItem(ItemStack stack, boolean simulate) {
        TileEntity tileEntity = world.getTileEntity(getPos().down());
        if (tileEntity == null) return stack;

        LazyOptional<IItemHandler> handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
        if (handler.isPresent()) {
            IItemHandler targetHandler = handler.orElse(null);
            return ItemHandlerHelper.insertItemStacked(targetHandler, stack, simulate);
        }
        return stack;
    }


    public void sendUpdates() {
        this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
        this.world.notifyNeighborsOfStateChange(pos, this.getBlockState().getBlock());
        markDirty();
    }


    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        String nickname = skin.getLatestNickname();
        nbt.putString("PlayerNickname", nickname == null ? "" : nickname);
        if (getInterval() == 0) migrate(this.getBlockState());

        nbt.putInt("Interval", getInterval());
        nbt.putInt("CurrentTick", getCurrentTick());
        nbt.putInt("StatueType", getStatueType().ordinal());
        nbt.put("LootItem", getLootItem().serializeNBT());
        nbt.putBoolean("HasCrown", hasCrown());
        nbt.putInt("ChipCount", chipCount);

        return super.write(nbt);
    }


    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        String nickname = nbt.getString("PlayerNickname");
        skin.updateSkin(nickname);
        if (!nbt.contains("Interval")) migrate(state);

        setLootItem(ItemStack.read(nbt.getCompound("LootItem")));
        setCurrentTick(nbt.getInt("CurrentTick"));
        setInterval(nbt.getInt("Interval"));
        setStatueType(StatueType.values()[nbt.getInt("StatueType")]);
        hasCrown = nbt.getBoolean("HasCrown");
        chipCount = nbt.getInt("ChipCount");

        super.read(state, nbt);
    }

    private void migrate(BlockState state) {
        LootStatueBlock block = (LootStatueBlock) state.getBlock();
        StatueType type = block.getType();
        setStatueType(type);
        setInterval(ModConfigs.STATUE_LOOT.getInterval(type));
        setLootItem(ModConfigs.STATUE_LOOT.randomLoot(type));
        setCurrentTick(0);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();

        String nickname = skin.getLatestNickname();
        nbt.putString("PlayerNickname", nickname == null ? "" : nickname);
        nbt.putInt("Interval", getInterval());
        nbt.putInt("CurrentTick", getCurrentTick());
        nbt.putInt("StatueType", getStatueType().ordinal());
        nbt.put("LootItem", getLootItem().serializeNBT());
        nbt.putBoolean("HasCrown", hasCrown());
        nbt.putInt("ChipCount", chipCount);

        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        read(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getNbtCompound();
        handleUpdateTag(getBlockState(), nbt);
    }
}

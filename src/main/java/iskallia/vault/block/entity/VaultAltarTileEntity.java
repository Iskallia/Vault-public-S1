package iskallia.vault.block.entity;

import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.altar.RequiredItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemVaultCrystal;
import iskallia.vault.util.VectorHelper;
import iskallia.vault.world.data.PlayerVaultAltarData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class VaultAltarTileEntity extends TileEntity implements ITickableTileEntity {

    private UUID owner;
    private AltarInfusionRecipe recipe;

    private boolean containsVaultRock = false;
    private int infusionTimer = -1;
    private boolean infusing;

    private ItemStackHandler itemHandler = createHandler();
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public VaultAltarTileEntity() {
        super(ModBlocks.VAULT_ALTAR_TILE_ENTITY);
    }

    public void setContainsVaultRock(boolean containsVaultRock) {
        this.containsVaultRock = containsVaultRock;
    }

    public boolean containsVaultRock() {
        return containsVaultRock;
    }

    public int getInfusionTimer() {
        return infusionTimer;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setRecipe(AltarInfusionRecipe recipe) {
        this.recipe = recipe;
    }

    public AltarInfusionRecipe getRecipe() {
        return recipe;
    }

    public boolean isInfusing() {
        return infusing;
    }

    public void setInfusing(boolean infusing) {
        this.infusing = infusing;
    }

    public void sendUpdates() {
        if (this.world == null) return;
        this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
        this.world.notifyNeighborsOfStateChange(pos, this.getBlockState().getBlock());
        markDirty();
    }

    @Override
    public void tick() {
        World world = this.getWorld();
        if (world == null || world.isRemote || !containsVaultRock) return;


        double x = this.getPos().getX() + 0.5d;
        double y = this.getPos().getY() + 0.5d;
        double z = this.getPos().getZ() + 0.5d;

        PlayerVaultAltarData data = PlayerVaultAltarData.get((ServerWorld) world);


        pullNearbyItems(world, data, x, y, z, ModConfigs.VAULT_ALTAR.ITEM_RANGE_CHECK);

        if (infusing) infusionTimer--;

        if (infusionTimer == 0) {
            completeInfusion(world);
            sendUpdates();
        }

        // update recipe for client to receive
        this.recipe = data.getRecipe(this.owner);

        if (this.containsVaultRock && this.recipe == null && !infusing) {
            this.containsVaultRock = false;
            world.addEntity(new ItemEntity(world, getPos().getX() + .5d, pos.getY() + 1.5d, pos.getZ() + .5d, new ItemStack(ModItems.VAULT_ROCK)));
        }

        if (world.getGameTime() % 20 == 0) sendUpdates();

    }

    private void completeInfusion(World world) {
        this.containsVaultRock = false;
        this.recipe = null;
        this.infusionTimer = -1;
        this.infusing = false;
        ItemStack crystal = ItemVaultCrystal.getRandomCrystal();

        world.addEntity(new ItemEntity(world, getPos().getX() + .5d, pos.getY() + 1.5d, pos.getZ() + .5d, crystal));
    }

    public void startInfusionTimer(int seconds) {
        infusionTimer = seconds * 20;
    }

    private void pullNearbyItems(World world, PlayerVaultAltarData data, double x, double y, double z, double range) {
        if (data.getRecipe(this.owner) == null || data.getRecipe(this.owner).getRequiredItems().isEmpty()) return;

        float speed = ModConfigs.VAULT_ALTAR.PULL_SPEED / 20f; // blocks per second

        List<ItemEntity> entities = world.getEntitiesWithinAABB(ItemEntity.class, getAABB(range, x, y, z));
        for (ItemEntity itemEntity : entities) {
            List<RequiredItem> itemsToPull = data.getRecipe(this.owner).getRequiredItems();
            if (itemsToPull == null) return;

            for (RequiredItem required : itemsToPull) {

                if (required.reachedAmountRequired()) continue;

                if (required.isItemEqual(itemEntity.getItem())) {
                    int excess = required.getRemainder(itemEntity.getItem().getCount());
                    moveItemTowardPedestal(itemEntity, speed);
                    if (isItemInRange(itemEntity.getPosition())) {
                        if (excess > 0) {
                            required.setCurrentAmount(required.getAmountRequired());
                            itemEntity.getItem().setCount(excess);
                        } else {
                            required.addAmount(itemEntity.getItem().getCount());
                            itemEntity.getItem().setCount(excess);
                            itemEntity.remove();
                        }
                        data.markDirty();
                        sendUpdates();
                    }
                }
            }
        }
    }

    private void moveItemTowardPedestal(ItemEntity itemEntity, float speed) {
        Vector3d target = VectorHelper.getVectorFromPos(this.getPos());
        Vector3d current = VectorHelper.getVectorFromPos(itemEntity.getPosition());

        Vector3d velocity = VectorHelper.getMovementVelocity(current, target, speed);

        itemEntity.addVelocity(velocity.x, velocity.y, velocity.z);
    }

    private boolean isItemInRange(BlockPos itemPos) {
        return itemPos.distanceSq(getPos()) <= (2 * 2);
    }

    public AxisAlignedBB getAABB(double range, double x, double y, double z) {
        return new AxisAlignedBB(x - range, y - range, z - range, x + range, y + range, z + range);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putBoolean("containsVaultRock", containsVaultRock);
        if (owner != null) compound.putUniqueId("Owner", this.owner);
        if (this.recipe != null) compound.put("Recipe", AltarInfusionRecipe.serialize(this.recipe));
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        containsVaultRock = compound.getBoolean("containsVaultRock");
        if (compound.contains("Owner")) this.owner = compound.getUniqueId("Owner");
        if (compound.contains("Recipe")) this.recipe = AltarInfusionRecipe.deserialize(compound.getCompound("Recipe"));
        super.read(state, compound);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        tag.putBoolean("containsVaultRock", containsVaultRock);
        if (owner != null) tag.putUniqueId("Owner", this.owner);
        if (this.recipe != null) tag.put("Recipe", AltarInfusionRecipe.serialize(this.recipe));
        return tag;
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
        CompoundNBT tag = pkt.getNbtCompound();
        handleUpdateTag(getBlockState(), tag);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {

            @Override
            protected void onContentsChanged(int slot) {
                sendUpdates();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (recipe != null && !recipe.getRequiredItems().isEmpty()) {
                    List<RequiredItem> items = recipe.getRequiredItems();
                    for (RequiredItem item : items) {
                        if (item.isItemEqual(stack)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (recipe != null && !recipe.getRequiredItems().isEmpty()) {
                    List<RequiredItem> items = recipe.getRequiredItems();
                    for (RequiredItem item : items) {
                        if (item.isItemEqual(stack)) {
                            if (item.reachedAmountRequired()) {
                                return stack;
                            }
                            int amount = stack.getCount();
                            int excess = item.getRemainder(amount);
                            if (excess > 0) {
                                // VanillaInventoryCodeHooks#insertItem operates in 2 modes: simulate and actual insert.
                                // It triggers it 2 times, first time to check if it works, and second time to actually
                                // insert items. It must be required to check if simulate is false, otherwise,
                                // all items will be inserted twice.
                                if (!simulate) {
                                    item.setCurrentAmount(item.getAmountRequired());
                                }
                                stack.setCount(excess);
                                return ItemHandlerHelper.copyStackWithSize(stack, excess);
                            } else {
                                // VanillaInventoryCodeHooks#insertItem operates in 2 modes: simulate and actual insert.
                                // It triggers it 2 times, first time to check if it works, and second time to actually
                                // insert items. It must be required to check if simulate is false, otherwise,
                                // all items will be inserted twice.
                                if (!simulate) {
                                    item.addAmount(stack.getCount());
                                }
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                }
                return stack;
            }
        };
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }
}

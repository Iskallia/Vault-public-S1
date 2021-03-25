package iskallia.vault.block.entity;

import iskallia.vault.entity.EternalData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemTraderCore;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.vending.TraderCore;
import iskallia.vault.world.data.EternalsData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class CryoChamberTileEntity extends TileEntity implements ITickableTileEntity {

    //    public static final Supplier<Generator> GENERATOR = Behaviour.register(Vault.id("generator"), Generator::new);
//    public static final Supplier<Miner> MINER = Behaviour.register(Vault.id("miner"), Miner::new);
//    public static final Supplier<Looter> LOOTER = Behaviour.register(Vault.id("looter"), Looter::new);
//
//    private List<Behaviour> behaviours = new ArrayList<>();
    protected SkinProfile skin;

    private UUID owner;
    private List<String> coreNames = new ArrayList<>();
    private int maxCores = 0;

    private boolean infusing = false;
    private int infusionTimeRemaining = 0;
    private boolean growingEternal = false;
    private int growEternalTimeRemaining = 0;

    private UUID eternalId;
    private EternalData eternalData; //This should be null server-side at all times.
    public float lastCoreCount;

    private ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            if(this.getStackInSlot(slot).getItem() == ModItems.TRADER_CORE) {
                CryoChamberTileEntity.this.addTraderCore(ItemTraderCore.getCoreFromStack(this.getStackInSlot(slot)));
                this.setStackInSlot(slot, ItemStack.EMPTY);
            }

            CryoChamberTileEntity.this.sendUpdates();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() == ModItems.TRADER_CORE && !CryoChamberTileEntity.this.isFull() && !CryoChamberTileEntity.this.isInfusing();
        }
    };

    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.itemHandler);

    public CryoChamberTileEntity() {
        super(ModBlocks.CRYO_CHAMBER_TILE_ENTITY);
        this.skin = new SkinProfile();
    }

    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public int getMaxCores() {
        return this.maxCores;
    }

    public void setMaxCores(int maxCores) {
        this.maxCores = maxCores;
    }

    public boolean isInfusing() {
        return this.infusing;
    }

    public int getInfusionTimeRemaining() {
        return this.infusionTimeRemaining;
    }

    public boolean isGrowingEternal() {
        return this.growingEternal;
    }

    public int getGrowEternalTimeRemaining() {
        return this.growEternalTimeRemaining;
    }

    public SkinProfile getSkin() {
        return this.skin;
    }

    public int getCoreCount() {
        return this.coreNames.size();
    }

    public List<String> getCoreNames() {
        return this.coreNames;
    }

    public String getEternalName() {
        EternalData eternal = this.getEternal();
        return eternal == null ? "" : eternal.getName();
    }

    public boolean addTraderCore(TraderCore core) {
        if (this.isFull() || this.isInfusing()) return false;
        this.level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), SoundEvents.SOUL_ESCAPE, SoundCategory.PLAYERS, 1f, 1f);
        this.coreNames.add(core.getName());
        this.infusing = true;
        this.infusionTimeRemaining = ModConfigs.CRYO_CHAMBER.getInfusionTime();
        setChanged();
        return true;
    }

    public void updateSkin() {
        if (infusing && !coreNames.isEmpty()) {
            skin.updateSkin(coreNames.get(coreNames.size() - 1));
            return;
        }
        String core = this.getEternalName();
        if (core == null) return;
        skin.updateSkin(core);
    }

    public void sendUpdates() {
        this.level.sendBlockUpdated(worldPosition, this.getBlockState(), this.getBlockState(), 3);
        this.level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
        setChanged();
    }

    public EternalData getEternal() {
        if (this.getLevel() == null) return null;
        if (!this.getLevel().isClientSide()) {
            if (this.eternalId == null) return null;
            return EternalsData.get((ServerWorld) this.getLevel()).getEternals(this.owner).getFromId(this.eternalId);
        }

        return this.eternalData;
    }

    public void onItemClicked(ItemStack heldStack, PlayerEntity player) {
        EternalData data = this.getEternal();

        if (data == null) return;

        if (!heldStack.isEmpty()) {
            EquipmentSlotType slot = MobEntity.getEquipmentSlotForItem(heldStack);
            ItemStack oldStack = data.getStack(slot);

            if (!oldStack.isEmpty()) {
                ItemStack copy = oldStack.copy();

                if (!player.addItem(copy)) {
                    player.drop(copy, false, false);
                }
            }

            data.setStack(slot, heldStack.copy());
            heldStack.setCount(0);
            this.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 1f, 1f);
            ((ServerWorld) this.level).sendParticles(ParticleTypes.DRAGON_BREATH, player.getX(), player.getY(), player.getZ(), 100, 1, 1, 1, 0.2);
        } else {
            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                ItemStack invStack = data.getStack(slot);
                if (invStack.isEmpty()) continue;
                ItemStack copy = invStack.copy();

                if (!player.addItem(copy)) {
                    player.drop(copy, false, false);
                }

                data.setStack(slot, ItemStack.EMPTY);
                this.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 1f, 1f);
                ((ServerWorld) this.level).sendParticles(ParticleTypes.DRAGON_BREATH, player.getX(), player.getY(), player.getZ(), 100, 1, 1, 1, 0.2);
                break;
            }
        }
    }

    private boolean isFull() {
        return this.coreNames.size() >= this.maxCores;
    }

//    public List<Behaviour> getBehaviours() {
//        return behaviours;
//    }
//
//    public void addBehaviour(Behaviour behaviour) {
//        behaviours.add(behaviour);
//        markDirty();
//    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide || this.owner == null) return;

        if (this.isFull() && !this.growingEternal && this.getEternal() == null) {
            this.growingEternal = true;
            this.growEternalTimeRemaining = ModConfigs.CRYO_CHAMBER.getGrowEternalTime();
        }

        if(this.isFull() && !this.growingEternal && this.level.getGameTime() % 40 == 0) {
            this.level.playSound(null, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), SoundEvents.CONDUIT_AMBIENT, SoundCategory.PLAYERS, 1.0f, 1f);
        }

        if (this.infusing) {
            if (this.infusionTimeRemaining-- <= 0) {
                this.infusionTimeRemaining = 0;
                this.infusing = false;
            }
            this.sendUpdates();
            //TODO send client time remaining.
        } else if (this.growingEternal) {
            if (this.growEternalTimeRemaining-- <= 0) {
                this.growEternalTimeRemaining = 0;
                this.growingEternal = false;
                this.createEternal();
            }

            this.sendUpdates();
            //TODO send client time remaining.
        }
        // this.behaviours.forEach(behaviour -> behaviour.tick(this.getWorld(), this.getPos(), this));
    }

    private void createEternal() {
        System.out.println("createEternal()");
        String name = this.coreNames.get(this.getLevel().getRandom().nextInt(this.coreNames.size()));
        this.eternalId = EternalsData.get((ServerWorld) this.getLevel()).add(this.owner, name);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        try {
            if (this.owner == null) return new CompoundNBT();
            nbt.putUUID("Owner", this.owner);
            if (this.eternalId != null) nbt.putUUID("EternalId", this.eternalId);

            if (!coreNames.isEmpty()) {
                ListNBT list = new ListNBT();
                for (int i = 0; i < coreNames.size(); i++) {
                    CompoundNBT nameNbt = new CompoundNBT();
                    String name = coreNames.get(i);
                    nameNbt.putString("name" + i, name);
                    list.add(nameNbt);
                }
                nbt.put("CoresList", list);
            }
            nbt.putInt("MaxCoreCount", this.maxCores);
            nbt.putBoolean("Infusing", this.infusing);
            nbt.putInt("InfusionTimeRemaining", this.infusionTimeRemaining);
            nbt.putBoolean("GrowingEternal", this.growingEternal);
            nbt.putInt("GrowEternalTimeRemaining", this.growEternalTimeRemaining);
            nbt.put("Inventory", this.itemHandler.serializeNBT());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        ListNBT behavioursList = new ListNBT();
//
//        this.behaviours.forEach(behaviour -> {
//            CompoundNBT tag = behaviour.serializeNBT();
//            tag.putString("RegistryId", behaviour.id.toString());
//            behavioursList.add(tag);
//        });
//
//        nbt.put("Behaviours", behavioursList);
        return super.save(nbt);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        try {
            if (nbt.contains("Owner")) this.owner = nbt.getUUID("Owner");
            if (nbt.contains("EternalId")) this.eternalId = nbt.getUUID("EternalId");

            if (nbt.contains("CoresList")) {
                ListNBT list = nbt.getList("CoresList", Constants.NBT.TAG_COMPOUND);
                this.coreNames = new LinkedList<>();
                for (int i = 0; i < list.size(); i++) {
                    CompoundNBT nameTag = list.getCompound(i);
                    coreNames.add(nameTag.getString("name" + i));
                }
            }
            this.maxCores = nbt.getInt("MaxCoreCount");
            this.infusing = nbt.getBoolean("Infusing");
            this.infusionTimeRemaining = nbt.getInt("InfusionTimeRemaining");
            this.growingEternal = nbt.getBoolean("GrowingEternal");
            this.growEternalTimeRemaining = nbt.getInt("GrowEternalTimeRemaining");
            this.itemHandler.deserializeNBT(nbt.getCompound("Inventory"));

//        this.behaviours.clear();
//        ListNBT behavioursList = nbt.getList("Behaviours", Constants.NBT.TAG_COMPOUND);
//
//        for (int i = 0; i < behavioursList.size(); i++) {
//            CompoundNBT tag = behavioursList.getCompound(i);
//            Supplier<? extends Behaviour> supplier = Behaviour.REGISTRY.get(new ResourceLocation(tag.getString("RegistryId")));
//
//            if (supplier != null) {
//                Behaviour behaviour = supplier.get();
//                behaviour.deserializeNBT(tag);
//                this.behaviours.add(behaviour);
//            }
//        }
            //if (!coreNames.isEmpty())
            //    updateSkin();

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.load(state, nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.handler.cast() : super.getCapability(cap, side);
    }

    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        try {
            if (this.owner == null) return nbt;
            nbt.putUUID("Owner", this.owner);
            nbt.putInt("MaxCoreCount", this.maxCores);
            nbt.putBoolean("Infusing", this.infusing);
            nbt.putInt("InfusionTimeRemaining", this.infusionTimeRemaining);
            nbt.putBoolean("GrowingEternal", this.growingEternal);
            nbt.putInt("GrowEternalTimeRemaining", this.growEternalTimeRemaining);
            if (!coreNames.isEmpty()) {
                ListNBT list = new ListNBT();
                for (int i = 0; i < coreNames.size(); i++) {
                    CompoundNBT nameTag = new CompoundNBT();
                    nameTag.putString("name" + i, coreNames.get(i));
                    list.add(nameTag);
                }
                nbt.put("CoresList", list);
            }
            EternalData eternal = this.getEternal();
            if (eternal != null) nbt.put("EternalData", eternal.serializeNBT());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        load(state, tag);

        if (tag.contains("EternalData", Constants.NBT.TAG_COMPOUND)) {
            this.eternalData = EternalData.fromNBT(tag.getCompound("EternalData"));
        }
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        handleUpdateTag(getBlockState(), nbt);
    }

    public CompoundNBT getRenameNBT() {
        CompoundNBT nbt = new CompoundNBT();
        if (this.getEternal() == null) return nbt;
        nbt.put("BlockPos", NBTUtil.writeBlockPos(this.getBlockPos()));
        nbt.putString("EternalName", this.getEternal().getName());
        return nbt;
    }

    public void renameEternal(String name) {
        if (this.getEternal() == null) return;
        this.getEternal().setName(name);
    }
//
//    public static class Energy extends EnergyStorage implements INBTSerializable<CompoundNBT> {
//        public Energy(int capacity, int maxTransfer) {
//            super(capacity, maxTransfer);
//        }
//
//        public int getTransferSpeed() {
//            return this.maxExtract;
//        }
//
//        public void setTransferSpeed(int transferSpeed) {
//            this.maxExtract = transferSpeed;
//            this.maxReceive = transferSpeed;
//        }
//
//        public void setEnergy(int energy) {
//            this.energy = energy;
//            this.onEnergyChanged();
//        }
//
//        protected void onEnergyChanged() {
//        }
//
//        public void addEnergy(int energy) {
//            this.energy += energy;
//
//            if (this.energy > getMaxEnergyStored()) {
//                this.energy = getEnergyStored();
//            }
//
//            this.onEnergyChanged();
//        }
//
//        public void consumeEnergy(int energy) {
//            this.energy -= energy;
//            if (this.energy < 0) {
//                this.energy = 0;
//            }
//
//            this.onEnergyChanged();
//        }
//
//        @Override
//        public CompoundNBT serializeNBT() {
//            CompoundNBT nbt = new CompoundNBT();
//            nbt.putInt("energy", this.energy);
//            nbt.putInt("transferSpeed", getTransferSpeed());
//            return nbt;
//        }
//
//        @Override
//        public void deserializeNBT(CompoundNBT nbt) {
//            setEnergy(nbt.getInt("energy"));
//            setTransferSpeed(nbt.getInt("transferSpeed"));
//        }
//    }
//
//    public static abstract class Behaviour implements INBTSerializable<CompoundNBT> {
//        public static final BiMap<ResourceLocation, Supplier<? extends Behaviour>> REGISTRY = HashBiMap.create();
//
//        public ResourceLocation id;
//
//        public Behaviour() {
//        }
//
//        public abstract void tick(World world, BlockPos pos, CryoChamberTileEntity te);
//
//        public static <T extends Behaviour> Supplier<T> register(ResourceLocation id, Supplier<T> behaviour) {
//            REGISTRY.put(id, () -> {
//                T value = behaviour.get();
//                value.id = id;
//                return value;
//            });
//
//            return behaviour;
//        }
//    }
//
//    public static class Generator extends Behaviour {
//        private Energy energyStorage = createEnergyStorage();
//        private LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);
//
//        public Generator() {
//
//        }
//
//        private Energy createEnergyStorage() {
//            int transferSpeed = MathUtilities.getRandomInt(ModConfigs.CRYO_CHAMBER.GENERATOR_FE_PER_TICK_MIN, ModConfigs.CRYO_CHAMBER.GENERATOR_FE_PER_TICK_MAX);
//            return new Energy(ModConfigs.CRYO_CHAMBER.GENERATOR_FE_CAPACITY, transferSpeed);
//        }
//
//        @Override
//        public void tick(World world, BlockPos pos, CryoChamberTileEntity te) {
//            if (world.isRemote) return;
//            energyStorage.addEnergy(energyStorage.getTransferSpeed());
//            for (DoubleBlockHalf half : DoubleBlockHalf.values()) {
//                if (half == DoubleBlockHalf.LOWER) pos = pos.offset(Direction.UP);
//                sendOutPower(world, pos);
//            }
//            te.markDirty();
//        }
//
//        private void sendOutPower(World world, BlockPos pos) {
//            AtomicInteger capacity = new AtomicInteger(energyStorage.getEnergyStored());
//            if (capacity.get() > 0) {
//                for (Direction direction : Direction.values()) {
//                    TileEntity te = world.getTileEntity(pos.offset(direction));
//                    if (te != null) {
//                        if (te instanceof CryoChamberTileEntity) continue; // skip self
//                        boolean doContinue = te.getCapability(CapabilityEnergy.ENERGY, direction).map(handler -> {
//                                    if (handler.canReceive()) {
//                                        int received = handler.receiveEnergy(Math.min(capacity.get(), energyStorage.getTransferSpeed()), false);
//                                        capacity.addAndGet(-received);
//                                        energyStorage.consumeEnergy(received);
//                                        return capacity.get() > 0;
//                                    } else {
//                                        return true;
//                                    }
//                                }
//                        ).orElse(true);
//                        if (!doContinue) {
//                            return;
//                        }
//                    }
//                }
//            }
//        }
//
//        @Override
//        public CompoundNBT serializeNBT() {
//            CompoundNBT nbt = new CompoundNBT();
//            nbt.put("Generator", energyStorage.serializeNBT());
//            return nbt;
//        }
//
//        @Override
//        public void deserializeNBT(CompoundNBT nbt) {
//            energyStorage.deserializeNBT(nbt.getCompound("Generator"));
//        }
//
//        @Override
//        public String toString() {
//            return "Generator{" +
//                    "energy=" + energyStorage.getEnergyStored() +
//                    ", speed=" + energyStorage.getTransferSpeed() +
//                    '}';
//        }
//    }
//
//    public static class Miner extends Poop {
//        public Miner() {
//            super(ModConfigs.CRYO_CHAMBER.MINER_DROPS, ModConfigs.CRYO_CHAMBER.MINER_TICKS_DELAY, Vault.id("miner"));
//        }
//    }
//
//    public static class Looter extends Poop {
//        public Looter() {
//            super(ModConfigs.CRYO_CHAMBER.LOOTER_DROPS, ModConfigs.CRYO_CHAMBER.LOOTER_TICKS_DELAY, Vault.id("looter"));
//        }
//    }
//
//    public static class Poop extends Behaviour {
//        private Product product;
//        private int delay;
//
//        public Poop(WeightedList<Product> pool, int delay, ResourceLocation resourceLocation) {
//            this.product = pool.getRandom(new Random());
//            this.delay = delay;
//        }
//
//        @Override
//        public void tick(World world, BlockPos pos, CryoChamberTileEntity te) {
//            if (world.getGameTime() % this.delay == 0) {
//                this.poop(te, this.product.toStack(), false);
//            }
//        }
//
//        public ItemStack poop(CryoChamberTileEntity te, ItemStack stack, boolean simulate) {
//            TileEntity tileEntity = te.getWorld().getTileEntity(te.getPos().down());
//            if (tileEntity == null) return stack;
//
//            LazyOptional<IItemHandler> handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
//
//            if (handler.isPresent()) {
//                IItemHandler targetHandler = handler.orElse(null);
//                return ItemHandlerHelper.insertItemStacked(targetHandler, stack, simulate);
//            }
//
//            return stack;
//        }
//
//
//        @Override
//        public CompoundNBT serializeNBT() {
//            CompoundNBT nbt = new CompoundNBT();
//
//            CompoundNBT productNBT = new CompoundNBT();
//            productNBT.putString("Id", this.product.getId());
//            productNBT.putInt("Amount", this.product.getAmount());
//            productNBT.putString("Nbt", this.product.getNBT().toString());
//            nbt.put("Product", productNBT);
//
//            return nbt;
//        }
//
//        @Override
//        public void deserializeNBT(CompoundNBT nbt) {
//            CompoundNBT productNBT = nbt.getCompound("Product");
//            String id = productNBT.getString("Id");
//            int amount = productNBT.getInt("Amount");
//            String itemNbt = productNBT.getString("Nbt");
//
//            try {
//                this.product = new Product(
//                        Registry.ITEM.getOptional(new ResourceLocation(id)).orElse(Items.AIR),
//                        amount, JsonToNBT.getTagFromJson(itemNbt)
//                );
//            } catch (CommandSyntaxException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}

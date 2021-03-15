package iskallia.vault.entity;

import iskallia.vault.init.ModEntities;
import iskallia.vault.world.data.EternalsData;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class EternalData implements INBTSerializable<CompoundNBT> {

    private UUID uuid = UUID.randomUUID();

    private String name;
    private ItemStack[] mainSlots = new ItemStack[EquipmentSlotType.values().length];

    protected EternalData() {
    }

    public EternalData(String name) {
        this.name = name;
    }

    public UUID getId() {
        return this.uuid;
    }

    public void setName(String name) {this.name = name; }

    public String getName() {
        return this.name;
    }

    public ItemStack getStack(EquipmentSlotType slot) {
        return this.mainSlots[slot.ordinal()] == null ? ItemStack.EMPTY : this.mainSlots[slot.ordinal()];
    }

    public void setStack(EquipmentSlotType slot, ItemStack stack) {
        this.mainSlots[slot.ordinal()] = stack;
    }

    public EternalEntity create(World world) {
        EternalEntity eternal = ModEntities.ETERNAL.create(world);

        EternalsData data = EternalsData.get((ServerWorld)world);
        int level = data.getEternals(data.getOwnerOf(this.getId())).getEternals().size();

        eternal.setCustomName(new StringTextComponent("[")
                .append(new StringTextComponent(String.valueOf(level)).mergeStyle(TextFormatting.GREEN))
                .append(new StringTextComponent("] " + this.getName())));

        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            eternal.setItemStackToSlot(slot, this.getStack(slot).copy());
        }

        return eternal;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        if(this.getId() != null) nbt.putUniqueId("Id", this.getId());
        if(this.getName() != null) nbt.putString("Name", this.getName());

        if(mainSlots != null) {
            ListNBT mainSlotsList = new ListNBT();

            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                mainSlotsList.add(this.getStack(slot).serializeNBT());
            }

            nbt.put("MainSlots", mainSlotsList);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("Id")) this.uuid = nbt.getUniqueId("Id");
        this.name = nbt.getString("Name");
        if (nbt.contains("MainSlots")) {
            ListNBT mainSlotsList = nbt.getList("MainSlots", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < Math.min(mainSlotsList.size(), EquipmentSlotType.values().length); i++) {
                this.setStack(EquipmentSlotType.values()[i], ItemStack.read(mainSlotsList.getCompound(i)));
            }
        }
    }

    public static EternalData fromNBT(CompoundNBT nbt) {
        EternalData eternal = new EternalData();
        eternal.deserializeNBT(nbt);
        return eternal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EternalData)) return false;
        EternalData other = (EternalData) o;
        return this.uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

}

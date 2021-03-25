package iskallia.vault.container;

import iskallia.vault.init.ModContainers;
import iskallia.vault.util.RenameType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;


public class RenamingContainer extends Container {

    private RenameType type;
    private CompoundNBT nbt;

    public RenamingContainer(int windowId, CompoundNBT nbt) {
        super(ModContainers.RENAMING_CONTAINER, windowId);
        this.type = RenameType.values()[nbt.getInt("RenameType")];
        this.nbt = nbt.getCompound("Data");
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return true;
    }

    public CompoundNBT getNbt() {
        return nbt;
    }

    public RenameType getRenameType() {
        return type;
    }
}

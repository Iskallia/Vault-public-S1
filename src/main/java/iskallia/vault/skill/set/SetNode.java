package iskallia.vault.skill.set;

import iskallia.vault.init.ModConfigs;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class SetNode<T extends PlayerSet> implements INBTSerializable<CompoundNBT> {

    private SetGroup<T> group;
    private int level;

    public SetNode(SetGroup<T> group, int level) {
        this.group = group;
        this.level = level;
    }

    public SetGroup<T> getGroup() {
        return this.group;
    }

    public int getLevel() {
        return this.level;
    }

    public T getSet() {
        if (!isActive())return null;
        return this.getGroup().getSet(this.getLevel());
    }

    public String getName() {
        return this.getGroup().getName(this.getLevel());
    }

    public boolean isActive() {
        return this.level != 0;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("Name", this.getGroup().getParentName());
        nbt.putInt("Level", this.getLevel());
        return nbt;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deserializeNBT(CompoundNBT nbt) {
        String groupName = nbt.getString("Name");
        this.group = (SetGroup<T>) ModConfigs.SETS.getByName(groupName);
        this.level = nbt.getInt("Level");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        SetNode<?> that = (SetNode<?>) other;

        return this.level == that.level &&
                this.group.getParentName().equals(that.group.getParentName());
    }

    /* ----------------------------------------- */

    public static <T extends PlayerSet> SetNode<T> fromNBT(CompoundNBT nbt, Class<T> clazz) {
        SetGroup<T> group = (SetGroup<T>) ModConfigs.SETS.getByName(nbt.getString("Name"));
        int level = nbt.getInt("Level");
        return new SetNode<>(group, level);
    }

}

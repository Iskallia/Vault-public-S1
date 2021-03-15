package iskallia.vault.skill.ability;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.type.PlayerAbility;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class AbilityNode<T extends PlayerAbility> implements INBTSerializable<CompoundNBT> {

    private AbilityGroup<T> group;
    private int level;

    public AbilityNode(AbilityGroup<T> group, int level) {
        this.group = group;
        this.level = level;
    }

    public AbilityGroup<T> getGroup() {
        return group;
    }

    public int getLevel() {
        return level;
    }

    public T getAbility() {
        if (!isLearned()) return null;
        return this.group.getAbility(this.level);
    }

    public String getName() {
        return this.group.getName(this.level);
    }

    public boolean isLearned() {
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
        this.group = (AbilityGroup<T>) ModConfigs.ABILITIES.getByName(groupName);
        this.level = nbt.getInt("Level");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        AbilityNode<?> that = (AbilityNode<?>) other;

        return this.level == that.level
                && this.group.getParentName().equals(that.group.getParentName());
    }

    /* ----------------------------------------- */

    public static <T extends PlayerAbility> AbilityNode<T> fromNBT(CompoundNBT nbt, Class<T> clazz) {
        AbilityGroup<T> group = (AbilityGroup<T>) ModConfigs.ABILITIES.getByName(nbt.getString("Name"));
        int level = nbt.getInt("Level");
        return new AbilityNode<>(group, level);
    }


}

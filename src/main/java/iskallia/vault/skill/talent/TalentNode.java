package iskallia.vault.skill.talent;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.type.PlayerTalent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class TalentNode<T extends PlayerTalent> implements INBTSerializable<CompoundNBT> {

    private TalentGroup<T> group;
    private int level;

    public TalentNode(TalentGroup<T> group, int level) {
        this.group = group;
        this.level = level;
    }

    public TalentGroup<T> getGroup() {
        return this.group;
    }

    public int getLevel() {
        return this.level;
    }

    public T getTalent() {
        if (!isLearned()) return null;
        return this.getGroup().getTalent(this.getLevel());
    }

    public String getName() {
        return this.getGroup().getName(this.getLevel());
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
        this.group = (TalentGroup<T>) ModConfigs.TALENTS.getByName(groupName);
        this.level = nbt.getInt("Level");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        TalentNode<?> that = (TalentNode<?>) other;

        return this.level == that.level &&
                this.group.getParentName().equals(that.group.getParentName());
    }

    /* ----------------------------------------- */

    public static <T extends PlayerTalent> TalentNode<T> fromNBT(CompoundNBT nbt, Class<T> clazz) {
        TalentGroup<T> group = (TalentGroup<T>) ModConfigs.TALENTS.getByName(nbt.getString("Name"));
        int level = nbt.getInt("Level");
        return new TalentNode<>(group, level);
    }

}

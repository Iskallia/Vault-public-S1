package iskallia.vault.research.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.research.Restrictions;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

public abstract class Research {

    @Expose protected String name;
    @Expose protected int cost;
    @Expose protected boolean usesKnowledge;
    @Expose protected String gatedBy;

    public Research(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public boolean isGated() {
        return gatedBy != null;
    }

    public String gatedBy() {
        return gatedBy;
    }

    public boolean usesKnowledge() {
        return usesKnowledge;
    }

    public abstract boolean restricts(Item item, Restrictions.Type restrictionType);

    public abstract boolean restricts(Block block, Restrictions.Type restrictionType);

    public abstract boolean restricts(EntityType<?> entityType, Restrictions.Type restrictionType);

}

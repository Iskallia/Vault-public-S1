package iskallia.vault.research.type;

import iskallia.vault.research.Restrictions;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

public class MinimapResearch extends Research {

    public MinimapResearch(String name, int cost) {
        super(name, cost);

        // TODO: Implement this bad boi
    }

    @Override
    public boolean restricts(Item item, Restrictions.Type restrictionType) {
        return false;
    }

    @Override
    public boolean restricts(Block block, Restrictions.Type restrictionType) {
        return false;
    }

    @Override
    public boolean restricts(EntityType<?> entityType, Restrictions.Type restrictionType) {
        return false;
    }

}

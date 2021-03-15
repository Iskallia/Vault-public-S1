package iskallia.vault.altar;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AltarInfusionRecipe {

    private final UUID player;
    private List<RequiredItem> requiredItems = new ArrayList<>();


    public AltarInfusionRecipe(UUID uuid) {
        this.player = uuid;
    }

    public AltarInfusionRecipe(UUID uuid, List<RequiredItem> items) {
        this.player = uuid;
        this.requiredItems = items;
    }


    public static AltarInfusionRecipe deserialize(CompoundNBT nbt) {
        UUID player = nbt.getUniqueId("player");
        ListNBT list = nbt.getList("requiredItems", Constants.NBT.TAG_COMPOUND);
        List<RequiredItem> requiredItems = new ArrayList<>();
        for (INBT tag : list) {
            CompoundNBT compound = (CompoundNBT) tag;
            requiredItems.add(RequiredItem.deserializeNBT(compound));
        }
        return new AltarInfusionRecipe(player, requiredItems);
    }

    public static CompoundNBT serialize(AltarInfusionRecipe recipe) {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT list = new ListNBT();
        for (RequiredItem item : recipe.getRequiredItems()) {
            list.add(RequiredItem.serializeNBT(item));
        }
        nbt.putUniqueId("player", recipe.getPlayer());
        nbt.put("requiredItems", list);
        return nbt;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public List<RequiredItem> getRequiredItems() {
        return requiredItems;
    }

    public boolean isComplete() {
        for (RequiredItem item : requiredItems) {
            if (!item.reachedAmountRequired()) {
                return false;
            }
        }
        return true;
    }
}

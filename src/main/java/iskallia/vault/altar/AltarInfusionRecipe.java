package iskallia.vault.altar;

import iskallia.vault.init.ModConfigs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AltarInfusionRecipe {

    private final UUID player;
    private List<RequiredItem> requiredItems = new ArrayList<>();

    public AltarInfusionRecipe(UUID uuid, List<RequiredItem> items) {
        this.player = uuid;
        this.requiredItems = items;
    }

    public AltarInfusionRecipe(ServerWorld world, PlayerEntity player) {
        this(player.getUniqueID(), ModConfigs.VAULT_ALTAR.generateItems(world, player));
    }

    public AltarInfusionRecipe(UUID player) {
        this.player = player;
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


    public CompoundNBT serialize() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT list = new ListNBT();
        for (RequiredItem item : this.getRequiredItems()) {
            list.add(RequiredItem.serializeNBT(item));
        }
        nbt.putUniqueId("player", this.getPlayer());
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
        if (this.requiredItems.isEmpty()) return false;
        for (RequiredItem item : requiredItems) {
            if (!item.reachedAmountRequired()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasEqualQuantities(AltarInfusionRecipe other) {
        int equals = 0;
        for (int i = 0; i < this.getRequiredItems().size(); i++) {
            RequiredItem item = this.getRequiredItems().get(i);
            if (item.getCurrentAmount() == other.getRequiredItems().get(i).getCurrentAmount()) equals++;
        }
        return equals == 4;
    }
}

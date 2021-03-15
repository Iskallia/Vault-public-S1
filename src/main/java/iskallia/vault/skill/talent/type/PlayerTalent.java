package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import net.minecraft.entity.player.PlayerEntity;

public abstract class PlayerTalent {

    @Expose private int cost;

    public PlayerTalent(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return this.cost;
    }

    public void onAdded(PlayerEntity player) { }

    public void tick(PlayerEntity player) { }

    public void onRemoved(PlayerEntity player) { }

}

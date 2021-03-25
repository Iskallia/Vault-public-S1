package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityDamageSource;

public class SelfSustainAbility extends PlayerAbility {

    @Expose private final int sustain;

    public SelfSustainAbility(int cost, int sustain) {
        super(cost, Behavior.RELEASE_TO_PERFORM);
        this.sustain = sustain;
    }

    public int getSustain() {
        return sustain;
    }

    @Override
    public void onAction(PlayerEntity player, boolean active) {
        float health = player.getHealth();
        player.hurt(EntityDamageSource.playerAttack(player), 1);
        player.setHealth(health - sustain);
        player.getFoodData().eat(sustain, sustain / 5f);
    }

}

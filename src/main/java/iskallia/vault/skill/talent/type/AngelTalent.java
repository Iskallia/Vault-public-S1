package iskallia.vault.skill.talent.type;

import net.minecraft.entity.player.PlayerEntity;

public class AngelTalent extends PlayerTalent {

    public AngelTalent(int cost) {
        super(cost);
    }

    @Override
    public void tick(PlayerEntity player) {
        if (!player.abilities.mayfly) {
            player.abilities.mayfly = true;
        }
        player.onUpdateAbilities();
    }

    @Override
    public void onRemoved(PlayerEntity player) {
        player.abilities.mayfly = false;
        player.onUpdateAbilities();
    }

}

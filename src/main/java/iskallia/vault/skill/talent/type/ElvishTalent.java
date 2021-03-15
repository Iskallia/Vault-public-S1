package iskallia.vault.skill.talent.type;

import net.minecraft.entity.player.PlayerEntity;

public class ElvishTalent extends PlayerTalent {

	public ElvishTalent(int cost) {
		super(cost);
	}

	@Override
	public void tick(PlayerEntity player) {
		player.fallDistance = 0.0F;
	}

}

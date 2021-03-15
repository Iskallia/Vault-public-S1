package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;

public class UnbreakableTalent extends PlayerTalent {

	@Expose private final float extraUnbreaking;

	public UnbreakableTalent(int cost, int extraUnbreaking) {
		super(cost);
		this.extraUnbreaking = extraUnbreaking;
	}

	public float getExtraUnbreaking() {
		return this.extraUnbreaking;
	}

}

package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;

public class CarapaceTalent extends PlayerTalent {

	@Expose private float resistanceBonus;

	public CarapaceTalent(int cost, float resistanceBonus) {
		super(cost);
		this.resistanceBonus = resistanceBonus;
	}

	public float getResistanceBonus() {
		return this.resistanceBonus;
	}

}

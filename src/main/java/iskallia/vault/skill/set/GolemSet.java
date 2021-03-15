package iskallia.vault.skill.set;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.gear.VaultGear;

public class GolemSet extends PlayerSet {

	@Expose private final float resistanceBonus;

	public GolemSet(float resistanceBonus) {
		super(VaultGear.Set.GOLEM);
		this.resistanceBonus = resistanceBonus;
	}

	public float getResistanceBonus() {
		return this.resistanceBonus;
	}

}

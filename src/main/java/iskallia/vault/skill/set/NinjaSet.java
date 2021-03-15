package iskallia.vault.skill.set;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.gear.VaultGear;

public class NinjaSet extends PlayerSet {

	@Expose private float parryChance;

	public NinjaSet(float parryChance) {
		super(VaultGear.Set.NINJA);
		this.parryChance = parryChance;
	}

	public float getParryChance() {
		return this.parryChance;
	}

}

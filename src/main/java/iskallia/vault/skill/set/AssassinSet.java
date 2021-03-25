package iskallia.vault.skill.set;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.talent.type.EffectTalent;
import net.minecraft.potion.Effects;

public class AssassinSet extends EffectSet {

	@Expose private float parryChance;

	public AssassinSet(int speedAmplifier, float parryChance) {
		super(VaultGear.Set.ASSASSIN, new EffectTalent(0, Effects.MOVEMENT_SPEED, speedAmplifier,
				EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD));
		this.parryChance = parryChance;
	}

	public float getParryChance() {
		return this.parryChance;
	}

}

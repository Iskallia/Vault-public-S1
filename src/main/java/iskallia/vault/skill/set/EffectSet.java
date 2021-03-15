package iskallia.vault.skill.set;

import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.talent.type.EffectTalent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.util.registry.Registry;

public class EffectSet extends TalentSet<EffectTalent> {

	public EffectSet(VaultGear.Set set, Effect effect, int amplifier, EffectTalent.Type type, EffectTalent.Operator operator) {
		this(set, Registry.EFFECTS.getKey(effect).toString(), amplifier, type.toString(), operator.toString());
	}

	public EffectSet(VaultGear.Set set, String effect, int amplifier, String type, String operator) {
		super(set, new EffectTalent(-1, effect, amplifier, type, operator));
	}

	public EffectSet(VaultGear.Set set, EffectTalent child) {
		super(set, child);
	}

	@Override
	public void onAdded(PlayerEntity player) {
		this.getChild().onAdded(player);
	}

	@Override
	public void onTick(PlayerEntity player) {
		this.getChild().tick(player);
	}

	@Override
	public void onRemoved(PlayerEntity player) {
		this.getChild().onRemoved(player);
	}

}

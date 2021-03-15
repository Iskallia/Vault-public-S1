package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.set.*;
import iskallia.vault.skill.talent.type.EffectTalent;
import net.minecraft.potion.Effects;

import java.util.Arrays;
import java.util.List;

public class SetsConfig extends Config {

	@Expose public SetGroup<PlayerSet> DRAGON;
	@Expose public SetGroup<PlayerSet> RIFT;
	@Expose public SetGroup<GolemSet> GOLEM;
	@Expose public SetGroup<EffectSet> GOBLIN;
	@Expose public SetGroup<AssassinSet> ASSASSIN;
	@Expose public SetGroup<EffectSet> SLAYER;
	@Expose public SetGroup<VampirismSet> VAMPIRE;
	@Expose public SetGroup<EffectSet> BRUTE;
	@Expose public SetGroup<EffectSet> DRYAD;
	@Expose public SetGroup<EffectSet> TITAN;
	@Expose public SetGroup<NinjaSet> NINJA;
	@Expose public SetGroup<EffectSet> TREASURE_HUNTER;

	@Override
	public String getName() {
		return "sets";
	}

	public List<SetGroup<?>> getAll() {
		return Arrays.asList(DRAGON, RIFT, GOLEM, GOBLIN, ASSASSIN, SLAYER, VAMPIRE, BRUTE, DRYAD, TITAN, NINJA, TREASURE_HUNTER);
	}

	public SetGroup<?> getByName(String name) {
		return this.getAll().stream().filter(group -> group.getParentName().equals(name)).findFirst()
				.orElseThrow(() -> new IllegalStateException("Unknown set with name " + name));
	}

	@Override
	protected void reset() {
		DRAGON = SetGroup.of("Dragon", 1, i -> new PlayerSet(VaultGear.Set.DRAGON));
		RIFT = SetGroup.of("Rift", 1, i -> new PlayerSet(VaultGear.Set.RIFT));
		GOLEM = SetGroup.of("Golem", 1, i -> new GolemSet(0.08F));
		GOBLIN = SetGroup.of("Goblin", 1, i -> new EffectSet(VaultGear.Set.GOBLIN,
				Effects.LUCK, 0, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD));
		ASSASSIN = SetGroup.of("Assassin", 1, i -> new AssassinSet(1, 0.1F));
		SLAYER = SetGroup.of("Slayer", 1, i -> new EffectSet(VaultGear.Set.SLAYER,
				Effects.STRENGTH, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD));
		VAMPIRE = SetGroup.of("Vampire", 1, i -> new VampirismSet(0.05f));
		BRUTE = SetGroup.of("Brute", 1, i -> new EffectSet(VaultGear.Set.BRUTE,
				Effects.STRENGTH, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD));
		DRYAD = SetGroup.of("Dryad", 1, i -> new EffectSet(VaultGear.Set.DRYAD,
				Effects.REGENERATION, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD));
		TITAN = SetGroup.of("Titan", 1, i -> new EffectSet(VaultGear.Set.TITAN,
				Effects.RESISTANCE, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD));
		NINJA = SetGroup.of("Ninja", 1, i -> new NinjaSet(0.2F));
		TREASURE_HUNTER = SetGroup.of("Treasure Hunter", 1, i -> new EffectSet(VaultGear.Set.TREASURE_HUNTER,
				Effects.LUCK, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD));
	}

}

package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.Vault;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.type.EffectAbility;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.util.WeightedList;
import iskallia.vault.world.raid.modifier.*;
import net.minecraft.potion.Effects;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VaultModifiersConfig extends Config {

	@Expose public List<MaxMobsModifier> MAX_MOBS_MODIFIERS;
	@Expose public List<TimerModifier> TIMER_MODIFIERS;
	@Expose public List<LevelModifier> LEVEL_MODIFIERS;
	@Expose public List<EffectModifier> EFFECT_MODIFIERS;
	@Expose public List<NoExitModifier> NO_EXIT_MODIFIERS;

	@Expose public List<Level> LEVELS;

	@Override
	public String getName() {
		return "vault_modifiers";
	}

	public List<VaultModifier> getAll() {
		return Stream.of(MAX_MOBS_MODIFIERS, TIMER_MODIFIERS, LEVEL_MODIFIERS, EFFECT_MODIFIERS, NO_EXIT_MODIFIERS).flatMap(Collection::stream).collect(Collectors.toList());
	}

	public VaultModifier getByName(String name) {
		return this.getAll().stream().filter(group -> group.getName().equals(name)).findFirst().orElse(null);
	}

	@Override
	protected void reset() {
		this.LEVELS = new ArrayList<>();

		this.MAX_MOBS_MODIFIERS = Arrays.asList(
				new MaxMobsModifier("Silent", Vault.id("textures/gui/modifiers/silent.png"), -2),
				new MaxMobsModifier("Lonely", Vault.id("textures/gui/modifiers/lonely.png"), -1),
				new MaxMobsModifier("Crowded", Vault.id("textures/gui/modifiers/crowded.png"), +1),
				new MaxMobsModifier("Chaotic", Vault.id("textures/gui/modifiers/chaotic.png"), +2));

		this.TIMER_MODIFIERS = Arrays.asList(
				new TimerModifier("Fast", Vault.id("textures/gui/modifiers/fast.png"), -20 * 60 * 5),
				new TimerModifier("Rush", Vault.id("textures/gui/modifiers/rush.png"), -20 * 60 * 10));

		this.LEVEL_MODIFIERS = Arrays.asList(
				new LevelModifier("Easy", Vault.id("textures/gui/modifiers/easy.png"), -5),
				new LevelModifier("Hard", Vault.id("textures/gui/modifiers/hard.png"), 5));

		this.EFFECT_MODIFIERS = Arrays.asList(
				new EffectModifier("Treasure", Vault.id("textures/gui/modifiers/treasure.png"), Effects.LUCK, 1, "ADD", EffectAbility.Type.ICON_ONLY),
				new EffectModifier("Unlucky", Vault.id("textures/gui/modifiers/unlucky.png"), Effects.UNLUCK, 1, "ADD", EffectAbility.Type.ICON_ONLY));

		this.NO_EXIT_MODIFIERS = Arrays.asList(
				new NoExitModifier("Raffle", Vault.id("textures/gui/modifiers/no_exit.png")),
				new NoExitModifier("Locked", Vault.id("textures/gui/modifiers/no_exit.png")));

		Level level = new Level(5);

		level.DEFAULT_POOLS.put(VaultRarity.COMMON.name(), Arrays.asList(
				new Pool(2, 2)
						.add("Silent", 1)
						.add("Lonely", 1)
						.add("Crowded", 1)
						.add("Chaos", 1)
						.add("Fast", 1)
						.add("Rush", 1)
						.add("Easy", 1)
						.add("Hard", 1)
						.add("Treasure", 1)
						.add("Unlucky", 1),
				new Pool(1, 1)
						.add("Locked", 1)
						.add("Dummy", 3)
		));

		level.DEFAULT_POOLS.put(VaultRarity.RARE.name(), Arrays.asList(
				new Pool(2, 2)
						.add("Silent", 1)
						.add("Lonely", 1)
						.add("Crowded", 1)
						.add("Chaos", 1)
						.add("Fast", 1)
						.add("Rush", 1)
						.add("Easy", 1)
						.add("Hard", 1)
						.add("Treasure", 1)
						.add("Unlucky", 1),
				new Pool(1, 1)
						.add("Locked", 1)
						.add("Dummy", 3)
		));

		level.DEFAULT_POOLS.put(VaultRarity.EPIC.name(), Arrays.asList(
				new Pool(2, 2)
						.add("Crowded", 1)
						.add("Chaos", 1)
						.add("Fast", 1)
						.add("Rush", 1)
						.add("Easy", 1)
						.add("Hard", 1)
						.add("Treasure", 1)
						.add("Unlucky", 1),
				new Pool(1, 1)
						.add("Locked", 1)
						.add("Dummy", 3)
		));

		level.DEFAULT_POOLS.put(VaultRarity.OMEGA.name(), Arrays.asList(
				new Pool(2, 2)
						.add("Crowded", 1)
						.add("Chaos", 1)
						.add("Fast", 1)
						.add("Rush", 1)
						.add("Easy", 1)
						.add("Hard", 1)
						.add("Treasure", 1)
						.add("Unlucky", 1),
				new Pool(1, 1)
						.add("Locked", 1)
						.add("Dummy", 3)
		));

		level.RAFFLE_POOLS.put(VaultRarity.COMMON.name(), Arrays.asList(
				new Pool(2, 2)
						.add("Silent", 1)
						.add("Lonely", 1)
						.add("Crowded", 1)
						.add("Chaos", 1)
						.add("Fast", 1)
						.add("Rush", 1)
						.add("Easy", 1)
						.add("Hard", 1)
						.add("Treasure", 1)
						.add("Unlucky", 1),
				new Pool(1, 1)
						.add("Raffle", 1)
		));

		level.RAFFLE_POOLS.put(VaultRarity.RARE.name(), Arrays.asList(
				new Pool(2, 2)
						.add("Silent", 1)
						.add("Lonely", 1)
						.add("Crowded", 1)
						.add("Chaos", 1)
						.add("Fast", 1)
						.add("Rush", 1)
						.add("Easy", 1)
						.add("Hard", 1)
						.add("Treasure", 1)
						.add("Unlucky", 1),
				new Pool(1, 1)
						.add("Raffle", 1)
		));

		level.RAFFLE_POOLS.put(VaultRarity.EPIC.name(), Arrays.asList(
				new Pool(2, 2)
						.add("Crowded", 1)
						.add("Chaos", 1)
						.add("Fast", 1)
						.add("Rush", 1)
						.add("Easy", 1)
						.add("Hard", 1)
						.add("Treasure", 1)
						.add("Unlucky", 1),
				new Pool(1, 1)
						.add("Raffle", 1)
		));

		level.RAFFLE_POOLS.put(VaultRarity.OMEGA.name(), Arrays.asList(
				new Pool(2, 2)
						.add("Crowded", 1)
						.add("Chaos", 1)
						.add("Fast", 1)
						.add("Rush", 1)
						.add("Easy", 1)
						.add("Hard", 1)
						.add("Treasure", 1)
						.add("Unlucky", 1),
				new Pool(1, 1)
						.add("Raffle", 1)
		));

		this.LEVELS.add(level);
	}

	public Set<VaultModifier> getRandom(VaultRarity rarity, Random random, int level, boolean raffle) {
		Level override = this.getForLevel(level);
		List<Pool> pools = raffle ? override.RAFFLE_POOLS.get(rarity.name()) : override.DEFAULT_POOLS.get(rarity.name());
		if(pools == null)return new HashSet<>();
		Set<VaultModifier> modifiers = new HashSet<>();
		pools.stream().map(pool -> pool.getRandom(random)).forEach(modifiers::addAll);
		return modifiers;
	}

	public Level getForLevel(int level) {
		for(int i = 0; i < this.LEVELS.size(); i++) {
			if(level < this.LEVELS.get(i).MIN_LEVEL) {
				if(i == 0)break;
				return this.LEVELS.get(i - 1);
			} else if(i == this.LEVELS.size() - 1) {
				return this.LEVELS.get(i);
			}
		}

		return Level.EMPTY;
	}

	public static class Level {
		public static Level EMPTY = new Level(0);

		@Expose public int MIN_LEVEL;
		@Expose public Map<String, List<Pool>> DEFAULT_POOLS;
		@Expose public Map<String, List<Pool>> RAFFLE_POOLS;

		public Level(int minLevel) {
			this.MIN_LEVEL = minLevel;
			this.DEFAULT_POOLS = new LinkedHashMap<>();
			this.RAFFLE_POOLS = new LinkedHashMap<>();
		}
	}

	public static class Pool {
		@Expose public int MIN_ROLLS;
		@Expose public int MAX_ROLLS;
		@Expose public WeightedList<String> POOL;

		public Pool(int min, int max) {
			this.MIN_ROLLS = min;
			this.MAX_ROLLS = max;
			this.POOL = new WeightedList<>();
		}

		public Pool add(String name, int weight) {
			this.POOL.add(name, weight);
			return this;
		}

		public Set<VaultModifier> getRandom(Random random) {
			int rolls = Math.min(this.MIN_ROLLS, this.MAX_ROLLS) + random.nextInt(Math.abs(this.MIN_ROLLS - this.MAX_ROLLS) + 1);

			Set<String> res = new HashSet<>();

			while(res.size() < rolls && res.size() < POOL.size()) {
				res.add(POOL.getRandom(random));
			}

			return res.stream().map(s -> ModConfigs.VAULT_MODIFIERS.getByName(s)).filter(Objects::nonNull).collect(Collectors.toSet());
		}
	}

}

package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.gen.ruletest.VaultRuleTest;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VaultOreConfig extends Config {

	@Expose private int ORES_PER_VAULT;
	@Expose private List<Ore> ORES = new ArrayList<>();
	private int totalWeight;

	@Override
	public String getName() {
		return "vault_ore";
	}

	public Ore[] getPool(long worldSeed, int chunkX, int chunkZ, SharedSeedRandom rand) {
		chunkX <<= 4; chunkZ <<= 4;
		int regionX = chunkX < 0 ? chunkX / VaultRaid.REGION_SIZE - 1 : chunkX / VaultRaid.REGION_SIZE;
		int regionZ = chunkZ < 0 ? chunkZ / VaultRaid.REGION_SIZE - 1 : chunkZ / VaultRaid.REGION_SIZE;
		rand.setLargeFeatureSeed(worldSeed, regionX, regionZ); //Lifty lifty :P

		Set<Ore> indices = new HashSet<>();

		while(indices.size() < ORES_PER_VAULT) {
			indices.add(this.getWeightedOreAt(rand.nextInt(this.getTotalWeight())));
		}

		return indices.toArray(new Ore[0]);
	}

	public int getTotalWeight() {
		if(this.totalWeight == 0) {
			for(Ore ore: this.ORES) {
				this.totalWeight += ore.WEIGHT;
			}
		}

		return this.totalWeight;
	}

	public Ore getWeightedOreAt(int index) {
		Ore current = null;

		for(Ore ore: this.ORES) {
			current = ore;
			index -= ore.WEIGHT;
			if(index < 0)break;
		}

		return current;
	}

	@Override
	protected void reset() {
		this.ORES_PER_VAULT = 2;

		ORES.add(new Ore(ModBlocks.ALEXANDRITE_ORE.getRegistryName().toString(), 64, 3, 1));
		ORES.add(new Ore(ModBlocks.BENITOITE_ORE.getRegistryName().toString(), 64, 3, 1));
		ORES.add(new Ore(ModBlocks.LARIMAR_ORE.getRegistryName().toString(), 64, 3, 1));
		ORES.add(new Ore(ModBlocks.BLACK_OPAL_ORE.getRegistryName().toString(), 64, 3, 1));
		ORES.add(new Ore(ModBlocks.PAINITE_ORE.getRegistryName().toString(), 64, 3, 1));
		ORES.add(new Ore(ModBlocks.ISKALLIUM_ORE.getRegistryName().toString(), 64, 3, 1));
		ORES.add(new Ore(ModBlocks.RENIUM_ORE.getRegistryName().toString(), 64, 3, 1));
		ORES.add(new Ore(ModBlocks.GORGINITE_ORE.getRegistryName().toString(), 64, 3, 1));
		ORES.add(new Ore(ModBlocks.SPARKLETINE_ORE.getRegistryName().toString(), 64, 3, 1));
		ORES.add(new Ore(ModBlocks.WUTODIE_ORE.getRegistryName().toString(), 64, 3, 1));
	}

	public static class Ore {
		@Expose public String NAME;
		@Expose public int TRIES;
		@Expose public int SIZE;
		@Expose public int WEIGHT;

		public Ore(String name, int tries, int size, int weight) {
			this.NAME = name;
			this.TRIES = tries;
			this.SIZE = size;
			this.WEIGHT = weight;
		}

		public OreFeatureConfig toConfig() {
			BlockState state = Registry.BLOCK.getOptional(new ResourceLocation(this.NAME)).orElse(Blocks.DIORITE).getDefaultState();
			return new OreFeatureConfig(VaultRuleTest.INSTANCE, state, this.SIZE);
		}
	}

	/*
	public static class Pool {
		@Expose private List<Ore> ORES;
		@Expose private int WEIGHT;
		@Expose private int TRIES;
		private int totalWeight;

		public Pool(int weight, int tries) {
			this.ORES = new ArrayList<>();
			this.WEIGHT = weight;
			this.TRIES = tries;
		}

		public Pool add(Block block, int size, int weight) {
			this.ORES.add(new Ore(block.getRegistryName().toString(), size, weight));
			return this;
		}

		public Pool add(Block block, int size) {
			return this.add(block, size, 1);
		}

		public int getTries() {
			return this.TRIES;
		}

		public OreFeatureConfig getRandom(Random random) {
			return this.getWeightedOreAt(random.nextInt(this.getTotalWeight())).toConfig();
		}

		public Ore getWeightedOreAt(int index) {
			Ore current = null;

			for(Ore ore: this.ORES) {
				current = ore;
				index -= ore.WEIGHT;
				if(index < 0)break;
			}

			return current;
		}

		public int getTotalWeight() {
			if(this.totalWeight == 0) {
				for(Ore ore: this.ORES) {
					this.totalWeight += ore.WEIGHT;
				}
			}

			return this.totalWeight;
		}
	}*/

}

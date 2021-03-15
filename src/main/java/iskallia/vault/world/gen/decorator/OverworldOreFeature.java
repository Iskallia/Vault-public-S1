package iskallia.vault.world.gen.decorator;

import com.mojang.serialization.Codec;
import iskallia.vault.Vault;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.event.RegistryEvent;

import java.util.Random;

public class OverworldOreFeature extends OreFeature {

	public static Feature<OreFeatureConfig> INSTANCE;

	public OverworldOreFeature(Codec<OreFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean func_241855_a(ISeedReader world, ChunkGenerator gen, Random random, BlockPos pos, OreFeatureConfig config) {
		if(world.getWorld().getDimensionKey() != World.OVERWORLD) {
			return false;
		}

		if(config.size == 1) {
			if(config.target.test(world.getBlockState(pos), random)) {
				world.setBlockState(pos, config.state, 2);
				return true;
			}

			return false;
		} else {
			return super.func_241855_a(world, gen, random, pos, config);
		}
	}

	public static void register(RegistryEvent.Register<Feature<?>> event) {
		INSTANCE = new OverworldOreFeature(OreFeatureConfig.field_236566_a_);
		INSTANCE.setRegistryName(Vault.id("overworld_ore"));
		event.getRegistry().register(INSTANCE);
	}

}

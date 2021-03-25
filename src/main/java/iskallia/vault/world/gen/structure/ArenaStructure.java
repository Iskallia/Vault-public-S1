package iskallia.vault.world.gen.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import iskallia.vault.Vault;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.MarginedStructureStart;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.function.Supplier;

import net.minecraft.world.gen.feature.structure.Structure.IStartFactory;

public class ArenaStructure extends Structure<ArenaStructure.Config> {

    public static final int START_Y = 32;

    public ArenaStructure(Codec<ArenaStructure.Config> config) {
        super(config);
    }

    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.UNDERGROUND_STRUCTURES;
    }

    public IStartFactory<ArenaStructure.Config> getStartFactory() {
        return (p_242778_1_, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_) -> new Start(this, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_);
    }

    public static class Start extends MarginedStructureStart<ArenaStructure.Config> {
        private final ArenaStructure structure;

        public Start(ArenaStructure structure, int chunkX, int chunkZ, MutableBoundingBox box, int references, long worldSeed) {
            super(structure, chunkX, chunkZ, box, references, worldSeed);
            this.structure = structure;
        }

        public void generatePieces(DynamicRegistries registry, ChunkGenerator gen, TemplateManager manager, int chunkX, int chunkZ, Biome biome, Config config) {
            BlockPos blockpos = new BlockPos(chunkX * 16, START_Y, chunkZ * 16);
            ArenaStructure.Pools.init();
            JigsawGenerator.addPieces(registry, config.toVillageConfig(), AbstractVillagePiece::new, gen, manager, blockpos, this.pieces, this.random, false, false);
            this.calculateBoundingBox();
        }
    }

    public static class Config implements IFeatureConfig {
        public static final Codec<ArenaStructure.Config> CODEC = RecordCodecBuilder.create(builder -> {
            return builder.group(JigsawPattern.CODEC.fieldOf("start_pool").forGetter(ArenaStructure.Config::getStartPool),
                    Codec.intRange(0, Integer.MAX_VALUE).fieldOf("size").forGetter(ArenaStructure.Config::getSize))
                    .apply(builder, ArenaStructure.Config::new);
        });

        private final Supplier<JigsawPattern> startPool;
        private final int size;

        public Config(Supplier<JigsawPattern> startPool, int size) {
            this.startPool = startPool;
            this.size = size;
        }

        public int getSize() {
            return this.size;
        }

        public Supplier<JigsawPattern> getStartPool() {
            return this.startPool;
        }

        public VillageConfig toVillageConfig() {
            return new VillageConfig(this.getStartPool(), this.getSize());
        }

    }

    public static class Pools {
        public static final JigsawPattern START = JigsawPatternRegistry.register(
                new JigsawPattern(Vault.id("arena/starts"), new ResourceLocation("empty"), ImmutableList.of(
                        Pair.of(JigsawPiece.single(Vault.sId("arena/arena1/p_p"), ProcessorLists.EMPTY), 1)
                ), JigsawPattern.PlacementBehaviour.RIGID));

        public static void init() {

        }
    }

}

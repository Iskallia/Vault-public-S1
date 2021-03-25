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

public class VaultStructure extends Structure<VaultStructure.Config> {

    public static final int START_Y = 128;

    public VaultStructure(Codec<VaultStructure.Config> config) {
        super(config);
    }

    public GenerationStage.Decoration func_236396_f_() {
        return GenerationStage.Decoration.UNDERGROUND_STRUCTURES;
    }

    public Structure.IStartFactory<VaultStructure.Config> getStartFactory() {
        return (p_242778_1_, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_) -> new Start(this, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_);
    }

    public static class Start extends MarginedStructureStart<VaultStructure.Config> {
        private final VaultStructure structure;

        public Start(VaultStructure structure, int chunkX, int chunkZ, MutableBoundingBox box, int references, long worldSeed) {
            super(structure, chunkX, chunkZ, box, references, worldSeed);
            this.structure = structure;
        }

        public void func_230364_a_(DynamicRegistries registry, ChunkGenerator gen, TemplateManager manager, int chunkX, int chunkZ, Biome biome, Config config) {
            BlockPos blockpos = new BlockPos(chunkX * 16, START_Y, chunkZ * 16);
            VaultStructure.Pools.init();
            JigsawGenerator.func_242837_a(registry, config.toVillageConfig(), AbstractVillagePiece::new, gen, manager, blockpos, this.components, this.rand, false, false);
            this.recalculateStructureSize();
        }
    }

    public static class Config implements IFeatureConfig {
        public static final Codec<VaultStructure.Config> CODEC = RecordCodecBuilder.create(builder -> {
            return builder.group(JigsawPattern.field_244392_b_.fieldOf("start_pool").forGetter(VaultStructure.Config::getStartPool),
                    Codec.intRange(0, Integer.MAX_VALUE).fieldOf("size").forGetter(VaultStructure.Config::getSize))
                    .apply(builder, VaultStructure.Config::new);
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
        public static final JigsawPattern START = JigsawPatternRegistry.func_244094_a(
                new JigsawPattern(Vault.id("vault/starts"), new ResourceLocation("empty"), ImmutableList.of(
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start1"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start2"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start3"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start4"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start5"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start6"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start7"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start8"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start9"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start10"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start11"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start12"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start13"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start14"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start15"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start16"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start17"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start18"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start19"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start20"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start21"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start22"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start23"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start24"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start25"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start26"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start27"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start28"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start29"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start30"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start31"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start32"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start33"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start34"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start35"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start36"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start37"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start38"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start39"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start40"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start41"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start42"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start43"), ProcessorLists.field_244101_a), 1),
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("vault/rooms/start44"), ProcessorLists.field_244101_a), 1)

                ), JigsawPattern.PlacementBehaviour.RIGID));

        public static final JigsawPattern FINAL_START = JigsawPatternRegistry.func_244094_a(
                new JigsawPattern(Vault.id("final_vault/start"), new ResourceLocation("empty"), ImmutableList.of(
                        Pair.of(JigsawPiece.func_242861_b(Vault.sId("final_vault/start"), ProcessorLists.field_244101_a), 1)
                ), JigsawPattern.PlacementBehaviour.RIGID));

        public static void init() {

        }
    }


}

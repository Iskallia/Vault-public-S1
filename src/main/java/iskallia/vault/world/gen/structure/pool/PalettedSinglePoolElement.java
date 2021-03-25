package iskallia.vault.world.gen.structure.pool;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import iskallia.vault.init.ModStructures;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class PalettedSinglePoolElement extends JigsawPiece {

	private static final Codec<Either<ResourceLocation, Template>> TEMPLATE_CODEC = Codec.of(PalettedSinglePoolElement::encodeTemplate, ResourceLocation.CODEC.map(Either::left));
	public static final Codec<PalettedSinglePoolElement> CODEC = RecordCodecBuilder.create((p_236841_0_) -> {
		return p_236841_0_.group(templateCodec(), processorsCodec(), projectionCodec()).apply(p_236841_0_, PalettedSinglePoolElement::new);
	});
	protected final Either<ResourceLocation, Template> template;
	protected final Supplier<StructureProcessorList> processors;

	private static <T> DataResult<T> encodeTemplate(Either<ResourceLocation, Template> p_236840_0_, DynamicOps<T> p_236840_1_, T p_236840_2_) {
		Optional<ResourceLocation> optional = p_236840_0_.left();
		return !optional.isPresent() ? DataResult.error("Can not serialize a runtime pool element") : ResourceLocation.CODEC.encode(optional.get(), p_236840_1_, p_236840_2_);
	}

	protected static <E extends PalettedSinglePoolElement> RecordCodecBuilder<E, Supplier<StructureProcessorList>> processorsCodec() {
		return IStructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter((p_236845_0_) -> {
			return p_236845_0_.processors;
		});
	}

	protected static <E extends PalettedSinglePoolElement> RecordCodecBuilder<E, Either<ResourceLocation, Template>> templateCodec() {
		return TEMPLATE_CODEC.fieldOf("location").forGetter((p_236842_0_) -> {
			return p_236842_0_.template;
		});
	}

	protected PalettedSinglePoolElement(Either<ResourceLocation, Template> p_i242008_1_, Supplier<StructureProcessorList> p_i242008_2_, JigsawPattern.PlacementBehaviour p_i242008_3_) {
		super(p_i242008_3_);
		this.template = p_i242008_1_;
		this.processors = p_i242008_2_;
	}

	public PalettedSinglePoolElement(Template p_i242009_1_) {
		this(Either.right(p_i242009_1_), () -> {
			return ProcessorLists.EMPTY;
		}, JigsawPattern.PlacementBehaviour.RIGID);
	}

	private Template getTemplate(TemplateManager p_236843_1_) {
		return this.template.map(p_236843_1_::getOrCreate, Function.identity());
	}

	public List<Template.BlockInfo> getDataMarkers(TemplateManager p_214857_1_, BlockPos p_214857_2_, Rotation p_214857_3_, boolean p_214857_4_) {
		Template template = this.getTemplate(p_214857_1_);
		List<Template.BlockInfo> list = template.filterBlocks(p_214857_2_, (new PlacementSettings()).setRotation(p_214857_3_), Blocks.STRUCTURE_BLOCK, p_214857_4_);
		List<Template.BlockInfo> list1 = Lists.newArrayList();

		for(Template.BlockInfo template$blockinfo : list) {
			if (template$blockinfo.nbt != null) {
				StructureMode structuremode = StructureMode.valueOf(template$blockinfo.nbt.getString("mode"));
				if (structuremode == StructureMode.DATA) {
					list1.add(template$blockinfo);
				}
			}
		}

		return list1;
	}

	@Override
	public List<Template.BlockInfo> getShuffledJigsawBlocks(TemplateManager templateManager, BlockPos pos, Rotation rotation, Random random) {
		Template template = this.getTemplate(templateManager);
		List<Template.BlockInfo> list = template.filterBlocks(pos, (new PlacementSettings()).setRotation(rotation), Blocks.JIGSAW, true);
		Collections.shuffle(list, random);
		return list;
	}

	@Override
	public MutableBoundingBox getBoundingBox(TemplateManager templateManager, BlockPos pos, Rotation rotation) {
		Template template = this.getTemplate(templateManager);
		return template.getBoundingBox((new PlacementSettings()).setRotation(rotation), pos);
	}

	@Override
	public boolean place(TemplateManager templateManager, ISeedReader world, StructureManager structureManager,
	                              ChunkGenerator chunkGen, BlockPos pos1, BlockPos pos2, Rotation rotation,
	                              MutableBoundingBox box, Random random, boolean keepJigsaws) {
		return this.generate(null, templateManager, world, structureManager, chunkGen, pos1, pos2, rotation, box, random, keepJigsaws);
	}

	public boolean generate(Supplier<StructureProcessorList> extra, TemplateManager templateManager, ISeedReader world,
	                        StructureManager structureManager, ChunkGenerator chunkGen, BlockPos pos1, BlockPos pos2,
	                        Rotation rotation, MutableBoundingBox box, Random random, boolean keepJigsaws) {
		Template template = this.getTemplate(templateManager);
		PlacementSettings placementsettings = this.getSettings(extra, rotation, box, keepJigsaws);
		if (!template.placeInWorld(world, pos1, pos2, placementsettings, random, 18)) {
			return false;
		} else {
			for(Template.BlockInfo info: Template.processBlockInfos(world, pos1, pos2, placementsettings,
					this.getDataMarkers(templateManager, pos1, rotation, false), template)) {
				this.handleDataMarker(world, info, pos1, rotation, random, box);
			}

			return true;
		}
	}

	protected PlacementSettings getSettings(Supplier<StructureProcessorList> extra, Rotation p_230379_1_, MutableBoundingBox p_230379_2_, boolean p_230379_3_) {
		PlacementSettings placementsettings = new PlacementSettings();
		placementsettings.setBoundingBox(p_230379_2_);
		placementsettings.setRotation(p_230379_1_);
		placementsettings.setKnownShape(true);
		placementsettings.setIgnoreEntities(false);
		placementsettings.addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
		placementsettings.setFinalizeEntities(true);
		if (!p_230379_3_) {
			placementsettings.addProcessor(JigsawReplacementStructureProcessor.INSTANCE);
		}

		this.processors.get().list().forEach(placementsettings::addProcessor);
		if(extra != null)extra.get().list().forEach(placementsettings::addProcessor);
		this.getProjection().getProcessors().forEach(placementsettings::addProcessor);
		return placementsettings;
	}

	@Override
	public IJigsawDeserializer<?> getType() {
		return ModStructures.PoolElements.PALETTED_SINGLE_POOL_ELEMENT;
	}

	@Override
	public String toString() {
		return "PalettedSingle[" + this.template + "]";
	}

}

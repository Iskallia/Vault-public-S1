package iskallia.vault.world.gen.structure.pool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import iskallia.vault.init.ModStructures;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("NullableProblems")
public class PalettedListPoolElement extends JigsawPiece {

	public static final Codec<PalettedListPoolElement> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(JigsawPiece.field_236847_e_.listOf().fieldOf("elements").forGetter(piece -> {
			return piece.elements;
		}), projection(), processors()).apply(instance, PalettedListPoolElement::new);
	});

	private final List<JigsawPiece> elements;
	protected final List<Supplier<StructureProcessorList>> processors;

	public PalettedListPoolElement(List<JigsawPiece> elements, JigsawPattern.PlacementBehaviour behaviour,  List<Supplier<StructureProcessorList>> processors) {
		super(behaviour);

		if(elements.isEmpty()) {
			throw new IllegalArgumentException("Elements are empty");
		} else {
			this.elements = elements;
			this.processors = processors;
			this.setProjectionOnEachElement(behaviour);
		}
	}

	protected static <E extends JigsawPiece> RecordCodecBuilder<E, JigsawPattern.PlacementBehaviour> projection() {
		return JigsawPattern.PlacementBehaviour.field_236858_c_.fieldOf("projection").forGetter(JigsawPiece::getPlacementBehaviour);
	}

	protected static <E extends PalettedListPoolElement> RecordCodecBuilder<E, List<Supplier<StructureProcessorList>>> processors() {
		return IStructureProcessorType.field_242922_m.listOf().fieldOf("processors").forGetter(piece -> {
			return piece.processors;
		});
	}

	@Override
	public List<Template.BlockInfo> getJigsawBlocks(TemplateManager templateManager, BlockPos pos, Rotation rotation, Random random) {
		return this.elements.get(0).getJigsawBlocks(templateManager, pos, rotation, random);
	}

	@Override
	public MutableBoundingBox getBoundingBox(TemplateManager templateManager, BlockPos pos, Rotation rotation) {
		MutableBoundingBox mutableboundingbox = MutableBoundingBox.getNewBoundingBox();
		this.elements.stream().map(piece -> piece.getBoundingBox(templateManager, pos, rotation)).forEach(mutableboundingbox::expandTo);
		return mutableboundingbox;
	}

	@Override
	public boolean func_230378_a_(TemplateManager templateManager, ISeedReader world, StructureManager structureManager,
	                              ChunkGenerator chunkGen, BlockPos pos1, BlockPos pos2, Rotation rotation,
	                              MutableBoundingBox box, Random random, boolean keepJigsaws) {
		Supplier<StructureProcessorList> extra = this.processors.isEmpty() ? null : this.processors.get(random.nextInt(this.processors.size()));

		for(JigsawPiece piece: this.elements) {
			if(piece instanceof PalettedSinglePoolElement) {
				if(!((PalettedSinglePoolElement)piece).generate(extra, templateManager, world, structureManager,
						chunkGen, pos1, pos2, rotation, box, random, keepJigsaws)) {
					return false;
				}
			} else {
				if(!piece.func_230378_a_(templateManager, world, structureManager, chunkGen, pos1, pos2, rotation, box,
						random, keepJigsaws)) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public IJigsawDeserializer<?> getType() {
		return ModStructures.PoolElements.PALETTED_LIST_POOL_ELEMENT;
	}

	@Override
	public JigsawPiece setPlacementBehaviour(JigsawPattern.PlacementBehaviour placementBehaviour) {
		super.setPlacementBehaviour(placementBehaviour);
		this.setProjectionOnEachElement(placementBehaviour);
		return this;
	}

	@Override
	public String toString() {
		return "PalettedList[" + this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
	}

	private void setProjectionOnEachElement(JigsawPattern.PlacementBehaviour p_214864_1_) {
		this.elements.forEach((p_214863_1_) -> {
			p_214863_1_.setPlacementBehaviour(p_214864_1_);
		});
	}

}

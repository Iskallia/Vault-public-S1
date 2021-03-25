package iskallia.vault.world.gen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import iskallia.vault.Vault;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.block.JigsawBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.jigsaw.*;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.*;

public class JigsawGenerator {

	public static void addPieces(DynamicRegistries p_242837_0_, VillageConfig p_242837_1_, JigsawManager.IPieceFactory p_242837_2_, ChunkGenerator p_242837_3_, TemplateManager p_242837_4_, BlockPos p_242837_5_, List<? super AbstractVillagePiece> p_242837_6_, Random p_242837_7_, boolean p_242837_8_, boolean p_242837_9_) {
		Structure.bootstrap();
		MutableRegistry<JigsawPattern> mutableregistry = p_242837_0_.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
		Rotation rotation = Rotation.getRandom(p_242837_7_);
		JigsawPattern jigsawpattern = p_242837_1_.startPool().get();
		JigsawPiece jigsawpiece = jigsawpattern.getRandomTemplate(p_242837_7_);
		AbstractVillagePiece abstractvillagepiece = p_242837_2_.create(p_242837_4_, jigsawpiece, p_242837_5_, jigsawpiece.getGroundLevelDelta(), rotation, jigsawpiece.getBoundingBox(p_242837_4_, p_242837_5_, rotation));
		MutableBoundingBox mutableboundingbox = abstractvillagepiece.getBoundingBox();
		int i = (mutableboundingbox.x1 + mutableboundingbox.x0) / 2;
		int j = (mutableboundingbox.z1 + mutableboundingbox.z0) / 2;
		int k;
		if (p_242837_9_) {
			k = p_242837_5_.getY() + p_242837_3_.getFirstFreeHeight(i, j, Heightmap.Type.WORLD_SURFACE_WG);
		} else {
			k = p_242837_5_.getY();
		}

		int l = mutableboundingbox.y0 + abstractvillagepiece.getGroundLevelDelta();
		abstractvillagepiece.move(0, k - l, 0);
		p_242837_6_.add(abstractvillagepiece);
		if (p_242837_1_.maxDepth() > 0) {
			int maxRange = VaultRaid.REGION_SIZE / 2;

			AxisAlignedBB axisalignedbb = new AxisAlignedBB(i - maxRange, k - maxRange, j - maxRange,
					i + maxRange + 1, k + maxRange + 1, j + maxRange + 1);
			Assembler jigsawmanager$assembler = new Assembler(mutableregistry, p_242837_1_.maxDepth(), p_242837_2_, p_242837_3_, p_242837_4_, p_242837_6_, p_242837_7_);
			jigsawmanager$assembler.availablePieces.addLast(new Entry(abstractvillagepiece, new MutableObject<>(VoxelShapes.join(VoxelShapes.create(axisalignedbb), VoxelShapes.create(AxisAlignedBB.of(mutableboundingbox)), IBooleanFunction.ONLY_FIRST)), k + maxRange, 0));

			while(!jigsawmanager$assembler.availablePieces.isEmpty()) {
				Entry jigsawmanager$entry = jigsawmanager$assembler.availablePieces.removeFirst();
				jigsawmanager$assembler.tryPlacingChildren(jigsawmanager$entry.villagePiece, jigsawmanager$entry.free, jigsawmanager$entry.boundsTop, jigsawmanager$entry.depth, p_242837_8_);
			}

		}
	}

	static final class Assembler {
		private final Registry<JigsawPattern> pools;
		private final int maxDepth;
		private final JigsawManager.IPieceFactory pieceFactory;
		private final ChunkGenerator chunkGenerator;
		private final TemplateManager templateManager;
		private final List<? super AbstractVillagePiece> structurePieces;
		private final Random rand;
		private final Deque<Entry> availablePieces = Queues.newArrayDeque();

		private Assembler(Registry<JigsawPattern> p_i242005_1_, int p_i242005_2_, JigsawManager.IPieceFactory p_i242005_3_, ChunkGenerator p_i242005_4_, TemplateManager p_i242005_5_, List<? super AbstractVillagePiece> p_i242005_6_, Random p_i242005_7_) {
			this.pools = p_i242005_1_;
			this.maxDepth = p_i242005_2_;
			this.pieceFactory = p_i242005_3_;
			this.chunkGenerator = p_i242005_4_;
			this.templateManager = p_i242005_5_;
			this.structurePieces = p_i242005_6_;
			this.rand = p_i242005_7_;
		}

		private void tryPlacingChildren(AbstractVillagePiece p_236831_1_, MutableObject<VoxelShape> p_236831_2_, int p_236831_3_, int currentDepth, boolean p_236831_5_) {
			JigsawPiece jigsawpiece = p_236831_1_.getElement();
			BlockPos blockpos = p_236831_1_.getPosition();
			Rotation rotation = p_236831_1_.getRotation();
			JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour = jigsawpiece.getProjection();
			boolean flag = jigsawpattern$placementbehaviour == JigsawPattern.PlacementBehaviour.RIGID;
			MutableObject<VoxelShape> mutableobject = new MutableObject<>();
			MutableBoundingBox mutableboundingbox = p_236831_1_.getBoundingBox();
			int i = mutableboundingbox.y0;

			label139:
			for(Template.BlockInfo template$blockinfo : jigsawpiece.getShuffledJigsawBlocks(this.templateManager, blockpos, rotation, this.rand)) {
				Direction direction = JigsawBlock.getFrontFacing(template$blockinfo.state);
				BlockPos blockpos1 = template$blockinfo.pos;
				BlockPos blockpos2 = blockpos1.relative(direction);
				int j = blockpos1.getY() - i;
				int k = -1;
				ResourceLocation resourcelocation = new ResourceLocation(template$blockinfo.nbt.getString("pool"));
				Optional<JigsawPattern> mainJigsawPattern = this.pools.getOptional(resourcelocation);
				if (mainJigsawPattern.isPresent() && (mainJigsawPattern.get().size() != 0 || Objects.equals(resourcelocation, JigsawPatternRegistry.EMPTY.location()))) {
					ResourceLocation resourcelocation1 = mainJigsawPattern.get().getFallback();
					Optional<JigsawPattern> fallbackJigsawPattern = this.pools.getOptional(resourcelocation1);
					if (fallbackJigsawPattern.isPresent() && (fallbackJigsawPattern.get().size() != 0 || Objects.equals(resourcelocation1, JigsawPatternRegistry.EMPTY.location()))) {
						boolean flag1 = mutableboundingbox.isInside(blockpos2);
						MutableObject<VoxelShape> mutableobject1;
						int l;
						if (flag1) {
							mutableobject1 = mutableobject;
							l = i;
							if (mutableobject.getValue() == null) {
								mutableobject.setValue(VoxelShapes.create(AxisAlignedBB.of(mutableboundingbox)));
							}
						} else {
							mutableobject1 = p_236831_2_;
							l = p_236831_3_;
						}

						List<JigsawPiece> list = Lists.newArrayList();

						if(currentDepth != this.maxDepth) {
							list.addAll(mainJigsawPattern.get().getShuffledTemplates(this.rand));
							list.addAll(fallbackJigsawPattern.get().getShuffledTemplates(this.rand));
						} else {
							list.addAll(fallbackJigsawPattern.get().getShuffledTemplates(this.rand));
						}

						for(JigsawPiece jigsawpiece1 : list) {
							if (jigsawpiece1 == EmptyJigsawPiece.INSTANCE) {
								break;
							}

							for(Rotation rotation1 : Rotation.getShuffled(this.rand)) {
								List<Template.BlockInfo> list1 = jigsawpiece1.getShuffledJigsawBlocks(this.templateManager, BlockPos.ZERO, rotation1, this.rand);
								MutableBoundingBox mutableboundingbox1 = jigsawpiece1.getBoundingBox(this.templateManager, BlockPos.ZERO, rotation1);
								int i1;
								if (p_236831_5_ && mutableboundingbox1.getYSpan() <= 16) {
									i1 = list1.stream().mapToInt((p_242841_2_) -> {
										if (!mutableboundingbox1.isInside(p_242841_2_.pos.relative(JigsawBlock.getFrontFacing(p_242841_2_.state)))) {
											return 0;
										} else {
											ResourceLocation resourcelocation2 = new ResourceLocation(p_242841_2_.nbt.getString("pool"));
											Optional<JigsawPattern> optional2 = this.pools.getOptional(resourcelocation2);
											Optional<JigsawPattern> optional3 = optional2.flatMap((p_242843_1_) -> {
												return this.pools.getOptional(p_242843_1_.getFallback());
											});
											int k3 = optional2.map((p_242842_1_) -> {
												return p_242842_1_.getMaxSize(this.templateManager);
											}).orElse(0);
											int l3 = optional3.map((p_242840_1_) -> {
												return p_242840_1_.getMaxSize(this.templateManager);
											}).orElse(0);
											return Math.max(k3, l3);
										}
									}).max().orElse(0);
								} else {
									i1 = 0;
								}

								for(Template.BlockInfo template$blockinfo1 : list1) {
									if (JigsawBlock.canAttach(template$blockinfo, template$blockinfo1)) {
										BlockPos blockpos3 = template$blockinfo1.pos;
										BlockPos blockpos4 = new BlockPos(blockpos2.getX() - blockpos3.getX(), blockpos2.getY() - blockpos3.getY(), blockpos2.getZ() - blockpos3.getZ());
										MutableBoundingBox mutableboundingbox2 = jigsawpiece1.getBoundingBox(this.templateManager, blockpos4, rotation1);
										int j1 = mutableboundingbox2.y0;
										JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour1 = jigsawpiece1.getProjection();
										boolean flag2 = jigsawpattern$placementbehaviour1 == JigsawPattern.PlacementBehaviour.RIGID;
										int k1 = blockpos3.getY();
										int l1 = j - k1 + JigsawBlock.getFrontFacing(template$blockinfo.state).getStepY();
										int i2;
										if (flag && flag2) {
											i2 = i + l1;
										} else {
											if (k == -1) {
												k = this.chunkGenerator.getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
											}

											i2 = k - k1;
										}

										int j2 = i2 - j1;
										MutableBoundingBox mutableboundingbox3 = mutableboundingbox2.moved(0, j2, 0);
										BlockPos blockpos5 = blockpos4.offset(0, j2, 0);
										if (i1 > 0) {
											int k2 = Math.max(i1 + 1, mutableboundingbox3.y1 - mutableboundingbox3.y0);
											mutableboundingbox3.y1 = mutableboundingbox3.y0 + k2;
										}

										if (!VoxelShapes.joinIsNotEmpty(mutableobject1.getValue(), VoxelShapes.create(AxisAlignedBB.of(mutableboundingbox3).deflate(0.25D)), IBooleanFunction.ONLY_SECOND)) {
											mutableobject1.setValue(VoxelShapes.joinUnoptimized(mutableobject1.getValue(), VoxelShapes.create(AxisAlignedBB.of(mutableboundingbox3)), IBooleanFunction.ONLY_FIRST));
											int j3 = p_236831_1_.getGroundLevelDelta();
											int l2;
											if (flag2) {
												l2 = j3 - l1;
											} else {
												l2 = jigsawpiece1.getGroundLevelDelta();
											}

											AbstractVillagePiece abstractvillagepiece = this.pieceFactory.create(this.templateManager, jigsawpiece1, blockpos5, l2, rotation1, mutableboundingbox3);
											int i3;
											if (flag) {
												i3 = i + j;
											} else if (flag2) {
												i3 = i2 + k1;
											} else {
												if (k == -1) {
													k = this.chunkGenerator.getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
												}

												i3 = k + l1 / 2;
											}

											p_236831_1_.addJunction(new JigsawJunction(blockpos2.getX(), i3 - j + j3, blockpos2.getZ(), l1, jigsawpattern$placementbehaviour1));
											abstractvillagepiece.addJunction(new JigsawJunction(blockpos1.getX(), i3 - k1 + l2, blockpos1.getZ(), -l1, jigsawpattern$placementbehaviour));

											if(abstractvillagepiece.getBoundingBox().y0 > 0 && abstractvillagepiece.getBoundingBox().y1 < 256) {
												this.structurePieces.add(abstractvillagepiece);

												if(currentDepth + 1 <= this.maxDepth) {
													this.availablePieces.addLast(new Entry(abstractvillagepiece, mutableobject1, l, currentDepth + 1));
												}
											}

											continue label139;
										}
									}
								}
							}
						}
					} else {
						Vault.LOGGER.warn("Empty or none existent fallback pool: {}", (Object)resourcelocation1);
					}
				} else {
					Vault.LOGGER.warn("Empty or none existent pool: {}", (Object)resourcelocation);
				}
			}

		}
	}

	static final class Entry {
		private final AbstractVillagePiece villagePiece;
		private final MutableObject<VoxelShape> free;
		private final int boundsTop;
		private final int depth;

		private Entry(AbstractVillagePiece p_i232042_1_, MutableObject<VoxelShape> p_i232042_2_, int p_i232042_3_, int p_i232042_4_) {
			this.villagePiece = p_i232042_1_;
			this.free = p_i232042_2_;
			this.boundsTop = p_i232042_3_;
			this.depth = p_i232042_4_;
		}
	}

}

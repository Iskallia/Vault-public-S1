package iskallia.vault.block;

import java.util.Locale;

import javax.annotation.Nullable;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.ItemRelicBoosterPack;
import iskallia.vault.item.PuzzleRuneItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class PuzzleRuneBlock extends Block {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Color> COLOR = EnumProperty.create("color", Color.class);
	public static final BooleanProperty RUNE_PLACED = BooleanProperty.create("rune_placed");

	public PuzzleRuneBlock() {
		super(Properties.of(Material.STONE, MaterialColor.STONE)
				.strength(-1.0F, 3600000.0F)
				.noOcclusion()
				.noDrops());

		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.SOUTH)
				.setValue(COLOR, Color.YELLOW)
				.setValue(RUNE_PLACED, false));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection())
				.setValue(COLOR, ModAttributes.PUZZLE_COLOR.getOrDefault(context.getItemInHand(), Color.YELLOW).getValue(context.getItemInHand()))
				.setValue(RUNE_PLACED, false);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING).add(COLOR).add(RUNE_PLACED);
	}


	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if(!world.isClientSide) {
			ItemStack heldStack = player.getItemInHand(hand);

			if(heldStack.getItem() instanceof PuzzleRuneItem) {
				if(isValidKey(heldStack, state)) {
					heldStack.shrink(1);
					BlockState blockState = world.getBlockState(pos);
					world.setBlock(pos, blockState.setValue(RUNE_PLACED, true), 3);
					world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1f, 1f);
				}
			}
		}

		return super.use(state, world, pos, player, hand, hit);
	}

	@Override
	public int getSignal(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return state.getValue(RUNE_PLACED) ? 15 : 0;
	}

	private boolean isValidKey(ItemStack stack, BlockState state) {
		if(state.getValue(RUNE_PLACED))return false;

		return ModAttributes.PUZZLE_COLOR.get(stack)
				.map(attribute -> attribute.getValue(stack))
				.filter(value -> value == state.getValue(COLOR))
				.isPresent();
	}

	public enum Color implements IStringSerializable {
		YELLOW, PINK, GREEN, BLUE;

		public Color next() {
			return values()[(this.ordinal() + 1) % values().length];
		}

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}
	}

	public static class Item extends BlockItem {
		public Item(Block block, Properties properties) {
			super(block, properties);
		}

		@Override
		public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
			PlayerEntity player = context.getPlayer();
			World world = context.getLevel();

			if(player != null && player.isCreative() && !world.isClientSide
					&& world.getBlockState(context.getClickedPos()).getBlockState().getBlock() == ModBlocks.PUZZLE_RUNE_BLOCK) {
				ModAttributes.PUZZLE_COLOR.create(stack,
						ModAttributes.PUZZLE_COLOR.getOrCreate(stack, PuzzleRuneBlock.Color.YELLOW).getValue(stack).next());
				ItemRelicBoosterPack.successEffects(world, player.position());
				return ActionResultType.SUCCESS;
			}

			return super.onItemUseFirst(stack, context);
		}
	}

}

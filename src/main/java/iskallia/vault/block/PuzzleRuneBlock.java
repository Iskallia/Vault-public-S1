package iskallia.vault.block;

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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Locale;

public class PuzzleRuneBlock extends Block {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Color> COLOR = EnumProperty.create("color", Color.class);
	public static final BooleanProperty RUNE_PLACED = BooleanProperty.create("rune_placed");

	public PuzzleRuneBlock() {
		super(Properties.create(Material.ROCK, MaterialColor.STONE)
				.hardnessAndResistance(-1.0F, 3600000.0F)
				.notSolid()
				.noDrops());

		this.setDefaultState(this.stateContainer.getBaseState()
				.with(FACING, Direction.SOUTH)
				.with(COLOR, Color.YELLOW)
				.with(RUNE_PLACED, false));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState()
				.with(FACING, context.getPlacementHorizontalFacing())
				.with(COLOR, ModAttributes.PUZZLE_COLOR.getOrDefault(context.getItem(), Color.YELLOW).getValue(context.getItem()))
				.with(RUNE_PLACED, false);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING).add(COLOR).add(RUNE_PLACED);
	}


	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if(!world.isRemote) {
			ItemStack heldStack = player.getHeldItem(hand);

			if(heldStack.getItem() instanceof PuzzleRuneItem) {
				if(isValidKey(heldStack, state)) {
					heldStack.shrink(1);
					BlockState blockState = world.getBlockState(pos);
					world.setBlockState(pos, blockState.with(RUNE_PLACED, true), 3);
					world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1f, 1f);
				}
			}
		}

		return super.onBlockActivated(state, world, pos, player, hand, hit);
	}

	@Override
	public int getWeakPower(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return state.get(RUNE_PLACED) ? 15 : 0;
	}

	private boolean isValidKey(ItemStack stack, BlockState state) {
		if(state.get(RUNE_PLACED))return false;

		return ModAttributes.PUZZLE_COLOR.get(stack)
				.map(attribute -> attribute.getValue(stack))
				.filter(value -> value == state.get(COLOR))
				.isPresent();
	}

	public enum Color implements IStringSerializable {
		YELLOW, PINK, GREEN, BLUE;

		public Color next() {
			return values()[(this.ordinal() + 1) % values().length];
		}

		@Override
		public String getString() {
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
			World world = context.getWorld();

			if(player != null && player.isCreative() && !world.isRemote
					&& world.getBlockState(context.getPos()).getBlockState().getBlock() == ModBlocks.PUZZLE_RUNE_BLOCK) {
				ModAttributes.PUZZLE_COLOR.create(stack,
						ModAttributes.PUZZLE_COLOR.getOrCreate(stack, PuzzleRuneBlock.Color.YELLOW).getValue(stack).next());
				ItemRelicBoosterPack.successEffects(world, player.getPositionVec());
				return ActionResultType.SUCCESS;
			}

			return super.onItemUseFirst(stack, context);
		}
	}

}

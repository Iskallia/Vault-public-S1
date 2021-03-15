package iskallia.vault.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

@FunctionalInterface
public interface BlockPlacer {

	BlockState getState(BlockPos pos, Random random, Direction facing);

}

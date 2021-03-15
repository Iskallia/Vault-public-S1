package iskallia.vault.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class PortalPlacer {

    private final BlockPlacer portalPlacer;
    private final BlockPlacer framePlacer;

    public PortalPlacer(BlockPlacer portal, BlockPlacer frame) {
        this.portalPlacer = portal;
        this.framePlacer = frame;
    }

    public void place(IWorld world, BlockPos pos, Direction facing, int width, int height) {
        pos = pos.offset(Direction.DOWN).offset(facing.getOpposite());

        for (int y = 0; y < height + 2; y++) {
            this.place(world, pos.up(y), facing, this.framePlacer);
            this.place(world, pos.offset(facing, width + 1).up(y), facing, this.framePlacer);

            for (int x = 1; x < width + 1; x++) {
                this.place(world, pos.offset(facing, x).up(y), facing, y == 0 || y == height + 1 ? this.framePlacer : this.portalPlacer);
            }
        }
    }

    protected void place(IWorld world, BlockPos pos, BlockState state) {
        if(state != null) {
            world.setBlockState(pos, state, 1);
        }
    }

    protected void place(IWorld world, BlockPos pos, Direction direction, BlockPlacer provider) {
        this.place(world, pos, provider.getState(pos, world.getRandom(), direction));
    }

}

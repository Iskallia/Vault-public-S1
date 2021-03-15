package iskallia.vault.block;

import iskallia.vault.block.entity.VaultPortalTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.CrystalData;
import iskallia.vault.item.ItemVaultCrystal;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public class VaultPortalSize {

    private static final AbstractBlock.IPositionPredicate POSITION_PREDICATE = (state, blockReader, pos) -> {
        return Arrays.stream(ModConfigs.VAULT_PORTAL.getValidFrameBlocks()).anyMatch(b -> b == state.getBlock());
    };

    private final IWorld world;
    private final Direction.Axis axis;
    private final Direction rightDir;
    private int portalBlockCount;
    @Nullable
    private BlockPos bottomLeft;
    private int height;
    private int width;

    public VaultPortalSize(IWorld worldIn, BlockPos pos, Direction.Axis axisIn) {
        this.world = worldIn;
        this.axis = axisIn;
        this.rightDir = axisIn == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        this.bottomLeft = this.getBottomLeft(pos);
        if (this.bottomLeft == null) {
            this.bottomLeft = pos;
            this.width = 1;
            this.height = 1;
        } else {
            this.width = this.getWidth();
            if (this.width > 0) {
                this.height = this.getHeight();
            }
        }

    }

    public static Optional<VaultPortalSize> getPortalSize(IWorld world, BlockPos pos, Direction.Axis axis) {
        return getPortalSize(world, pos, (size) -> {
            return size.isValid() && size.portalBlockCount == 0;
        }, axis);
    }

    public static Optional<VaultPortalSize> getPortalSize(IWorld world, BlockPos pos, Predicate<VaultPortalSize> sizePredicate, Direction.Axis axis) {
        Optional<VaultPortalSize> optional = Optional.of(new VaultPortalSize(world, pos, axis)).filter(sizePredicate);
        if (optional.isPresent()) {
            return optional;
        } else {
            Direction.Axis direction$axis = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            return Optional.of(new VaultPortalSize(world, pos, direction$axis)).filter(sizePredicate);
        }
    }

    private static boolean canConnect(BlockState state) {
        return state.isAir() || state.isIn(ModBlocks.VAULT_PORTAL);
    }

    @Nullable
    private BlockPos getBottomLeft(BlockPos pos) {
        for (int i = Math.max(0, pos.getY() - 21); pos.getY() > i && canConnect(this.world.getBlockState(pos.down())); pos = pos.down()) {
        }

        Direction direction = this.rightDir.getOpposite();
        int j = this.getWidth(pos, direction) - 1;
        return j < 0 ? null : pos.offset(direction, j);
    }

    private int getWidth() {
        int i = this.getWidth(this.bottomLeft, this.rightDir);
        return i >= 2 && i <= 21 ? i : 0;
    }

    private int getWidth(BlockPos pos, Direction direction) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int i = 0; i <= 21; ++i) {
            blockpos$mutable.setPos(pos).move(direction, i);
            BlockState blockstate = this.world.getBlockState(blockpos$mutable);
            if (!canConnect(blockstate)) {
                if (POSITION_PREDICATE.test(blockstate, this.world, blockpos$mutable)) {
                    return i;
                }
                break;
            }

            BlockState blockstate1 = this.world.getBlockState(blockpos$mutable.move(Direction.DOWN));
            if (!POSITION_PREDICATE.test(blockstate1, this.world, blockpos$mutable)) {
                break;
            }
        }

        return 0;
    }

    private int getHeight() {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int i = this.getFrameColumnCount(blockpos$mutable);
        return i >= 3 && i <= 21 && this.getHeight(blockpos$mutable, i) ? i : 0;
    }

    private boolean getHeight(BlockPos.Mutable mutablePos, int upDisplacement) {
        for (int i = 0; i < this.width; ++i) {
            BlockPos.Mutable blockpos$mutable = mutablePos.setPos(this.bottomLeft).move(Direction.UP, upDisplacement).move(this.rightDir, i);
            if (!POSITION_PREDICATE.test(this.world.getBlockState(blockpos$mutable), this.world, blockpos$mutable)) {
                return false;
            }
        }

        return true;
    }

    private int getFrameColumnCount(BlockPos.Mutable mutablePos) {
        for (int i = 0; i < 21; ++i) {
            mutablePos.setPos(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);
            if (!POSITION_PREDICATE.test(this.world.getBlockState(mutablePos), this.world, mutablePos)) {
                return i;
            }

            mutablePos.setPos(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
            if (!POSITION_PREDICATE.test(this.world.getBlockState(mutablePos), this.world, mutablePos)) {
                return i;
            }

            for (int j = 0; j < this.width; ++j) {
                mutablePos.setPos(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
                BlockState blockstate = this.world.getBlockState(mutablePos);
                if (!canConnect(blockstate)) {
                    return i;
                }

                if (blockstate.isIn(ModBlocks.VAULT_PORTAL)) {
                    ++this.portalBlockCount;
                }
            }
        }

        return 21;
    }

    public boolean isValid() {
        return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
    }

    public void placePortalBlocks(ItemVaultCrystal item, String playerBossName, CrystalData data) {
        BlockState blockstate = ModBlocks.VAULT_PORTAL.getDefaultState().with(VaultPortalBlock.AXIS, this.axis).with(VaultPortalBlock.RARITY, item.getRarity().ordinal());

        BlockPos.getAllInBoxMutable(this.bottomLeft, this.bottomLeft.offset(Direction.UP, this.height - 1).offset(this.rightDir, this.width - 1)).forEach((pos) -> {
            this.world.setBlockState(pos, blockstate, 3);
            TileEntity te = this.world.getTileEntity(pos);
            if(!(te instanceof VaultPortalTileEntity))return;
            VaultPortalTileEntity portal = (VaultPortalTileEntity)this.world.getTileEntity(pos);
            portal.setPlayerBossName(playerBossName);
            portal.setCrystalData(data);
        });
    }

    public boolean validatePortal() {
        return this.isValid() && this.portalBlockCount == this.width * this.height;
    }

}

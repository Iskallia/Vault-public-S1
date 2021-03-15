package iskallia.vault.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class MazeBlock extends Block {


    public static final EnumProperty<MazeColor> COLOR = EnumProperty.create("color", MazeColor.class);

    public MazeBlock() {
        super(Properties.create(Material.IRON, MaterialColor.IRON)
                .hardnessAndResistance(-1, 3600000.0F)
                .sound(SoundType.METAL));

        this.setDefaultState(this.stateContainer.getBaseState()
                .with(COLOR, MazeColor.RED));

    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(COLOR);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context);
    }

    //RED = 0, BLUE = 1
    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.isRemote) return;
        if (!(entityIn instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) entityIn;
        Scoreboard scoreboard = worldIn.getScoreboard();

        if (scoreboard.getObjective("Color") == null)
            scoreboard.addObjective("Color", ScoreCriteria.DUMMY, new StringTextComponent("Color"), ScoreCriteria.RenderType.INTEGER);

        ScoreObjective colorObjective = scoreboard.getObjective("Color");
        assert colorObjective != null;

        // DEBUG: worldIn.getScoreboard().setObjectiveInDisplaySlot(Scoreboard.getObjectiveDisplaySlotNumber("sidebar"), colorObjective);
        Score colorScore = worldIn.getScoreboard().getOrCreateScore(player.getDisplayName().getString(), colorObjective);
        MazeColor playerColor = MazeColor.values()[colorScore.getScorePoints()];

        BlockPos nextPosition = player.getPosition();
        if (playerColor == worldIn.getBlockState(pos).get(MazeBlock.COLOR)) {
            nextPosition = nextPosition.offset(player.getHorizontalFacing().getOpposite(), 1);
        } else {
            nextPosition = nextPosition.offset(player.getHorizontalFacing(), 1);
            colorScore.setScorePoints(playerColor == MazeColor.RED ? MazeColor.BLUE.ordinal() : MazeColor.RED.ordinal());
        }

        player.setPositionAndUpdate(nextPosition.getX() + 0.5d, nextPosition.getY(), nextPosition.getZ() + 0.5d);


        super.onEntityWalk(worldIn, pos, entityIn);
    }

    public enum MazeColor implements IStringSerializable {
        RED("red"),
        BLUE("blue");

        private String name;

        MazeColor(String name) {
            this.name = name;
        }

        @Override
        public String getString() {
            return this.name;
        }
    }

}

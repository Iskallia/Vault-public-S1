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

import net.minecraft.block.AbstractBlock.Properties;

public class MazeBlock extends Block {


    public static final EnumProperty<MazeColor> COLOR = EnumProperty.create("color", MazeColor.class);

    public MazeBlock() {
        super(Properties.of(Material.METAL, MaterialColor.METAL)
                .strength(-1, 3600000.0F)
                .sound(SoundType.METAL));

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(COLOR, MazeColor.RED));

    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(COLOR);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context);
    }

    //RED = 0, BLUE = 1
    @Override
    public void stepOn(World worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.isClientSide) return;
        if (!(entityIn instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) entityIn;
        Scoreboard scoreboard = worldIn.getScoreboard();

        if (scoreboard.getObjective("Color") == null)
            scoreboard.addObjective("Color", ScoreCriteria.DUMMY, new StringTextComponent("Color"), ScoreCriteria.RenderType.INTEGER);

        ScoreObjective colorObjective = scoreboard.getObjective("Color");
        assert colorObjective != null;

        // DEBUG: worldIn.getScoreboard().setObjectiveInDisplaySlot(Scoreboard.getObjectiveDisplaySlotNumber("sidebar"), colorObjective);
        Score colorScore = worldIn.getScoreboard().getOrCreatePlayerScore(player.getDisplayName().getString(), colorObjective);
        MazeColor playerColor = MazeColor.values()[colorScore.getScore()];

        BlockPos nextPosition = player.blockPosition();
        if (playerColor == worldIn.getBlockState(pos).getValue(MazeBlock.COLOR)) {
            nextPosition = nextPosition.relative(player.getDirection().getOpposite(), 1);
        } else {
            nextPosition = nextPosition.relative(player.getDirection(), 1);
            colorScore.setScore(playerColor == MazeColor.RED ? MazeColor.BLUE.ordinal() : MazeColor.RED.ordinal());
        }

        player.teleportTo(nextPosition.getX() + 0.5d, nextPosition.getY(), nextPosition.getZ() + 0.5d);


        super.stepOn(worldIn, pos, entityIn);
    }

    public enum MazeColor implements IStringSerializable {
        RED("red"),
        BLUE("blue");

        private String name;

        MazeColor(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

}

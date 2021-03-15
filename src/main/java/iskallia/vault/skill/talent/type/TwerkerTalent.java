package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import net.minecraft.block.Block;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class TwerkerTalent extends PlayerTalent {

    @Expose private final int tickDelay = 5;
    @Expose private final int xRange = 2;
    @Expose private final int yRange = 1;
    @Expose private final int zRange = 2;

    public TwerkerTalent(int cost) {
        super(cost);
    }

    public int getTickDelay() {
        return this.tickDelay;
    }

    public int getXRange() {
        return this.xRange;
    }

    public int getYRange() {
        return this.yRange;
    }

    public int getZRange() {
        return this.zRange;
    }

    @Override
    public void tick(PlayerEntity player) {
        if (player.isCrouching()) {
            BlockPos playerPos = player.getPosition();

            BlockPos pos = new BlockPos(
                    playerPos.getX() + player.getRNG().nextInt(this.getXRange() * 2 + 1) - this.getXRange(),
                    playerPos.getY() - player.getRNG().nextInt(this.getYRange() * 2 + 1) + this.getYRange(),
                    playerPos.getZ() + player.getRNG().nextInt(this.getZRange() * 2 + 1) - this.getZRange());

            Block block = player.world.getBlockState(pos).getBlock();

            if (block instanceof CropsBlock || block instanceof SaplingBlock) {
                BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), player.world, pos, player);
                ((ServerWorld) player.world).spawnParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX(), pos.getY(), pos.getZ(),
                        100, 1D, 0.5D, 1D, 0.0D);
            }
        }
    }

}
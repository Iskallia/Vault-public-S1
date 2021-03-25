package iskallia.vault.entity.ai;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.*;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Supplier;

public class FollowEntityGoal<T extends MobEntity, O extends LivingEntity> extends GoalTask<T> {

	private O owner;
	private final double followSpeed;
	private final PathNavigator navigator;
	private int timeToRecalcPath;
	private final float maxDist;
	private final float minDist;
	private float oldWaterCost;
	private final boolean teleportToLeaves;
	private final Supplier<Optional<O>> ownerSupplier;

	public FollowEntityGoal(T entity, double speed, float minDist, float maxDist, boolean teleportToLeaves, Supplier<Optional<O>> ownerSupplier) {
		super(entity);
		this.followSpeed = speed;
		this.navigator = entity.getNavigation();
		this.minDist = minDist;
		this.maxDist = maxDist;
		this.teleportToLeaves = teleportToLeaves;
		this.ownerSupplier = ownerSupplier;

		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));

		if(!(this.getEntity().getNavigation() instanceof GroundPathNavigator) && !(this.getEntity().getNavigation() instanceof FlyingPathNavigator)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	public boolean canUse() {
		O owner = this.ownerSupplier.get().orElse(null);

		if (owner == null) {
			return false;
		} else if(owner.isSpectator()) {
			return false;
		} else if(owner.distanceToSqr(this.getEntity()) < (double)(this.minDist * this.minDist)) {
			return false;
		} else {
			this.owner = owner;
			return true;
		}
	}

	public boolean canContinueToUse() {
		if(this.navigator.isDone()) {
			return false;
		}

		return this.getEntity().distanceToSqr(this.owner) > (double)(this.maxDist * this.maxDist);
	}

	public void start() {
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.getEntity().getPathfindingMalus(PathNodeType.WATER);
		this.getEntity().setPathfindingMalus(PathNodeType.WATER, 0.0F);
	}

	public void stop() {
		this.owner = null;
		this.navigator.stop();
		this.getEntity().setPathfindingMalus(PathNodeType.WATER, this.oldWaterCost);
	}

	public void tick() {
		this.getEntity().getLookControl().setLookAt(this.owner, 10.0F, (float)this.getEntity().getMaxHeadXRot());
		if(--this.timeToRecalcPath > 0)return;

		if(!this.getEntity().isLeashed() && !this.getEntity().isPassenger()) {
			if(this.getEntity().distanceToSqr(this.owner) >= 144.0D) {
				this.tryToTeleportNearEntity();
			} else {
				this.navigator.moveTo(this.owner, this.followSpeed);
			}
		}

		this.timeToRecalcPath = 10;
	}

	private void tryToTeleportNearEntity() {
		BlockPos blockpos = this.owner.blockPosition();

		for(int i = 0; i < 10; ++i) {
			int j = this.nextInt(-3, 3);
			int k = this.nextInt(-1, 1);
			int l = this.nextInt(-3, 3);
			boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
			if (flag) {
				return;
			}
		}
	}

	private boolean tryToTeleportToLocation(int x, int y, int z) {
		if (Math.abs((double)x - this.owner.getX()) < 2.0D && Math.abs((double)z - this.owner.getZ()) < 2.0D) {
			return false;
		} else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
			return false;
		} else {
			this.getEntity().moveTo((double)x + 0.5D, y, (double)z + 0.5D, this.getEntity().yRot, this.getEntity().xRot);
			this.navigator.stop();
			return true;
		}
	}

	private boolean isTeleportFriendlyBlock(BlockPos pos) {
		PathNodeType pathnodetype = WalkNodeProcessor.getBlockPathTypeStatic(this.getWorld(), pos.mutable());

		if(pathnodetype != PathNodeType.WALKABLE) {
			return false;
		} else {
			BlockState blockstate = this.getWorld().getBlockState(pos.below());
			if(!this.teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
				return false;
			} else {
				BlockPos blockpos = pos.subtract(this.getEntity().blockPosition());
				return this.getWorld().noCollision(this.getEntity(), this.getEntity().getBoundingBox().move(blockpos));
			}
		}
	}

	private int nextInt(int min, int max) {
		return this.getWorld().getRandom().nextInt(max - min + 1) + min;
	}

}

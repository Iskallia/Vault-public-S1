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
		this.navigator = entity.getNavigator();
		this.minDist = minDist;
		this.maxDist = maxDist;
		this.teleportToLeaves = teleportToLeaves;
		this.ownerSupplier = ownerSupplier;

		this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));

		if(!(this.getEntity().getNavigator() instanceof GroundPathNavigator) && !(this.getEntity().getNavigator() instanceof FlyingPathNavigator)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	public boolean shouldExecute() {
		O owner = this.ownerSupplier.get().orElse(null);

		if (owner == null) {
			return false;
		} else if(owner.isSpectator()) {
			return false;
		} else if(owner.getDistanceSq(this.getEntity()) < (double)(this.minDist * this.minDist)) {
			return false;
		} else {
			this.owner = owner;
			return true;
		}
	}

	public boolean shouldContinueExecuting() {
		if(this.navigator.noPath()) {
			return false;
		}

		return this.getEntity().getDistanceSq(this.owner) > (double)(this.maxDist * this.maxDist);
	}

	public void startExecuting() {
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.getEntity().getPathPriority(PathNodeType.WATER);
		this.getEntity().setPathPriority(PathNodeType.WATER, 0.0F);
	}

	public void resetTask() {
		this.owner = null;
		this.navigator.clearPath();
		this.getEntity().setPathPriority(PathNodeType.WATER, this.oldWaterCost);
	}

	public void tick() {
		this.getEntity().getLookController().setLookPositionWithEntity(this.owner, 10.0F, (float)this.getEntity().getVerticalFaceSpeed());
		if(--this.timeToRecalcPath > 0)return;

		if(!this.getEntity().getLeashed() && !this.getEntity().isPassenger()) {
			if(this.getEntity().getDistanceSq(this.owner) >= 144.0D) {
				this.tryToTeleportNearEntity();
			} else {
				this.navigator.tryMoveToEntityLiving(this.owner, this.followSpeed);
			}
		}

		this.timeToRecalcPath = 10;
	}

	private void tryToTeleportNearEntity() {
		BlockPos blockpos = this.owner.getPosition();

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
		if (Math.abs((double)x - this.owner.getPosX()) < 2.0D && Math.abs((double)z - this.owner.getPosZ()) < 2.0D) {
			return false;
		} else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
			return false;
		} else {
			this.getEntity().setLocationAndAngles((double)x + 0.5D, y, (double)z + 0.5D, this.getEntity().rotationYaw, this.getEntity().rotationPitch);
			this.navigator.clearPath();
			return true;
		}
	}

	private boolean isTeleportFriendlyBlock(BlockPos pos) {
		PathNodeType pathnodetype = WalkNodeProcessor.func_237231_a_(this.getWorld(), pos.toMutable());

		if(pathnodetype != PathNodeType.WALKABLE) {
			return false;
		} else {
			BlockState blockstate = this.getWorld().getBlockState(pos.down());
			if(!this.teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
				return false;
			} else {
				BlockPos blockpos = pos.subtract(this.getEntity().getPosition());
				return this.getWorld().hasNoCollisions(this.getEntity(), this.getEntity().getBoundingBox().offset(blockpos));
			}
		}
	}

	private int nextInt(int min, int max) {
		return this.getWorld().getRandom().nextInt(max - min + 1) + min;
	}

}

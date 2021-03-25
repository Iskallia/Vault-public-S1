package iskallia.vault.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

public class ThrowProjectilesGoal<T extends MobEntity> extends GoalTask<T> {

	private final int chance;
	private final int count;
	private final Projectile projectile;

	private ItemStack oldStack;
	private int progress;

	public ThrowProjectilesGoal(T entity, int chance, int count, Projectile projectile) {
		super(entity);
		this.chance = chance;
		this.count = count;
		this.projectile = projectile;
	}

	@Override
	public boolean canUse() {
		return this.getEntity().getTarget() != null && this.getWorld().random.nextInt(this.chance) == 0;
	}

	@Override
	public boolean canContinueToUse() {
		return this.getEntity().getTarget() != null && this.progress < this.count;
	}

	@Override
	public void start() {
		this.oldStack = this.getEntity().getItemBySlot(EquipmentSlotType.OFFHAND);
		this.getEntity().setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.SNOWBALL));
	}

	@Override
	public void tick() {
		if(this.getWorld().random.nextInt(3) == 0) {
			Entity throwEntity = this.projectile.create(this.getWorld(), this.getEntity());

			LivingEntity target = this.getEntity().getTarget();

			if(target != null) {
				double d0 = target.getEyeY() - (double) 1.1F;
				double d1 = target.getX() - this.getEntity().getX();
				double d2 = d0 - throwEntity.getY();
				double d3 = target.getZ() - this.getEntity().getZ();
				float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
				shoot(throwEntity, d1, d2 + (double) f, d3, 1.6F, 4.0F, this.getWorld().random);
				this.getWorld().playSound(null, this.getEntity().blockPosition(), SoundEvents.SNOW_GOLEM_SHOOT, SoundCategory.HOSTILE, 1.0F, 0.4F / (this.getWorld().random.nextFloat() * 0.4F + 0.8F));
				this.getWorld().addFreshEntity(throwEntity);
			}

			this.progress++;
		}
	}

	public void shoot(Entity projectile, double x, double y, double z, float velocity, float inaccuracy, Random rand) {
		Vector3d vector3d = (new Vector3d(x, y, z)).normalize().add(
				rand.nextGaussian() * (double)0.0075F * (double)inaccuracy,
				rand.nextGaussian() * (double)0.0075F * (double)inaccuracy,
				rand.nextGaussian() * (double)0.0075F * (double)inaccuracy).scale(velocity);

		projectile.setDeltaMovement(vector3d);
		float f = MathHelper.sqrt(Entity.getHorizontalDistanceSqr(vector3d));
		projectile.yRot = (float)(MathHelper.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI));
		projectile.xRot = (float)(MathHelper.atan2(vector3d.y, f) * (double)(180F / (float)Math.PI));
		projectile.yRotO = projectile.yRot;
		projectile.xRotO = projectile.xRot;
	}

	@Override
	public void stop() {
		this.getEntity().setItemSlot(EquipmentSlotType.OFFHAND, this.oldStack);
		this.oldStack = ItemStack.EMPTY;
		this.progress = 0;
	}

	public interface Projectile {
		Entity create(World world, LivingEntity shooter);
	}

}

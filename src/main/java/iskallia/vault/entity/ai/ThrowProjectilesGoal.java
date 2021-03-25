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
	public boolean shouldExecute() {
		return this.getEntity().getAttackTarget() != null && this.getWorld().rand.nextInt(this.chance) == 0;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.getEntity().getAttackTarget() != null && this.progress < this.count;
	}

	@Override
	public void startExecuting() {
		this.oldStack = this.getEntity().getItemStackFromSlot(EquipmentSlotType.OFFHAND);
		this.getEntity().setItemStackToSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.SNOWBALL));
	}

	@Override
	public void tick() {
		if(this.getWorld().rand.nextInt(3) == 0) {
			Entity throwEntity = this.projectile.create(this.getWorld(), this.getEntity());

			LivingEntity target = this.getEntity().getAttackTarget();

			if(target != null) {
				double d0 = target.getPosYEye() - (double) 1.1F;
				double d1 = target.getPosX() - this.getEntity().getPosX();
				double d2 = d0 - throwEntity.getPosY();
				double d3 = target.getPosZ() - this.getEntity().getPosZ();
				float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
				shoot(throwEntity, d1, d2 + (double) f, d3, 1.6F, 4.0F, this.getWorld().rand);
				this.getWorld().playSound(null, this.getEntity().getPosition(), SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, SoundCategory.HOSTILE, 1.0F, 0.4F / (this.getWorld().rand.nextFloat() * 0.4F + 0.8F));
				this.getWorld().addEntity(throwEntity);
			}

			this.progress++;
		}
	}

	public void shoot(Entity projectile, double x, double y, double z, float velocity, float inaccuracy, Random rand) {
		Vector3d vector3d = (new Vector3d(x, y, z)).normalize().add(
				rand.nextGaussian() * (double)0.0075F * (double)inaccuracy,
				rand.nextGaussian() * (double)0.0075F * (double)inaccuracy,
				rand.nextGaussian() * (double)0.0075F * (double)inaccuracy).scale(velocity);

		projectile.setMotion(vector3d);
		float f = MathHelper.sqrt(Entity.horizontalMag(vector3d));
		projectile.rotationYaw = (float)(MathHelper.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI));
		projectile.rotationPitch = (float)(MathHelper.atan2(vector3d.y, f) * (double)(180F / (float)Math.PI));
		projectile.prevRotationYaw = projectile.rotationYaw;
		projectile.prevRotationPitch = projectile.rotationPitch;
	}

	@Override
	public void resetTask() {
		this.getEntity().setItemStackToSlot(EquipmentSlotType.OFFHAND, this.oldStack);
		this.oldStack = ItemStack.EMPTY;
		this.progress = 0;
	}

	public interface Projectile {
		Entity create(World world, LivingEntity shooter);
	}

}

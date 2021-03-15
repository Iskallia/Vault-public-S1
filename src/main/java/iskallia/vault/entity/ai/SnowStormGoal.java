package iskallia.vault.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;

public class SnowStormGoal<T extends MobEntity> extends GoalTask<T> {

	private final int chance;
	private final int count;

	private ItemStack oldStack;
	private int progress;

	public SnowStormGoal(T entity, int chance, int count) {
		super(entity);
		this.chance = chance;
		this.count = count;
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
			SnowballEntity snowball = new SnowballEntity(this.getWorld(), this.getEntity()) {
				@Override
				protected void onEntityHit(EntityRayTraceResult raycast) {
					Entity entity = raycast.getEntity();
					if(entity == SnowStormGoal.this.getEntity())return;
					int i = entity instanceof BlazeEntity ? 3 : 1;
					entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, SnowStormGoal.this.getEntity()), (float)i);
				}
			};

			LivingEntity target = this.getEntity().getAttackTarget();

			if(target != null) {
				double d0 = target.getPosYEye() - (double) 1.1F;
				double d1 = target.getPosX() - this.getEntity().getPosX();
				double d2 = d0 - snowball.getPosY();
				double d3 = target.getPosZ() - this.getEntity().getPosZ();
				float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
				snowball.shoot(d1, d2 + (double) f, d3, 1.6F, 4.0F);
				this.getWorld().playSound(null, this.getEntity().getPosition(), SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, SoundCategory.HOSTILE, 1.0F, 0.4F / (this.getWorld().rand.nextFloat() * 0.4F + 0.8F));
				this.getWorld().addEntity(snowball);
			}

			this.progress++;
		}
	}

	@Override
	public void resetTask() {
		this.getEntity().setItemStackToSlot(EquipmentSlotType.OFFHAND, this.oldStack);
		this.oldStack = ItemStack.EMPTY;
		this.progress = 0;
	}

}

package iskallia.vault.entity.ai;

import iskallia.vault.init.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.INBTSerializable;

public class TeleportRandomly<T extends LivingEntity> implements INBTSerializable<CompoundNBT> {

	protected T entity;
	private final Condition<T>[] conditions;

	public TeleportRandomly(T entity) {
		this(entity, new Condition[0]);
	}

	public TeleportRandomly(T entity, Condition<T>... conditions) {
		this.entity = entity;
		this.conditions = conditions;
	}

	public boolean attackEntityFrom(DamageSource source, float amount) {
		for(Condition<T> condition : conditions) {
			double chance = condition.getChance(this.entity, source, amount);

			if(this.entity.world.rand.nextDouble() < chance) {
				for(int i = 0; i < 64; ++i) {
					if(this.teleportRandomly()) {
						System.out.println("TP!");
						this.entity.world.playSound(null, this.entity.prevPosX, this.entity.prevPosY, this.entity.prevPosZ,
								ModSounds.BOSS_TP_SFX, this.entity.getSoundCategory(), 1.0F, 1.0F);
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean teleportRandomly() {
		if (!this.entity.world.isRemote() && this.entity.isAlive()) {
			double d0 = this.entity.getPosX() + (this.entity.world.rand.nextDouble() - 0.5D) * 64.0D;
			double d1 = this.entity.getPosY() + (double)(this.entity.world.rand.nextInt(64) - 32);
			double d2 = this.entity.getPosZ() + (this.entity.world.rand.nextDouble() - 0.5D) * 64.0D;
			return this.entity.attemptTeleport(d0, d1, d2, true);
		}

		return false;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
	}

	public static <T extends LivingEntity> TeleportRandomly<T> fromNBT(T entity, CompoundNBT nbt) {
		TeleportRandomly<T> tp = new TeleportRandomly<>(entity);
		tp.deserializeNBT(nbt);
		return tp;
	}

	@FunctionalInterface
	public interface Condition<T extends LivingEntity> {
		double getChance(T entity, DamageSource source, double amount);
	}

}

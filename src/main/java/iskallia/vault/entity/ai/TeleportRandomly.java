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

			if(this.entity.level.random.nextDouble() < chance) {
				for(int i = 0; i < 64; ++i) {
					if(this.teleportRandomly()) {
						System.out.println("TP!");
						this.entity.level.playSound(null, this.entity.xo, this.entity.yo, this.entity.zo,
								ModSounds.BOSS_TP_SFX, this.entity.getSoundSource(), 1.0F, 1.0F);
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean teleportRandomly() {
		if (!this.entity.level.isClientSide() && this.entity.isAlive()) {
			double d0 = this.entity.getX() + (this.entity.level.random.nextDouble() - 0.5D) * 64.0D;
			double d1 = this.entity.getY() + (double)(this.entity.level.random.nextInt(64) - 32);
			double d2 = this.entity.getZ() + (this.entity.level.random.nextDouble() - 0.5D) * 64.0D;
			return this.entity.randomTeleport(d0, d1, d2, true);
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

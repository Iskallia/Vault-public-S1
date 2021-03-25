package iskallia.vault.entity;

import iskallia.vault.entity.ai.*;
import iskallia.vault.init.ModSounds;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FinalBossEntity extends FighterEntity  {

	public TeleportRandomly<FinalBossEntity> teleportTask = new TeleportRandomly<>(this, (entity, source, amount) -> {
		if(!(source.getEntity() instanceof LivingEntity)) {
			return 0.2D;
		}

		return 0.0D;
	});

	public RegenAfterAWhile<FinalBossEntity> regenAfterAWhile = new RegenAfterAWhile<>(this, 20 * 5, 10, 0.005F);

	public FinalBossEntity(EntityType<? extends ZombieEntity> type, World world) {
		super(type, world);

		if(!this.level.isClientSide) {
			this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		}

		this.bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS) {
			@Override
			public UUID getId() {
				return FinalBossEntity.this.getUUID();
			}
		};

		this.bossInfo.setVisible(true);
	}

	@Override
	public void tick() {
		if(!this.level.isClientSide) {
			String name = this.getCustomName().getString();

			ServerPlayerEntity player = this.getServer().getPlayerList().getPlayerByName(name);

			if((this.getTarget() instanceof PlayerEntity
					&& !this.getTarget().getName().getString().equals(name))
					|| this.level.random.nextInt(600) == 0) {

				if(player != null && player.level.dimension() == this.level.dimension()
						&& player.distanceTo(this) <= 200.0D) {
					this.setTarget(player);
				}
			}

			if(this.getTarget() == null && this.random.nextInt(100) == 0) {
				this.setTarget(this.level.getNearestEntity(LivingEntity.class, new EntityPredicate()
								.selector(entity -> {
									return entity instanceof PlayerEntity || entity instanceof EternalEntity;
								}),
							this, this.getX(), this.getY(), this.getZ(),
								this.getBoundingBox().inflate(128.0D, 128.0D, 128.0D)));
			}

			if(player != null) {
				this.setItemSlot(EquipmentSlotType.HEAD, player.getItemBySlot(EquipmentSlotType.HEAD).copy());
				this.setItemSlot(EquipmentSlotType.CHEST, player.getItemBySlot(EquipmentSlotType.CHEST).copy());
				this.setItemSlot(EquipmentSlotType.LEGS, player.getItemBySlot(EquipmentSlotType.LEGS).copy());
				this.setItemSlot(EquipmentSlotType.FEET, player.getItemBySlot(EquipmentSlotType.FEET).copy());
				this.setItemSlot(EquipmentSlotType.MAINHAND, player.getItemBySlot(EquipmentSlotType.MAINHAND).copy());

				ItemStack offhand = player.getItemBySlot(EquipmentSlotType.OFFHAND);

				if(offhand.getItem() != Items.SNOWBALL && offhand.getItem() != Items.FIRE_CHARGE) {
					this.setItemSlot(EquipmentSlotType.OFFHAND, offhand.copy());
				}
			}

			this.regenAfterAWhile.onDamageTaken();
		}

		super.tick();
	}

	@Override
	protected void addBehaviourGoals() {
		super.addBehaviourGoals();
		//this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, ArenaFighterEntity.class, false));

		this.goalSelector.addGoal(1, TeleportGoal.builder(this).start(entity -> {
			return entity.getTarget() != null && entity.tickCount % 60 == 0;
		}).to(entity -> {
			return entity.getTarget().position().add((entity.random.nextDouble() - 0.5D) * 8.0D,
					entity.random.nextInt(16) - 8, (entity.random.nextDouble() - 0.5D) * 8.0D);
		}).then(entity -> {
			entity.playSound(ModSounds.BOSS_TP_SFX, 1.0F, 1.0F);
		}).build());

		this.goalSelector.addGoal(1, new ThrowProjectilesGoal<>(this, 128, 10, SNOWBALLS));
		this.goalSelector.addGoal(1, new ThrowProjectilesGoal<>(this, 512, 3, (world1, shooter) -> new TNTEntity(world1, 0, 0, 0, shooter)));
		//this.goalSelector.addGoal(1, new AOEGoal<>(this, e -> !(e instanceof ArenaBossEntity)));

		this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(100.0D);
	}

	private float knockbackAttack(Entity entity) {
		for(int i = 0; i < 20; ++i) {
			double d0 = this.level.random.nextGaussian() * 0.02D;
			double d1 = this.level.random.nextGaussian() * 0.02D;
			double d2 = this.level.random.nextGaussian() * 0.02D;

			((ServerWorld)this.level).sendParticles(ParticleTypes.POOF,
					entity.getX() + this.level.random.nextDouble() - d0,
					entity.getY() + this.level.random.nextDouble() - d1,
					entity.getZ() + this.level.random.nextDouble() - d2, 10, d0, d1, d2, 1.0D);
		}

		this.level.playSound(null, entity.blockPosition(), SoundEvents.IRON_GOLEM_HURT, this.getSoundSource(), 1.0F, 1.0F);
		return 15.0F;
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		boolean ret = false;

		if(this.random.nextInt(12) == 0) {
			double old = this.getAttribute(Attributes.ATTACK_KNOCKBACK).getBaseValue();
			this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(this.knockbackAttack(entity));
			boolean result = super.doHurtTarget(entity);
			this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(old);
			ret |= result;
		}

		if(this.random.nextInt(6) == 0) {
			this.level.broadcastEntityEvent(this, (byte)4);
			float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
			float f1 = (int)f > 0 ? f / 2.0F + (float)this.random.nextInt((int)f) : f;
			boolean flag = entity.hurt(DamageSource.mobAttack(this), f1);

			if(flag) {
				entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.6F, 0.0D));
				this.doEnchantDamageEffects(this, entity);
			}

			this.level.playSound(null, entity.blockPosition(), SoundEvents.IRON_GOLEM_HURT, this.getSoundSource(), 1.0F, 1.0F);
			ret |= flag;
		}

		return ret || super.doHurtTarget(entity);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(!(source.getEntity() instanceof PlayerEntity)
				&& !(source.getEntity() instanceof EternalEntity)
				&& source != DamageSource.OUT_OF_WORLD) {
			return false;
		}

		if(this.isInvulnerableTo(source) || source == DamageSource.FALL) {
			return false;
		} else if(this.teleportTask.attackEntityFrom(source, amount)) {
			return true;
		}

		return super.hurt(source, amount);
	}

	@Override
	public boolean fireImmune() {
		return true;
	}

	@Override
	public boolean ignoreExplosion() {
		return true;
	}

	@Override
	public boolean displayFireAnimation() {
		return false;
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		super.finalizeSpawn(world, difficulty, reason, spawnData, dataTag);
		this.changeSize(5.0F);
		return spawnData;
	}

	public static AttributeModifierMap.MutableAttribute getCustomAttributes() {
		return MonsterEntity.createMonsterAttributes()
				.add(Attributes.FOLLOW_RANGE, 100.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.23F)
				.add(Attributes.ATTACK_DAMAGE, 3.0D)
				.add(Attributes.ARMOR, 2.0D)
				.add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
	}

}

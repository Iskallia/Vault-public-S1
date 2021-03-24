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
		if(!(source.getTrueSource() instanceof LivingEntity)) {
			return 0.2D;
		}

		return 0.0D;
	});

	public RegenAfterAWhile<FinalBossEntity> regenAfterAWhile = new RegenAfterAWhile<>(this, 20 * 5, 10, 0.005F);

	public FinalBossEntity(EntityType<? extends ZombieEntity> type, World world) {
		super(type, world);

		if(!this.world.isRemote) {
			this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		}

		this.bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS) {
			@Override
			public UUID getUniqueId() {
				return FinalBossEntity.this.getUniqueID();
			}
		};

		this.bossInfo.setVisible(true);
	}

	@Override
	public void tick() {
		if(!this.world.isRemote) {
			String name = this.getCustomName().getString();

			ServerPlayerEntity player = this.getServer().getPlayerList().getPlayerByUsername(name);

			if((this.getAttackTarget() instanceof PlayerEntity
					&& !this.getAttackTarget().getName().getString().equals(name))
					|| this.world.rand.nextInt(600) == 0) {

				if(player != null && player.world.getDimensionKey() == this.world.getDimensionKey()
						&& player.getDistance(this) <= 200.0D) {
					this.setAttackTarget(player);
				}
			}

			if(this.getAttackTarget() == null && this.rand.nextInt(100) == 0) {
				this.setAttackTarget(this.world.getClosestEntityWithinAABB(LivingEntity.class, new EntityPredicate()
								.setCustomPredicate(entity -> {
									return entity instanceof PlayerEntity || entity instanceof EternalEntity;
								}),
							this, this.getPosX(), this.getPosY(), this.getPosZ(),
								this.getBoundingBox().grow(128.0D, 128.0D, 128.0D)));
			}

			if(player != null) {
				this.setItemStackToSlot(EquipmentSlotType.HEAD, player.getItemStackFromSlot(EquipmentSlotType.HEAD).copy());
				this.setItemStackToSlot(EquipmentSlotType.CHEST, player.getItemStackFromSlot(EquipmentSlotType.CHEST).copy());
				this.setItemStackToSlot(EquipmentSlotType.LEGS, player.getItemStackFromSlot(EquipmentSlotType.LEGS).copy());
				this.setItemStackToSlot(EquipmentSlotType.FEET, player.getItemStackFromSlot(EquipmentSlotType.FEET).copy());
				this.setItemStackToSlot(EquipmentSlotType.MAINHAND, player.getItemStackFromSlot(EquipmentSlotType.MAINHAND).copy());

				ItemStack offhand = player.getItemStackFromSlot(EquipmentSlotType.OFFHAND);

				if(offhand.getItem() != Items.SNOWBALL && offhand.getItem() != Items.FIRE_CHARGE) {
					this.setItemStackToSlot(EquipmentSlotType.OFFHAND, offhand.copy());
				}
			}

			this.regenAfterAWhile.onDamageTaken();
		}

		super.tick();
	}

	@Override
	protected void applyEntityAI() {
		super.applyEntityAI();
		//this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, ArenaFighterEntity.class, false));

		this.goalSelector.addGoal(1, TeleportGoal.builder(this).start(entity -> {
			return entity.getAttackTarget() != null && entity.ticksExisted % 60 == 0;
		}).to(entity -> {
			return entity.getAttackTarget().getPositionVec().add((entity.rand.nextDouble() - 0.5D) * 8.0D,
					entity.rand.nextInt(16) - 8, (entity.rand.nextDouble() - 0.5D) * 8.0D);
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
			double d0 = this.world.rand.nextGaussian() * 0.02D;
			double d1 = this.world.rand.nextGaussian() * 0.02D;
			double d2 = this.world.rand.nextGaussian() * 0.02D;

			((ServerWorld)this.world).spawnParticle(ParticleTypes.POOF,
					entity.getPosX() + this.world.rand.nextDouble() - d0,
					entity.getPosY() + this.world.rand.nextDouble() - d1,
					entity.getPosZ() + this.world.rand.nextDouble() - d2, 10, d0, d1, d2, 1.0D);
		}

		this.world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_IRON_GOLEM_HURT, this.getSoundCategory(), 1.0F, 1.0F);
		return 15.0F;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		boolean ret = false;

		if(this.rand.nextInt(12) == 0) {
			double old = this.getAttribute(Attributes.ATTACK_KNOCKBACK).getBaseValue();
			this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(this.knockbackAttack(entity));
			boolean result = super.attackEntityAsMob(entity);
			this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(old);
			ret |= result;
		}

		if(this.rand.nextInt(6) == 0) {
			this.world.setEntityState(this, (byte)4);
			float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
			float f1 = (int)f > 0 ? f / 2.0F + (float)this.rand.nextInt((int)f) : f;
			boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), f1);

			if(flag) {
				entity.setMotion(entity.getMotion().add(0.0D, 0.6F, 0.0D));
				this.applyEnchantments(this, entity);
			}

			this.world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_IRON_GOLEM_HURT, this.getSoundCategory(), 1.0F, 1.0F);
			ret |= flag;
		}

		return ret || super.attackEntityAsMob(entity);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(!(source.getTrueSource() instanceof PlayerEntity)
				&& !(source.getTrueSource() instanceof EternalEntity)
				&& source != DamageSource.OUT_OF_WORLD) {
			return false;
		}

		if(this.isInvulnerableTo(source) || source == DamageSource.FALL) {
			return false;
		} else if(this.teleportTask.attackEntityFrom(source, amount)) {
			return true;
		}

		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean isImmuneToFire() {
		return true;
	}

	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		super.onInitialSpawn(world, difficulty, reason, spawnData, dataTag);
		this.changeSize(5.0F);
		return spawnData;
	}

	public static AttributeModifierMap.MutableAttribute getAttributes() {
		return MonsterEntity.func_234295_eP_()
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 100.0D)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.23F)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D)
				.createMutableAttribute(Attributes.ARMOR, 2.0D)
				.createMutableAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS);
	}

}

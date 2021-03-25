package iskallia.vault.entity;

import com.mojang.datafixers.util.Either;
import iskallia.vault.entity.ai.FollowEntityGoal;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.FighterSizeMessage;
import iskallia.vault.util.SkinProfile;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class EternalEntity extends ZombieEntity {

	public SkinProfile skin;
	public String lastName = "Eternal";
	public float sizeMultiplier = 1.0F;
	private long despawnTime = Long.MAX_VALUE;

	public final ServerBossInfo bossInfo;
	public UUID owner;

	public EternalEntity(EntityType<? extends ZombieEntity> type, World world) {
		super(type, world);

		if(!this.level.isClientSide) {
			this.changeSize(this.sizeMultiplier);
			this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.random.nextFloat() * 0.15D + 0.20D);
		} else {
			this.skin = new SkinProfile();
		}

		this.bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);
		this.bossInfo.setDarkenScreen(true);
		this.bossInfo.setVisible(false);

		this.setCustomName(new StringTextComponent(this.lastName));
		this.setCanPickUpLoot(false);
	}

	@Override
	protected void addBehaviourGoals() {
		super.addBehaviourGoals();
		this.targetSelector.addGoal(2, new FollowEntityGoal<>(this, 1.0D, 32.0F, 3.0F, false, () -> this.getOwner().right()));
	}

	public ResourceLocation getLocationSkin() {
		return this.skin.getLocationSkin();
	}

	public void setDespawnTime(long despawnTime) {
		this.despawnTime = despawnTime;
	}

	@Override
	public boolean isBaby() {
		return false;
	}

	@Override
	protected boolean isSunSensitive() {
		return false;
	}

	public Either<UUID, PlayerEntity> getOwner() {
		if(level.isClientSide)return Either.left(this.owner);
		ServerPlayerEntity player = this.getServer().getPlayerList().getPlayer(this.owner);
		return player == null ? Either.left(this.owner) : Either.right(player);
	}

	@Override
	public void tick() {
		super.tick();
		if(this.dead)return;

		if(this.level.isClientSide) {
			String name = this.getCustomName().getString();

			if(name.startsWith("[")) {
				String[] data = name.split(Pattern.quote("]"));
				name = data[1].trim();
			}

			if (!this.lastName.equals(name)) {
				this.skin.updateSkin(name);
				this.lastName = name;
			}
		} else {
			if(this.getServer().getTickCount() >= this.despawnTime) {
				this.kill(); //TODO: better
			}

			double amplitude = this.getDeltaMovement().distanceToSqr(0.0D, this.getDeltaMovement().y(), 0.0D);

			if(amplitude > 0.004D) {
				this.setSprinting(true);
				//this.getJumpController().setJumping();
			} else {
				this.setSprinting(false);
			}

			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
			if(this.tickCount % 10 == 0)this.updateAttackTarget();
		}
	}

	@Override
	public void setTarget(LivingEntity entity) {
		if(entity == this.getOwner().right().orElse(null) || entity instanceof EternalEntity)return;
		super.setTarget(entity);
	}

	@Override
	public void setLastHurtByMob(LivingEntity entity) {
		if(entity == this.getOwner().right().orElse(null) || entity instanceof EternalEntity)return;
		super.setLastHurtByMob(entity);
	}

	private void updateAttackTarget() {
		AxisAlignedBB box = this.getBoundingBox().inflate(32);

		this.level.getLoadedEntitiesOfClass(LivingEntity.class, box, e -> {
			Either<UUID, PlayerEntity> o = this.getOwner();

			if(o.right().isPresent() && o.right().get() == e) {
				return false;
			}

			return !(e instanceof EternalEntity);
		}).stream()
		.sorted(Comparator.comparingDouble(e -> e.position().distanceTo(this.position())))
		.findFirst().ifPresent(this::setTarget);
	}

	private Predicate<LivingEntity> ignoreEntities() {
		Predicate<LivingEntity> pred = e -> !(e instanceof EternalEntity) && !(e instanceof PlayerEntity);
		return pred;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.PLAYER_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.PLAYER_HURT;
	}

	@Override
	public void setCustomName(ITextComponent name) {
		super.setCustomName(name);
		this.bossInfo.setName(this.getDisplayName());
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.putFloat("SizeMultiplier", this.sizeMultiplier);
		compound.putLong("DespawnTime", this.despawnTime);
		if(this.owner != null)compound.putString("Owner", this.owner.toString());
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);

		if(compound.contains("SizeMultiplier", Constants.NBT.TAG_FLOAT)) {
			this.changeSize(compound.getFloat("SizeMultiplier"));
		}

		this.bossInfo.setName(this.getDisplayName());
		this.despawnTime = compound.getLong("DespawnTime");

		if(compound.contains("Owner", Constants.NBT.TAG_STRING)) {
			this.owner = UUID.fromString(compound.getString("Owner"));
		}
	}

	@Override
	public void startSeenByPlayer(ServerPlayerEntity player) {
		super.startSeenByPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayerEntity player) {
		super.stopSeenByPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	@Override
	public EntitySize getDimensions(Pose pose) {
		Field sizeField = Entity.class.getDeclaredFields()[79]; //Entity.size
		sizeField.setAccessible(true);

		try {
			return (EntitySize)sizeField.get(this);
		} catch(IllegalAccessException e) {
			e.printStackTrace();
			return super.getDimensions(pose);
		}
	}

	public float getSizeMultiplier() {
		return sizeMultiplier;
	}

	public EternalEntity changeSize(float m) {
		Field sizeField = Entity.class.getDeclaredFields()[79]; //Entity.size
		sizeField.setAccessible(true);

		try {
			sizeField.set(this, this.getDimensions(Pose.STANDING).scale(this.sizeMultiplier = m));
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}

		this.refreshDimensions();

		if(!this.level.isClientSide) {
			ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new FighterSizeMessage(this, this.sizeMultiplier));
		}

		return this;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntitySize size) {
		return super.getStandingEyeHeight(pose, size) * this.sizeMultiplier;
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		this.setCustomName(this.getCustomName());
		this.setCanBreakDoors(true);
		this.setCanPickUpLoot(true);
		this.setPersistenceRequired();

		//Good ol' easter egg.
		if(this.random.nextInt(100) == 0) {
			ChickenEntity chicken = EntityType.CHICKEN.create(this.level);
			chicken.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0F);
			chicken.finalizeSpawn(world, difficulty, reason, spawnData, dataTag);
			chicken.setChickenJockey(true);
			((ServerWorld)this.level).addWithUUID(chicken);
			this.startRiding(chicken);
		}

		return spawnData;
	}

	@Override
	protected void dropFromLootTable(DamageSource damageSource, boolean attackedRecently) {
		super.dropFromLootTable(damageSource, attackedRecently);
		if(this.level.isClientSide())return;

		/* Drop the head.
		if(!this.lastName.equals(this.getCustomName().getString())) {
			ItemStack headDrop = new ItemStack(Items.PLAYER_HEAD, 1);
			CompoundNBT nbt = new CompoundNBT();
			nbt.putString("SkullOwner", this.getCustomName().getString());
			headDrop.setTag(nbt);
			this.entityDropItem(headDrop, 0.0F);
		}*/
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		if (!this.level.isClientSide) {
			((ServerWorld)this.level).sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX(), this.getY(),
					this.getZ(), 1, 0.0f, 0.0f, 0.0f, 0);

			this.level.playSound(null, this.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
					SoundCategory.PLAYERS, 1.0f, this.random.nextFloat() - this.random.nextFloat());
		}

		return super.doHurtTarget(entity);
	}

}

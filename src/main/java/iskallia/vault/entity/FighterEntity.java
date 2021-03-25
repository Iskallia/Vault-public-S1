package iskallia.vault.entity;

import iskallia.vault.entity.ai.ThrowProjectilesGoal;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.FighterSizeMessage;
import iskallia.vault.util.SkinProfile;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.EntityRayTraceResult;
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
import java.util.regex.Pattern;

public class FighterEntity extends ZombieEntity {

	public static final ThrowProjectilesGoal.Projectile SNOWBALLS = (world1, shooter) -> {
		return new SnowballEntity(world1, shooter) {
			@Override
			protected void onHitEntity(EntityRayTraceResult raycast) {
				Entity entity = raycast.getEntity();
				if(entity == shooter)return;
				int i = entity instanceof BlazeEntity ? 3 : 1;
				entity.hurt(DamageSource.indirectMobAttack(this, shooter), (float)i);
			}
		};
	};

	public SkinProfile skin;
	public String lastName = "Fighter";
	public float sizeMultiplier = 1.0F;

	public ServerBossInfo bossInfo;

	public FighterEntity(EntityType<? extends ZombieEntity> type, World world) {
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
	}

	public ResourceLocation getLocationSkin() {
		return this.skin.getLocationSkin();
	}

	@Override
	public boolean isBaby() {
		return false;
	}

	@Override
	protected boolean isSunSensitive() {
		return false;
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
			double amplitude = this.getDeltaMovement().distanceToSqr(0.0D, this.getDeltaMovement().y(), 0.0D);

			if(amplitude > 0.004D) {
				this.setSprinting(true);
				//this.getJumpController().setJumping();
			} else {
				this.setSprinting(false);
			}

			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
		}
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
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);

		if(compound.contains("SizeMultiplier", Constants.NBT.TAG_FLOAT)) {
			this.changeSize(compound.getFloat("SizeMultiplier"));
		}

		this.bossInfo.setName(this.getDisplayName());
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

	public FighterEntity changeSize(float m) {
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
package iskallia.vault.entity;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.FighterSizeMessage;
import iskallia.vault.util.SkinProfile;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
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

	public SkinProfile skin;
	public String lastName = "Fighter";
	public float sizeMultiplier = 1.0F;

	public final ServerBossInfo bossInfo;

	public FighterEntity(EntityType<? extends ZombieEntity> type, World world) {
		super(type, world);

		if(!this.world.isRemote) {
			this.changeSize(this.sizeMultiplier);
			this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.rand.nextFloat() * 0.15D + 0.20D);
		} else {
			this.skin = new SkinProfile();
		}

		this.bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);
		this.bossInfo.setDarkenSky(true);
		this.bossInfo.setVisible(false);

		this.setCustomName(new StringTextComponent(this.lastName));
	}

	public ResourceLocation getLocationSkin() {
		return this.skin.getLocationSkin();
	}

	@Override
	public boolean isChild() {
		return false;
	}

	@Override
	protected boolean shouldBurnInDay() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		if(this.dead)return;

		if(this.world.isRemote) {
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
			double amplitude = this.getMotion().squareDistanceTo(0.0D, this.getMotion().getY(), 0.0D);

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
		return SoundEvents.ENTITY_PLAYER_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_PLAYER_HURT;
	}

	@Override
	public void setCustomName(ITextComponent name) {
		super.setCustomName(name);
		this.bossInfo.setName(this.getDisplayName());
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putFloat("SizeMultiplier", this.sizeMultiplier);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);

		if(compound.contains("SizeMultiplier", Constants.NBT.TAG_FLOAT)) {
			this.changeSize(compound.getFloat("SizeMultiplier"));
		}

		this.bossInfo.setName(this.getDisplayName());
	}

	@Override
	public void addTrackingPlayer(ServerPlayerEntity player) {
		super.addTrackingPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	@Override
	public void removeTrackingPlayer(ServerPlayerEntity player) {
		super.removeTrackingPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	@Override
	public EntitySize getSize(Pose pose) {
		Field sizeField = Entity.class.getDeclaredFields()[79]; //Entity.size
		sizeField.setAccessible(true);

		try {
			return (EntitySize)sizeField.get(this);
		} catch(IllegalAccessException e) {
			e.printStackTrace();
			return super.getSize(pose);
		}
	}

	public float getSizeMultiplier() {
		return sizeMultiplier;
	}

	public FighterEntity changeSize(float m) {
		Field sizeField = Entity.class.getDeclaredFields()[79]; //Entity.size
		sizeField.setAccessible(true);

		try {
			sizeField.set(this, this.getSize(Pose.STANDING).scale(this.sizeMultiplier = m));
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}

		this.recalculateSize();

		if(!this.world.isRemote) {
			ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new FighterSizeMessage(this, this.sizeMultiplier));
		}

		return this;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntitySize size) {
		return super.getStandingEyeHeight(pose, size) * this.sizeMultiplier;
	}

	@Override
	public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		this.setCustomName(this.getCustomName());
		this.setBreakDoorsAItask(true);
		this.setCanPickUpLoot(true);
		this.enablePersistence();

		//Good ol' easter egg.
		if(this.rand.nextInt(100) == 0) {
			ChickenEntity chicken = EntityType.CHICKEN.create(this.world);
			chicken.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, 0.0F);
			chicken.onInitialSpawn(world, difficulty, reason, spawnData, dataTag);
			chicken.setChickenJockey(true);
			((ServerWorld)this.world).summonEntity(chicken);
			this.startRiding(chicken);
		}

		return spawnData;
	}

	@Override
	protected void dropLoot(DamageSource damageSource, boolean attackedRecently) {
		super.dropLoot(damageSource, attackedRecently);
		if(this.world.isRemote())return;

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
	public boolean attackEntityAsMob(Entity entity) {
		if (!this.world.isRemote) {
			((ServerWorld)this.world).spawnParticle(ParticleTypes.SWEEP_ATTACK, this.getPosX(), this.getPosY(),
					this.getPosZ(), 1, 0.0f, 0.0f, 0.0f, 0);

			this.world.playSound(null, this.getPosition(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
					SoundCategory.PLAYERS, 1.0f, this.rand.nextFloat() - this.rand.nextFloat());
		}

		return super.attackEntityAsMob(entity);
	}

}
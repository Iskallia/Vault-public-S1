package iskallia.vault.entity;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.lang.reflect.Field;

public class FinalDummyEntity extends VaultGuardianEntity {

	public float sizeMultiplier = 1.0F;
	public final ServerBossInfo bossInfo;

	public FinalDummyEntity(EntityType<? extends PiglinBruteEntity> type, World world) {
		super(type, world);
		this.changeSize(6.0F);
		this.bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);
		this.bossInfo.setVisible(true);
		this.setCustomName(new StringTextComponent("The Guardian"));
	}

	@Override
	public void setCustomName(ITextComponent name) {
		super.setCustomName(name);
		this.bossInfo.setName(this.getDisplayName());
	}

	@Override
	public void tick() {
		if(!this.level.isClientSide) {
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
			this.setTarget(this.level.getNearestPlayer(this, 96.0D));
		}

		super.tick();
	}

	public void changeSize(float m) {
		Field sizeField = Entity.class.getDeclaredFields()[79]; //Entity.size
		sizeField.setAccessible(true);

		try {
			sizeField.set(this, this.getDimensions(Pose.STANDING).scale(this.sizeMultiplier = m));
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}

		this.refreshDimensions();
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

	@Override
	protected float getStandingEyeHeight(Pose pose, EntitySize size) {
		return super.getStandingEyeHeight(pose, size) * this.sizeMultiplier;
	}

	@Override
	public void die(DamageSource cause) {
		super.die(cause);

		if(!this.level.isClientSide) {
			VaultRaid raid = VaultRaidData.get((ServerWorld)this.level).getAt(this.blockPosition());

			ModConfigs.FINAL_VAULT_GENERAL.bossNames.forEach(name -> {
				FinalBossEntity boss = ModEntities.FINAL_BOSS.create(this.level);
				boss.setCustomName(new StringTextComponent(name));

				if(raid != null) {
					boss.addTag("VaultBoss");
					raid.addBoss(boss);
				}

				boss.changeSize(1.0F);
				boss.moveTo(this.getX() + 0.5D, this.getY() + 0.2D, this.getZ() + 0.5D, 0.0F, 0.0F);
				((ServerWorld)this.level).addWithUUID(boss);
			});
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(!(source.getEntity() instanceof PlayerEntity)
				|| !(source.getDirectEntity() instanceof PlayerEntity)) {
			if(source != DamageSource.OUT_OF_WORLD) {
				return false;
			}
		}

		this.playSound(SoundEvents.IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
		return super.hurt(source, this.getMaxHealth() / 50.0F);
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

	public static AttributeModifierMap.MutableAttribute getCustomAttributes() {
		return MonsterEntity.createMonsterAttributes()
				.add(Attributes.FOLLOW_RANGE, 100.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.15F)
				.add(Attributes.ATTACK_DAMAGE, 3.0D)
				.add(Attributes.ARMOR, 2.0D)
				.add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
	}

}

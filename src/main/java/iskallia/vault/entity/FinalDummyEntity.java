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
		if(!this.world.isRemote) {
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
			this.setAttackTarget(this.world.getClosestPlayer(this, 96.0D));
		}

		super.tick();
	}

	public void changeSize(float m) {
		Field sizeField = Entity.class.getDeclaredFields()[79]; //Entity.size
		sizeField.setAccessible(true);

		try {
			sizeField.set(this, this.getSize(Pose.STANDING).scale(this.sizeMultiplier = m));
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}

		this.recalculateSize();
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

	@Override
	protected float getStandingEyeHeight(Pose pose, EntitySize size) {
		return super.getStandingEyeHeight(pose, size) * this.sizeMultiplier;
	}

	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);

		if(!this.world.isRemote) {
			VaultRaid raid = VaultRaidData.get((ServerWorld)this.world).getAt(this.getPosition());

			ModConfigs.FINAL_VAULT_GENERAL.bossNames.forEach(name -> {
				FinalBossEntity boss = ModEntities.FINAL_BOSS.create(this.world);
				boss.setCustomName(new StringTextComponent(name));

				if(raid != null) {
					boss.addTag("VaultBoss");
					raid.addBoss(boss);
				}

				boss.changeSize(1.0F);
				boss.setLocationAndAngles(this.getPosX() + 0.5D, this.getPosY() + 0.2D, this.getPosZ() + 0.5D, 0.0F, 0.0F);
				((ServerWorld)this.world).summonEntity(boss);
			});
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(!(source.getTrueSource() instanceof PlayerEntity)
				|| !(source.getImmediateSource() instanceof PlayerEntity)) {
			if(source != DamageSource.OUT_OF_WORLD) {
				return false;
			}
		}

		this.playSound(SoundEvents.ENTITY_IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
		return super.attackEntityFrom(source, this.getMaxHealth() / 50.0F);
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

	public static AttributeModifierMap.MutableAttribute getAttributes() {
		return MonsterEntity.func_234295_eP_()
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 100.0D)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15F)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D)
				.createMutableAttribute(Attributes.ARMOR, 2.0D)
				.createMutableAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS);
	}

}

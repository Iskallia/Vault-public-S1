package iskallia.vault.entity;

import iskallia.vault.entity.ai.*;
import iskallia.vault.init.ModSounds;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BoogiemanEntity extends ZombieEntity implements VaultBoss {

    public TeleportRandomly<BoogiemanEntity> teleportTask = new TeleportRandomly<>(this, (entity, source, amount) -> {
        if (!(source.getEntity() instanceof LivingEntity)) {
            return 0.2D;
        }

        return 0.0D;
    });

    public final ServerBossInfo bossInfo;
    public RegenAfterAWhile<BoogiemanEntity> regenAfterAWhile;

    public BoogiemanEntity(EntityType<? extends ZombieEntity> type, World worldIn) {
        super(type, worldIn);
        bossInfo = new ServerBossInfo(getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);
        regenAfterAWhile = new RegenAfterAWhile<>(this);
    }

    @Override
    protected void dropFromLootTable(DamageSource damageSource, boolean attackedRecently) { }

    @Override
    protected void addBehaviourGoals() {
        super.addBehaviourGoals();
        //this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, ArenaFighterEntity.class, false));

        this.goalSelector.addGoal(1, TeleportGoal.builder(this).start(entity -> {
            return entity.getTarget() != null && entity.tickCount % 60 == 0;
        }).to(entity -> {
            return entity.getTarget().position().add((entity.random.nextDouble() - 0.5D) * 8.0D, entity.random.nextInt(16) - 8, (entity.random.nextDouble() - 0.5D) * 8.0D);
        }).then(entity -> {
            entity.playSound(ModSounds.BOSS_TP_SFX, 1.0F, 1.0F);
        }).build());

        this.goalSelector.addGoal(1, new ThrowProjectilesGoal<>(this, 96, 10, FighterEntity.SNOWBALLS));
        this.goalSelector.addGoal(1, new AOEGoal<>(this, e -> !(e instanceof VaultBoss)));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(100.0D);
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public void spawnInTheWorld(VaultRaid raid, ServerWorld world, BlockPos pos) {
        this.moveTo(pos.getX() + 0.5D, pos.getY() + 0.2D, pos.getZ() + 0.5D, 0.0F, 0.0F);
        world.addWithUUID(this);

        this.getTags().add("VaultBoss");
        this.bossInfo.setVisible(true);

        if (raid != null) {
            raid.addBoss(this);
            EntityScaler.scaleVault(this, raid.level, new Random(), EntityScaler.Type.BOSS);

            if (raid.playerBossName != null) {
                this.setCustomName(new StringTextComponent(raid.playerBossName));
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!(source.getEntity() instanceof PlayerEntity)
                && !(source.getEntity() instanceof EternalEntity)
                && source != DamageSource.OUT_OF_WORLD) {
            return false;
        }

        if (this.isInvulnerableTo(source) || source == DamageSource.FALL) {
            return false;
        } else if (teleportTask.attackEntityFrom(source, amount)) {
            return true;
        }

        regenAfterAWhile.onDamageTaken();
        return super.hurt(source, amount);
    }

    @Override
    public ServerBossInfo getServerBossInfo() {
        return bossInfo;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level.isClientSide) {
            this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
            this.regenAfterAWhile.tick();
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
    public SoundCategory getSoundSource() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.BOOGIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.BOOGIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BOOGIE_DEATH;
    }

}

package iskallia.vault.entity;

import iskallia.vault.entity.ai.*;
import iskallia.vault.init.ModSounds;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class MonsterEyeEntity extends SlimeEntity implements VaultBoss {

    public TeleportRandomly<MonsterEyeEntity> teleportTask = new TeleportRandomly<>(this, (entity, source, amount) -> {
        if(!(source.getTrueSource() instanceof LivingEntity)) {
            return 0.2D;
        }

        return 0.0D;
    });

    public boolean shouldBlockSlimeSplit;
    public final ServerBossInfo bossInfo;
    public RegenAfterAWhile<MonsterEyeEntity> regenAfterAWhile;

    public MonsterEyeEntity(EntityType<? extends SlimeEntity> type, World worldIn) {
        super(type, worldIn);
        setSlimeSize(3, false);
        bossInfo = new ServerBossInfo(getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);
        regenAfterAWhile = new RegenAfterAWhile<>(this);
    }

    @Override
    protected void dropLoot(DamageSource damageSource, boolean attackedRecently) { }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        //this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, ArenaFighterEntity.class, false));

        this.goalSelector.addGoal(1, TeleportGoal.builder(this).start(entity -> {
            return entity.getAttackTarget() != null && entity.ticksExisted % 60 == 0;
        }).to(entity -> {
            return entity.getAttackTarget().getPositionVec().add((entity.rand.nextDouble() - 0.5D) * 8.0D, entity.rand.nextInt(16) - 8, (entity.rand.nextDouble() - 0.5D) * 8.0D);
        }).then(entity -> {
            entity.playSound(ModSounds.BOSS_TP_SFX, 1.0F, 1.0F);
        }).build());

        this.goalSelector.addGoal(1, new ThrowProjectilesGoal<>(this, 96, 10, FighterEntity.SNOWBALLS));
        this.goalSelector.addGoal(1, new AOEGoal<>(this, e -> !(e instanceof VaultBoss)));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(100.0D);
    }

    @Override
    public void spawnInTheWorld(VaultRaid raid, ServerWorld world, BlockPos pos) {
        this.spawnInTheWorld(raid, world, pos, 3);
    }

    public void spawnInTheWorld(VaultRaid raid, ServerWorld world, BlockPos pos, int size) {
        this.setSlimeSize(size, false);
        this.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.2D, pos.getZ() + 0.5D, 0.0F, 0.0F);
        world.summonEntity(this);

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
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if(!(source.getTrueSource() instanceof PlayerEntity)
                && !(source.getTrueSource() instanceof EternalEntity)
                && source != DamageSource.OUT_OF_WORLD) {
            return false;
        }

        if(this.isInvulnerableTo(source) || source == DamageSource.FALL) {
            return false;
        } else if(teleportTask.attackEntityFrom(source, amount)) {
            return true;
        }

        regenAfterAWhile.onDamageTaken();
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void dealDamage(LivingEntity entityIn) {
        if (this.isAlive()) {
            int i = this.getSlimeSize();
            if (this.getDistanceSq(entityIn) < 0.8D * (double) i * 0.8D * (double) i && this.canEntityBeSeen(entityIn) && entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), this.func_225512_er_())) {
                this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                this.applyEnchantments(this, entityIn);
            }
        }
    }

    @Override
    public void remove(boolean keepData) {
        shouldBlockSlimeSplit = true;
        super.remove(keepData);
    }

    @Override
    public int getSlimeSize() {
        return shouldBlockSlimeSplit ? 0 : super.getSlimeSize();
    }

    @Override
    public ServerBossInfo getServerBossInfo() {
        return bossInfo;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.world.isRemote) {
            this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
            this.regenAfterAWhile.tick();
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

}

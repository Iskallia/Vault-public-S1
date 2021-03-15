package iskallia.vault.world.raid;

import iskallia.vault.Vault;
import iskallia.vault.config.VaultMobsConfig;
import iskallia.vault.init.ModConfigs;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public class VaultSpawner {

	private final VaultRaid raid;
	private List<LivingEntity> mobs = new ArrayList<>();
	public int maxMobs;

	public VaultSpawner(VaultRaid raid) {
		this.raid = raid;
	}

	public void init() {
		VaultMobsConfig.Level config = ModConfigs.VAULT_MOBS.getForLevel(this.raid.level);
		this.maxMobs = config.MOB_MISC.MAX_MOBS;
	}

	public int getMaxMobs() {
		return this.maxMobs;
	}

	public void tick(ServerPlayerEntity player) {
		if(player.world.getDimensionKey() != Vault.VAULT_KEY)return;
		if(this.raid.ticksLeft + 15 * 20 > this.raid.sTickLeft)return;

		this.mobs.removeIf(entity -> {
			if(entity.getDistanceSq(player) > 24 * 24) {
				entity.remove();
				return true;
			}

			return false;
		});

		if(this.mobs.size() >= this.getMaxMobs())return;

		List<BlockPos> spaces = this.getSpawningSpaces(player);

		while(this.mobs.size() < this.getMaxMobs() && spaces.size() > 0) {
			 BlockPos pos = spaces.remove(player.getServerWorld().getRandom().nextInt(spaces.size()));
			 this.spawn(player.getServerWorld(), pos);
		}
	}

	private List<BlockPos> getSpawningSpaces(ServerPlayerEntity player) {
		List<BlockPos> spaces = new ArrayList<>();

		for(int x = -18; x <= 18; x++) {
			for(int z = -18; z <= 18; z++) {
				for(int y = -5; y <= 5; y++) {
					ServerWorld world = player.getServerWorld();
					BlockPos pos = player.getPosition().add(new BlockPos(x, y, z));

					if(player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) < 10 * 10) {
						continue;
					}

					if(!world.getBlockState(pos).canEntitySpawn(world, pos, EntityType.ZOMBIE))continue;
					boolean isAir = true;

					for(int o = 1; o <= 2; o++) {
						if(world.getBlockState(pos.up(o)).isSuffocating(world, pos)) {
							isAir = false;
							break;
						}
					}

					if(isAir) {
						spaces.add(pos.up());
					}
				}
			}
		}

		return spaces;
	}

	public void spawn(ServerWorld world, BlockPos pos) {
		VaultMobsConfig.Mob mob = ModConfigs.VAULT_MOBS.getForLevel(this.raid.level).MOB_POOL.getRandom(world.rand);
		if(mob == null)return;
		LivingEntity entity = mob.create(world);

		if(entity != null) {
			entity.setLocationAndAngles(pos.getX() + 0.5F, pos.getY() + 0.2F, pos.getZ() + 0.5F, 0.0F, 0.0F);
			world.summonEntity(entity);

			if(entity instanceof MobEntity) {
				((MobEntity)entity).spawnExplosionParticle();
				((MobEntity)entity).onInitialSpawn(world, new DifficultyInstance(Difficulty.PEACEFUL, 13000L, 0L, 0L),
						SpawnReason.STRUCTURE, null, null);
			}


			this.mobs.add(entity);
		}
	}

}

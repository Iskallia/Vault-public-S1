package iskallia.vault.entity;

import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class VaultFighterEntity extends FighterEntity {

	public VaultFighterEntity(EntityType<? extends ZombieEntity> type, World world) {
		super(type, world);
	}

	@Override
	public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		ILivingEntityData livingData = super.onInitialSpawn(world, difficulty, reason, spawnData, dataTag);

		if(!this.world.isRemote) {
			VaultRaid raid = VaultRaidData.get((ServerWorld)this.world).getAt(this.getPosition());

			if(raid != null) {
				ServerPlayerEntity player = ((ServerWorld)world).getServer().getPlayerList().getPlayerByUUID(raid.playerIds.get(0));
				String name = player != null ? player.getName().getString() : "";
				this.setCustomName(new StringTextComponent(name));
			}
		}

		return livingData;
	}

}

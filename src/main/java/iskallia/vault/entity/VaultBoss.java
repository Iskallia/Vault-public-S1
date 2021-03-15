package iskallia.vault.entity;

import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

// Yet another Y E S interface. Pls don't kill me, Wu
public interface VaultBoss {

    void spawnInTheWorld(VaultRaid raid, ServerWorld world, BlockPos pos);

    ServerBossInfo getServerBossInfo();

}

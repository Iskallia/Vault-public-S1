package iskallia.vault.skill;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.VaultLevelMessage;
import iskallia.vault.util.NetcodeUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.UUID;

public class PlayerVaultStats implements INBTSerializable<CompoundNBT> {

    private final UUID uuid;
    private int vaultLevel;
    private int exp;
    private int unspentSkillPts;
    private int unspentKnowledgePts;

    public PlayerVaultStats(UUID uuid) {
        this.uuid = uuid;
    }

    public int getVaultLevel() {
        return vaultLevel;
    }

    public int getExp() {
        return exp;
    }

    public int getUnspentSkillPts() {
        return unspentSkillPts;
    }

    public int getUnspentKnowledgePts() {
        return unspentKnowledgePts;
    }

    public int getTnl() {
        return ModConfigs.LEVELS_META.getLevelMeta(this.vaultLevel).tnl;
    }

    /* --------------------------------------- */

    public PlayerVaultStats setVaultLevel(MinecraftServer server, int level) {
        this.vaultLevel = level;
        this.exp = 0;
        sync(server);

        return this;
    }

    public PlayerVaultStats addVaultExp(MinecraftServer server, int exp) {
        int tnl;
        this.exp += exp;

        int initialLevel = this.vaultLevel;

        while (this.exp >= (tnl = getTnl())) {
            this.vaultLevel++;
            this.unspentSkillPts++;
            this.exp -= tnl; // Carry extra exp to next level!
        }

        if (this.vaultLevel > initialLevel) {
            NetcodeUtils.runIfPresent(server, uuid, this::fancyLevelUpEffects);
        }

        sync(server);

        return this;
    }

    protected void fancyLevelUpEffects(ServerPlayerEntity player) {
        World world = player.world;

        Vector3d pos = player.getPositionVec();

        for (int i = 0; i < 20; ++i) {
            double d0 = world.rand.nextGaussian() * 1D;
            double d1 = world.rand.nextGaussian() * 1D;
            double d2 = world.rand.nextGaussian() * 1D;

            ((ServerWorld) world).spawnParticle(ParticleTypes.TOTEM_OF_UNDYING,
                    pos.getX() + world.rand.nextDouble() - 0.5,
                    pos.getY() + world.rand.nextDouble() - 0.5 + 3,
                    pos.getZ() + world.rand.nextDouble() - 0.5, 10, d0, d1, d2, 0.25D);
        }

        world.playSound(null, player.getPosition(), ModSounds.VAULT_LEVEL_UP_SFX, SoundCategory.PLAYERS,
                1.0F, 2f);
    }

    public PlayerVaultStats spendSkillPoints(MinecraftServer server, int amount) {
        this.unspentSkillPts -= amount;

        sync(server);

        return this;
    }

    public PlayerVaultStats spendKnowledgePoints(MinecraftServer server, int amount) {
        this.unspentKnowledgePts -= amount;

        sync(server);

        return this;
    }

    public PlayerVaultStats reset(MinecraftServer server) {
        this.vaultLevel = 0;
        this.exp = 0;
        this.unspentSkillPts = 0;
        this.unspentKnowledgePts = 0;

        sync(server);

        return this;
    }

    public PlayerVaultStats addSkillPoints(int amount) {
        this.unspentSkillPts += amount;
        return this;
    }

    public PlayerVaultStats addKnowledgePoints(int amount) {
        this.unspentKnowledgePts += amount;
        return this;
    }

    /* --------------------------------------- */

    public void sync(MinecraftServer server) {
        NetcodeUtils.runIfPresent(server, this.uuid, player -> {
            ModNetwork.CHANNEL.sendTo(
                    new VaultLevelMessage(this.vaultLevel, this.exp, this.getTnl(), this.unspentSkillPts, this.unspentKnowledgePts),
                    player.connection.netManager,
                    NetworkDirection.PLAY_TO_CLIENT
            );
        });
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("vaultLevel", vaultLevel);
        nbt.putInt("exp", exp);
        nbt.putInt("unspentSkillPts", unspentSkillPts);
        nbt.putInt("unspentKnowledgePts", unspentKnowledgePts);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.vaultLevel = nbt.getInt("vaultLevel");
        this.exp = nbt.getInt("exp");
        this.unspentSkillPts = nbt.getInt("unspentSkillPts");
        this.unspentKnowledgePts = nbt.getInt("unspentKnowledgePts");
        this.vaultLevel = nbt.getInt("vaultLevel");
    }

}

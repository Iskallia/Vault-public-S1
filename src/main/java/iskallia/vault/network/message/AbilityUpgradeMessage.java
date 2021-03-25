package iskallia.vault.network.message;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AbilityUpgradeMessage {

    public String abilityName;

    public AbilityUpgradeMessage() { }

    public AbilityUpgradeMessage(String abilityName) {
        this.abilityName = abilityName;
    }

    public static void encode(AbilityUpgradeMessage message, PacketBuffer buffer) {
        buffer.writeUtf(message.abilityName, 32767);
    }

    public static AbilityUpgradeMessage decode(PacketBuffer buffer) {
        AbilityUpgradeMessage message = new AbilityUpgradeMessage();
        message.abilityName = buffer.readUtf(32767);
        return message;
    }

    public static void handle(AbilityUpgradeMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity sender = context.getSender();

            if (sender == null) return;

            AbilityGroup<?> abilityGroup = ModConfigs.ABILITIES.getByName(message.abilityName);

            PlayerVaultStatsData statsData = PlayerVaultStatsData.get((ServerWorld) sender.level);
            PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get((ServerWorld) sender.level);
            AbilityTree abilityTree = abilitiesData.getAbilities(sender);

            AbilityNode<?> abilityNode = abilityTree.getNodeByName(message.abilityName);
            PlayerVaultStats stats = statsData.getVaultStats(sender);

            if (abilityNode.getLevel() >= abilityGroup.getMaxLevel())
                return; // Already maxed out

            int requiredSkillPts = abilityGroup.cost(abilityNode.getLevel() + 1);

            if (stats.getUnspentSkillPts() >= requiredSkillPts) {
                abilitiesData.upgradeAbility(sender, abilityNode);
                statsData.spendSkillPts(sender, requiredSkillPts);
            }
        });
        context.setPacketHandled(true);
    }

}

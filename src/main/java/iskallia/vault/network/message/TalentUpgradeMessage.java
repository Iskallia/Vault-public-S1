package iskallia.vault.network.message;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

// From Client to Server
// "Hey dude, I want to upgrade dis talent o' mine. May I?"
public class TalentUpgradeMessage {

    public String talentName;

    public TalentUpgradeMessage() { }

    public TalentUpgradeMessage(String talentName) {
        this.talentName = talentName;
    }

    public static void encode(TalentUpgradeMessage message, PacketBuffer buffer) {
        buffer.writeUtf(message.talentName, 32767);
    }

    public static TalentUpgradeMessage decode(PacketBuffer buffer) {
        TalentUpgradeMessage message = new TalentUpgradeMessage();
        message.talentName = buffer.readUtf(32767);
        return message;
    }

    public static void handle(TalentUpgradeMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity sender = context.getSender();

            if (sender == null) return;

            TalentGroup<?> talentGroup = ModConfigs.TALENTS.getByName(message.talentName);

            PlayerVaultStatsData statsData = PlayerVaultStatsData.get((ServerWorld) sender.level);
            PlayerTalentsData abilitiesData = PlayerTalentsData.get((ServerWorld) sender.level);
            TalentTree talentTree = abilitiesData.getTalents(sender);

            if (ModConfigs.SKILL_GATES.getGates().isLocked(talentGroup, talentTree))
                return; // Cannot upgrade locked skill...

            TalentNode<?> talentNode = talentTree.getNodeByName(message.talentName);
            PlayerVaultStats stats = statsData.getVaultStats(sender);

            if (talentNode.getLevel() >= talentGroup.getMaxLevel())
                return; // Already maxed out

            int requiredSkillPts = talentGroup.cost(talentNode.getLevel() + 1);

            if (stats.getUnspentSkillPts() >= requiredSkillPts) {
                abilitiesData.upgradeTalent(sender, talentNode);
                statsData.spendSkillPts(sender, requiredSkillPts);
            }
        });
        context.setPacketHandled(true);
    }

}

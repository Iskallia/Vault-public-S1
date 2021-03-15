package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.network.message.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModNetwork {

    private static final String NETWORK_VERSION = "0.21.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Vault.MOD_ID, "network"),
            () -> NETWORK_VERSION,
            version -> version.equals(NETWORK_VERSION), // Client acceptance predicate
            version -> version.equals(NETWORK_VERSION) // Server acceptance predicate
    );

    private static int ID = 0;

    public static void initialize() {
        CHANNEL.registerMessage(nextId(), OpenSkillTreeMessage.class,
                OpenSkillTreeMessage::encode,
                OpenSkillTreeMessage::decode,
                OpenSkillTreeMessage::handle);

        CHANNEL.registerMessage(nextId(), VaultLevelMessage.class,
                VaultLevelMessage::encode,
                VaultLevelMessage::decode,
                VaultLevelMessage::handle);

        CHANNEL.registerMessage(nextId(), TalentUpgradeMessage.class,
                TalentUpgradeMessage::encode,
                TalentUpgradeMessage::decode,
                TalentUpgradeMessage::handle);

        CHANNEL.registerMessage(nextId(), ResearchMessage.class,
                ResearchMessage::encode,
                ResearchMessage::decode,
                ResearchMessage::handle);

        CHANNEL.registerMessage(nextId(), ResearchTreeMessage.class,
                ResearchTreeMessage::encode,
                ResearchTreeMessage::decode,
                ResearchTreeMessage::handle);

        CHANNEL.registerMessage(nextId(), AbilityKeyMessage.class,
                AbilityKeyMessage::encode,
                AbilityKeyMessage::decode,
                AbilityKeyMessage::handle);

        CHANNEL.registerMessage(nextId(), AbilityUpgradeMessage.class,
                AbilityUpgradeMessage::encode,
                AbilityUpgradeMessage::decode,
                AbilityUpgradeMessage::handle);

        CHANNEL.registerMessage(nextId(), AbilityKnownOnesMessage.class,
                AbilityKnownOnesMessage::encode,
                AbilityKnownOnesMessage::decode,
                AbilityKnownOnesMessage::handle);

        CHANNEL.registerMessage(nextId(), AbilityFocusMessage.class,
                AbilityFocusMessage::encode,
                AbilityFocusMessage::decode,
                AbilityFocusMessage::handle);

        CHANNEL.registerMessage(nextId(), AbilityActivityMessage.class,
                AbilityActivityMessage::encode,
                AbilityActivityMessage::decode,
                AbilityActivityMessage::handle);

        CHANNEL.registerMessage(nextId(), VaultRaidTickMessage.class,
                VaultRaidTickMessage::encode,
                VaultRaidTickMessage::decode,
                VaultRaidTickMessage::handle);

        CHANNEL.registerMessage(nextId(), FighterSizeMessage.class,
                FighterSizeMessage::encode,
                FighterSizeMessage::decode,
                FighterSizeMessage::handle);

        CHANNEL.registerMessage(nextId(), VendingUIMessage.class,
                VendingUIMessage::encode,
                VendingUIMessage::decode,
                VendingUIMessage::handle);

        CHANNEL.registerMessage(nextId(), VaultBeginMessage.class,
                VaultBeginMessage::encode,
                VaultBeginMessage::decode,
                VaultBeginMessage::handle);

        CHANNEL.registerMessage(nextId(), AdvancedVendingUIMessage.class,
                AdvancedVendingUIMessage::encode,
                AdvancedVendingUIMessage::decode,
                AdvancedVendingUIMessage::handle);

        CHANNEL.registerMessage(nextId(), VaultEscapeMessage.class,
                VaultEscapeMessage::encode,
                VaultEscapeMessage::decode,
                VaultEscapeMessage::handle);

        CHANNEL.registerMessage(nextId(), RenameUIMessage.class,
                RenameUIMessage::encode,
                RenameUIMessage::decode,
                RenameUIMessage::handle);

        CHANNEL.registerMessage(nextId(), VaultInfoMessage.class,
                VaultInfoMessage::encode,
                VaultInfoMessage::decode,
                VaultInfoMessage::handle);

        CHANNEL.registerMessage(nextId(), StepHeightMessage.class,
                StepHeightMessage::encode,
                StepHeightMessage::decode,
                StepHeightMessage::handle);

        CHANNEL.registerMessage(nextId(), AttackOffHandMessage.class,
                AttackOffHandMessage::encode,
                AttackOffHandMessage::decode,
                AttackOffHandMessage::handle);
    }

    public static int nextId() {
        return ID++;
    }

}

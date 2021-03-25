package iskallia.vault.network.message;

import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

// From Server to Client
// "Hey dude, your new vault level info is like dis!"
public class VaultLevelMessage {

    public int vaultLevel;
    public int vaultExp, tnl;
    public int unspentSkillPoints;
    public int unspentKnowledgePoints;

    public VaultLevelMessage() { }

    public VaultLevelMessage(int vaultLevel, int vaultExp, int tnl, int unspentSkillPoints, int unspentKnowledgePoints) {
        this.vaultLevel = vaultLevel;
        this.vaultExp = vaultExp;
        this.tnl = tnl;
        this.unspentSkillPoints = unspentSkillPoints;
        this.unspentKnowledgePoints = unspentKnowledgePoints;
    }

    public static void encode(VaultLevelMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.vaultLevel);
        buffer.writeInt(message.vaultExp);
        buffer.writeInt(message.tnl);
        buffer.writeInt(message.unspentSkillPoints);
        buffer.writeInt(message.unspentKnowledgePoints);
    }

    public static VaultLevelMessage decode(PacketBuffer buffer) {
        VaultLevelMessage message = new VaultLevelMessage();
        message.vaultLevel = buffer.readInt();
        message.vaultExp = buffer.readInt();
        message.tnl = buffer.readInt();
        message.unspentSkillPoints = buffer.readInt();
        message.unspentKnowledgePoints = buffer.readInt();
        return message;
    }

    public static void handle(VaultLevelMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            VaultBarOverlay.vaultLevel = message.vaultLevel;
            VaultBarOverlay.vaultExp = message.vaultExp;
            VaultBarOverlay.tnl = message.tnl;
            VaultBarOverlay.unspentSkillPoints = message.unspentSkillPoints;
            VaultBarOverlay.unspentKnowledgePoints = message.unspentKnowledgePoints;

            VaultBarOverlay.expGainedAnimation.reset();
            VaultBarOverlay.expGainedAnimation.play();

            Screen currentScreen = Minecraft.getInstance().screen;
            if (currentScreen instanceof SkillTreeScreen) {
                SkillTreeScreen skillTreeScreen = (SkillTreeScreen) currentScreen;
                skillTreeScreen.refreshWidgets();
            }
        });
        context.setPacketHandled(true);
    }

}

package iskallia.vault.network.message;

import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.StageManager;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

// From Server to Client
// "Hey dude, dis is your new Research Tree"
public class ResearchTreeMessage {

    public ResearchTree researchTree;
    public UUID playerUUID;

    public ResearchTreeMessage() { }

    public ResearchTreeMessage(ResearchTree researchTree, UUID playerUUID) {
        this.researchTree = researchTree;
        this.playerUUID = playerUUID;
    }

    public static void encode(ResearchTreeMessage message, PacketBuffer buffer) {
        buffer.writeUUID(message.playerUUID);
        buffer.writeNbt(message.researchTree.serializeNBT());
    }

    public static ResearchTreeMessage decode(PacketBuffer buffer) {
        ResearchTreeMessage message = new ResearchTreeMessage();
        message.researchTree = new ResearchTree(buffer.readUUID());
        message.researchTree.deserializeNBT(Objects.requireNonNull(buffer.readNbt()));
        return message;
    }

    public static void handle(ResearchTreeMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            StageManager.RESEARCH_TREE = message.researchTree;
        });
        context.setPacketHandled(true);
    }

}

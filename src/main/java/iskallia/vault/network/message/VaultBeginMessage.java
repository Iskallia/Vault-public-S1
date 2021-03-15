package iskallia.vault.network.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class VaultBeginMessage {

    public boolean cannotExit;

    public VaultBeginMessage() { }

    public VaultBeginMessage(boolean cannotExit) {
        this.cannotExit = cannotExit;
    }

    public static void encode(VaultBeginMessage message, PacketBuffer buffer) {
        buffer.writeBoolean(message.cannotExit);
    }

    public static VaultBeginMessage decode(PacketBuffer buffer) {
        VaultBeginMessage message = new VaultBeginMessage();
        message.cannotExit = buffer.readBoolean();
        return message;
    }

    public static void handle(VaultBeginMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {});
        context.setPacketHandled(true);
    }

}

package iskallia.vault.network.message;

import iskallia.vault.client.gui.overlay.VaultRaidOverlay;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

// From Server to Client
// "Yo buddy, dis many ticks are remaining."
public class VaultRaidTickMessage {

    public int remainingTicks;

    public VaultRaidTickMessage() { }

    public VaultRaidTickMessage(int remainingTicks) {
        this.remainingTicks = remainingTicks;
    }

    public static void encode(VaultRaidTickMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.remainingTicks);
    }

    public static VaultRaidTickMessage decode(PacketBuffer buffer) {
        VaultRaidTickMessage message = new VaultRaidTickMessage();
        message.remainingTicks = buffer.readInt();
        return message;
    }

    public static void handle(VaultRaidTickMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            VaultRaidOverlay.remainingTicks = message.remainingTicks;
        });
        context.setPacketHandled(true);
    }

}

package iskallia.vault.network.message;

import iskallia.vault.client.gui.overlay.AbilitiesOverlay;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

// From Server to Client
// "Hey dude, you focused to dis ability now"
public class AbilityFocusMessage {

    public int focusedIndex;

    public AbilityFocusMessage() { }

    public AbilityFocusMessage(int focusedIndex) {
        this.focusedIndex = focusedIndex;
    }

    public static void encode(AbilityFocusMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.focusedIndex);
    }

    public static AbilityFocusMessage decode(PacketBuffer buffer) {
        AbilityFocusMessage message = new AbilityFocusMessage();
        message.focusedIndex = buffer.readInt();
        return message;
    }

    public static void handle(AbilityFocusMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            AbilitiesOverlay.focusedIndex = message.focusedIndex;
        });
        context.setPacketHandled(true);
    }

}

package iskallia.vault.network.message;

import iskallia.vault.client.gui.overlay.AbilitiesOverlay;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

// From Server to Client
// "Hey dude, dis ability of yours has dis much cooldown and dis activity state"
public class AbilityActivityMessage {

    public int abilityIndex;
    public int cooldownTicks;
    public int activeFlag;

    public AbilityActivityMessage() { }

    // Don't mind Goodie being lazy...
    // Active flag:
    // 0 - ignore me
    // 1 - current ability deactivated
    // 2 - current ability activated
    public AbilityActivityMessage(int abilityIndex, int cooldownTicks, int activeFlag) {
        this.cooldownTicks = cooldownTicks;
        this.activeFlag = activeFlag;
        this.abilityIndex = abilityIndex;
    }

    public static void encode(AbilityActivityMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.abilityIndex);
        buffer.writeInt(message.cooldownTicks);
        buffer.writeInt(message.activeFlag);
    }

    public static AbilityActivityMessage decode(PacketBuffer buffer) {
        AbilityActivityMessage message = new AbilityActivityMessage();
        message.abilityIndex = buffer.readInt();
        message.cooldownTicks = buffer.readInt();
        message.activeFlag = buffer.readInt();
        return message;
    }

    public static void handle(AbilityActivityMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            AbilitiesOverlay.cooldowns.put(message.abilityIndex, message.cooldownTicks);
            if (message.activeFlag != 0) {
                AbilitiesOverlay.active = message.activeFlag != 1;
            }
        });
        context.setPacketHandled(true);
    }

}

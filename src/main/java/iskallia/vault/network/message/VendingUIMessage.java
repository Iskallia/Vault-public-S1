package iskallia.vault.network.message;

import iskallia.vault.container.GlobalTraderContainer;
import iskallia.vault.container.VendingMachineContainer;
import iskallia.vault.world.data.GlobalTraderData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

// From Client to Server
// "Hey I have a thingy related to the Vending UI"
public class VendingUIMessage {

    public enum Opcode {
        SELECT_TRADE,
        EJECT_CORE
    }

    public Opcode opcode;
    public CompoundNBT payload;

    public VendingUIMessage() {
    }

    public static void encode(VendingUIMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.opcode.ordinal());
        buffer.writeNbt(message.payload);
    }

    public static VendingUIMessage decode(PacketBuffer buffer) {
        VendingUIMessage message = new VendingUIMessage();
        message.opcode = VendingUIMessage.Opcode.values()[buffer.readInt()];
        message.payload = buffer.readNbt();
        return message;
    }

    public static void handle(VendingUIMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (message.opcode == Opcode.SELECT_TRADE) {
                int index = message.payload.getInt("Index");
                ServerPlayerEntity sender = context.getSender();
                Container openContainer = sender.containerMenu;
                if (openContainer instanceof VendingMachineContainer) {
                    VendingMachineContainer vendingMachineContainer = (VendingMachineContainer) openContainer;
                    vendingMachineContainer.selectTrade(index);
                } else if (openContainer instanceof GlobalTraderContainer) {
                    GlobalTraderContainer globalTraderContainer = (GlobalTraderContainer) openContainer;
                    globalTraderContainer.selectTrade(index);
                    GlobalTraderData.get((ServerWorld) sender.level).setDirty();
                }

            } else if (message.opcode == Opcode.EJECT_CORE) {
                int index = message.payload.getInt("Index");
                ServerPlayerEntity sender = context.getSender();
                Container openContainer = sender.containerMenu;
                if (openContainer instanceof VendingMachineContainer) {
                    VendingMachineContainer vendingMachineContainer = (VendingMachineContainer) openContainer;
                    vendingMachineContainer.ejectCore(index);
                }
            }
        });
        context.setPacketHandled(true);
    }

    public static VendingUIMessage selectTrade(int index) {
        VendingUIMessage message = new VendingUIMessage();
        message.opcode = Opcode.SELECT_TRADE;
        message.payload = new CompoundNBT();
        message.payload.putInt("Index", index);
        return message;
    }

    public static VendingUIMessage ejectTrade(int index) {
        VendingUIMessage message = new VendingUIMessage();
        message.opcode = Opcode.EJECT_CORE;
        message.payload = new CompoundNBT();
        message.payload.putInt("Index", index);
        return message;
    }

}

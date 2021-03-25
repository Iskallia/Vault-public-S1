package iskallia.vault.network.message.base;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.function.Consumer;

public class OpcodeMessage<OPC extends Enum<OPC>> {

    public OPC opcode;
    public CompoundNBT payload;

    public void encodeSelf(OpcodeMessage<OPC> message, PacketBuffer buffer) {
        buffer.writeInt(message.opcode.ordinal());
        buffer.writeNbt(message.payload);
    }

    public void decodeSelf(PacketBuffer buffer, Class<OPC> enumClass) {
        this.opcode = enumClass.getEnumConstants()[buffer.readInt()];
        this.payload = buffer.readNbt();
    }

    public static <O extends Enum<O>, T extends OpcodeMessage<O>>
    T composeMessage(T message, O opcode, Consumer<CompoundNBT> payloadSerializer) {
        message.opcode = opcode;
        CompoundNBT payload = new CompoundNBT();
        payloadSerializer.accept(payload);
        message.payload = payload;
        return message;
    }

}

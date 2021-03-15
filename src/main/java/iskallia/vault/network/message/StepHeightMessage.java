package iskallia.vault.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class StepHeightMessage {

	public float stepHeight;

	protected StepHeightMessage() { }

	public StepHeightMessage(float stepHeight) {
		this.stepHeight = stepHeight;
	}

	public static void encode(StepHeightMessage message, PacketBuffer buffer) {
		buffer.writeFloat(message.stepHeight);
	}

	public static StepHeightMessage decode(PacketBuffer buffer) {
		StepHeightMessage message = new StepHeightMessage();
		message.stepHeight = buffer.readFloat();
		return message;
	}

	public static void handle(StepHeightMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();

		context.enqueueWork(() -> {
			if(Minecraft.getInstance().player != null) {
				Minecraft.getInstance().player.stepHeight = message.stepHeight;
			}
		});

		context.setPacketHandled(true);
	}

}

package iskallia.vault.network.message;

import iskallia.vault.entity.EternalEntity;
import iskallia.vault.entity.FighterEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class FighterSizeMessage {

	private int entityId;
	private float size;

	public FighterSizeMessage() { }

	public FighterSizeMessage(int entityId, float size) {
		this.entityId = entityId;
		this.size = size;
	}

	public FighterSizeMessage(Entity entity, float size) {
		this(entity.getEntityId(), size);
	}

	public static void encode(FighterSizeMessage message, PacketBuffer buffer) {
		buffer.writeInt(message.entityId);
		buffer.writeFloat(message.size);
	}

	public static FighterSizeMessage decode(PacketBuffer buffer) {
		FighterSizeMessage message = new FighterSizeMessage();
		message.entityId = buffer.readInt();
		message.size = buffer.readFloat();
		return message;
	}

	public static void handle(FighterSizeMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();

		context.enqueueWork(() -> {
			Minecraft minecraft = Minecraft.getInstance();
			ClientPlayerEntity player = minecraft.player;
			World world = player.world;
			Entity entity = world.getEntityByID(message.entityId);
			if(entity == null || !entity.isAlive())return;
			if(entity instanceof FighterEntity)((FighterEntity)entity).changeSize(message.size);
			if(entity instanceof EternalEntity)((EternalEntity)entity).changeSize(message.size);
		});

		context.setPacketHandled(true);
	}

}

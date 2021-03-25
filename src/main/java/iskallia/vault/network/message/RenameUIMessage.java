package iskallia.vault.network.message;

import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.block.entity.PlayerStatueTileEntity;
import iskallia.vault.util.RenameType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class RenameUIMessage {

    public RenameType renameType;
    public CompoundNBT payload;

    public RenameUIMessage() { }

    public static void encode(RenameUIMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.renameType.ordinal());
        buffer.writeNbt(message.payload);
    }

    public static RenameUIMessage decode(PacketBuffer buffer) {
        RenameUIMessage message = new RenameUIMessage();
        message.renameType = RenameType.values()[buffer.readInt()];
        message.payload = buffer.readNbt();
        return message;
    }

    public static void handle(RenameUIMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            CompoundNBT data = message.payload.getCompound("Data");
            ServerPlayerEntity sender = context.getSender();
            if (message.renameType == RenameType.PLAYER_STATUE) {
                BlockPos statuePos = new BlockPos(data.getInt("x"), data.getInt("y"), data.getInt("z"));
                TileEntity te = sender.getCommandSenderWorld().getBlockEntity(statuePos);
                if (te instanceof PlayerStatueTileEntity) {
                    PlayerStatueTileEntity statue = (PlayerStatueTileEntity) te;
                    statue.getSkin().updateSkin(data.getString("PlayerNickname"));
                    statue.sendUpdates();
                } else if (te instanceof LootStatueTileEntity) {
                    LootStatueTileEntity statue = (LootStatueTileEntity) te;
                    statue.getSkin().updateSkin(data.getString("PlayerNickname"));
                    statue.sendUpdates();
                }
            } else if (message.renameType == RenameType.TRADER_CORE) {
                sender.inventory.items.set(sender.inventory.selected, ItemStack.of(data));
            } else if(message.renameType == RenameType.CRYO_CHAMBER) {
                BlockPos pos = NBTUtil.readBlockPos(data.getCompound("BlockPos"));
                String name = data.getString("EternalName");
                TileEntity te = sender.getCommandSenderWorld().getBlockEntity(pos);
                if (te instanceof CryoChamberTileEntity) {
                    CryoChamberTileEntity chamber = (CryoChamberTileEntity) te;
                    chamber.renameEternal(name);
                    chamber.getSkin().updateSkin(name);
                    chamber.sendUpdates();
                }
            }
        });
        context.setPacketHandled(true);
    }

    public static RenameUIMessage updateName(RenameType type, CompoundNBT nbt) {
        RenameUIMessage message = new RenameUIMessage();
        message.renameType = type;
        message.payload = nbt;
        return message;
    }

}

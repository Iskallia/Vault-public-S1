package iskallia.vault.network.message;

import iskallia.vault.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class VaultEscapeMessage {

    public VaultEscapeMessage() { }

    public static void encode(VaultEscapeMessage message, PacketBuffer buffer) { }

    public static VaultEscapeMessage decode(PacketBuffer buffer) {
        return new VaultEscapeMessage();
    }

    public static void handle(VaultEscapeMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            playEscapeSound();
        });
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playEscapeSound() {
        Minecraft minecraft = Minecraft.getInstance();
        SoundHandler soundHandler = minecraft.getSoundManager();
        soundHandler.play(SimpleSound.forUI(ModSounds.VAULT_PORTAL_LEAVE, 1f, 1f));
    }

}

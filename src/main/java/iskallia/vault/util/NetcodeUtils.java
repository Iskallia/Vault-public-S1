package iskallia.vault.util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;
import java.util.function.Consumer;

public class NetcodeUtils {

    public static boolean runIfPresent(MinecraftServer server, UUID uuid, Consumer<ServerPlayerEntity> action) {
        if (server == null)
            return false;

        ServerPlayerEntity player = server.getPlayerList().getPlayer(uuid);

        if (player == null)
            return false;

        action.accept(player);

        return true;
    }

}

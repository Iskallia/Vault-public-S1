package iskallia.vault.event;

import iskallia.vault.Vault;
import iskallia.vault.init.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SetupEvents {

    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event) {
        Vault.LOGGER.info("setupClient()");
        ModScreens.register(event);
        ModScreens.registerOverlays();
        ModKeybinds.register(event);
        ModEntities.Renderers.register(event);
        MinecraftForge.EVENT_BUS.register(InputEvents.class);
        ModBlocks.registerTileEntityRenderers();
    }

    @SubscribeEvent
    public static void setupCommon(final FMLCommonSetupEvent event) {
        Vault.LOGGER.info("setupCommon()");
        ModConfigs.register();
        ModNetwork.initialize();
    }

    @SubscribeEvent
    public static void setupDedicatedServer(final FMLDedicatedServerSetupEvent event) {
        Vault.LOGGER.info("setupDedicatedServer()");
    }

}

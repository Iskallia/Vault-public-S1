package iskallia.vault;

import iskallia.vault.init.ModCommands;
import iskallia.vault.init.ModFeatures;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Vault.MOD_ID)
public class Vault {

    public static final String MOD_ID = "the_vault";
    public static final Logger LOGGER = LogManager.getLogger();

    public static RegistryKey<World> VAULT_KEY = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, Vault.id("vault"));

    public Vault() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::onCommandRegister);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onBiomeLoad);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::onPlayerLoggedIn);
    }
    
    public void onCommandRegister(RegisterCommandsEvent event) {
        ModCommands.registerCommands(event.getDispatcher(), event.getEnvironment());
    }

    public void onBiomeLoad(BiomeLoadingEvent event) {
        if (event.getName().equals(Vault.id("spoopy"))) {
            event.getGeneration()
                    .withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModFeatures.VAULT_ORE)
                    .withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModFeatures.BREADCRUMB_CHEST);
        }

        event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ModFeatures.VAULT_ROCK_ORE);
    }

    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        ServerWorld serverWorld = player.getServerWorld();
        MinecraftServer server = player.getServer();
        PlayerVaultStatsData.get(serverWorld).getVaultStats(player).sync(server);
        PlayerResearchesData.get(serverWorld).getResearches(player).sync(server);
        PlayerAbilitiesData.get(serverWorld).getAbilities(player).sync(server);
    }


    public static String sId(String name) {
        return MOD_ID + ":" + name;
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

}
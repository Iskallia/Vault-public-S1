package iskallia.vault.init;

import iskallia.vault.client.gui.overlay.*;
import iskallia.vault.client.gui.screen.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ModScreens {

    public static void register(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModContainers.SKILL_TREE_CONTAINER, SkillTreeScreen::new);
        ScreenManager.registerFactory(ModContainers.VAULT_CRATE_CONTAINER, VaultCrateScreen::new);
        ScreenManager.registerFactory(ModContainers.VENDING_MACHINE_CONTAINER, VendingMachineScreen::new);
        ScreenManager.registerFactory(ModContainers.ADVANCED_VENDING_MACHINE_CONTAINER, AdvancedVendingMachineScreen::new);
        ScreenManager.registerFactory(ModContainers.RENAMING_CONTAINER, RenameScreen::new);
        ScreenManager.registerFactory(ModContainers.KEY_PRESS_CONTAINER, KeyPressScreen::new);
        ScreenManager.registerFactory(ModContainers.TRADER_CONTAINER, GlobalTraderScreen::new);
    }

    public static void registerOverlays() {
        MinecraftForge.EVENT_BUS.register(VaultBarOverlay.class);
        MinecraftForge.EVENT_BUS.register(AbilitiesOverlay.class);
        MinecraftForge.EVENT_BUS.register(AbilityVignetteOverlay.class);
        MinecraftForge.EVENT_BUS.register(VaultRaidOverlay.class);
        MinecraftForge.EVENT_BUS.register(GiftBombOverlay.class);
    }

}

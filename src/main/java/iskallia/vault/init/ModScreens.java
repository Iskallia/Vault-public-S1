package iskallia.vault.init;

import iskallia.vault.client.gui.overlay.*;
import iskallia.vault.client.gui.screen.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ModScreens {

    public static void register(final FMLClientSetupEvent event) {
        ScreenManager.register(ModContainers.SKILL_TREE_CONTAINER, SkillTreeScreen::new);
        ScreenManager.register(ModContainers.VAULT_CRATE_CONTAINER, VaultCrateScreen::new);
        ScreenManager.register(ModContainers.VENDING_MACHINE_CONTAINER, VendingMachineScreen::new);
        ScreenManager.register(ModContainers.ADVANCED_VENDING_MACHINE_CONTAINER, AdvancedVendingMachineScreen::new);
        ScreenManager.register(ModContainers.RENAMING_CONTAINER, RenameScreen::new);
        ScreenManager.register(ModContainers.KEY_PRESS_CONTAINER, KeyPressScreen::new);
        ScreenManager.register(ModContainers.TRADER_CONTAINER, GlobalTraderScreen::new);
    }

    public static void registerOverlays() {
        MinecraftForge.EVENT_BUS.register(VaultBarOverlay.class);
        MinecraftForge.EVENT_BUS.register(AbilitiesOverlay.class);
        MinecraftForge.EVENT_BUS.register(AbilityVignetteOverlay.class);
        MinecraftForge.EVENT_BUS.register(VaultRaidOverlay.class);
        MinecraftForge.EVENT_BUS.register(GiftBombOverlay.class);
    }

}

package iskallia.vault.init;

import iskallia.vault.Vault;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.awt.event.KeyEvent;

@OnlyIn(Dist.CLIENT)
public class ModKeybinds {

    public static KeyBinding openAbilityTree;
    public static KeyBinding abilityKey;
    public static KeyBinding abilityWheelKey;

    public static void register(final FMLClientSetupEvent event) {
        openAbilityTree = createKeyBinding("open_ability_tree", KeyEvent.VK_H);
        abilityKey = createKeyBinding("ability_key", KeyEvent.VK_G);
        abilityWheelKey = createKeyBinding("ability_wheel_key", 342); // --> Supposed to be R_ALT

        ClientRegistry.registerKeyBinding(openAbilityTree);
        ClientRegistry.registerKeyBinding(abilityKey);
        ClientRegistry.registerKeyBinding(abilityWheelKey);
    }

    private static KeyBinding createKeyBinding(String name, int key) {
        return new KeyBinding(
                "key." + Vault.MOD_ID + "." + name,
                key,
                "key.category." + Vault.MOD_ID
        );
    }
}

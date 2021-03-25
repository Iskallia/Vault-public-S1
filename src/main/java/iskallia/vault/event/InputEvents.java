package iskallia.vault.event;

import iskallia.vault.client.gui.overlay.AbilitiesOverlay;
import iskallia.vault.client.gui.screen.AbilitySelectionScreen;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AbilityKeyMessage;
import iskallia.vault.network.message.OpenSkillTreeMessage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class InputEvents {

    private static boolean isShiftDown;

    public static boolean isShiftDown() {
        return isShiftDown;
    }

    @SubscribeEvent
    public static void onShiftKey(InputEvent.KeyInputEvent event) {
        if (event.getKey() == GLFW.GLFW_KEY_LEFT_SHIFT) {
            if (event.getAction() == GLFW.GLFW_PRESS) {
                isShiftDown = true;
            } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                isShiftDown = false;
            }
        }
    }

    @SubscribeEvent
    public static void onKey(InputEvent.KeyInputEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        onInput(minecraft, event.getKey(), event.getAction());
    }

    @SubscribeEvent
    public static void onMouse(InputEvent.MouseInputEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        onInput(minecraft, event.getButton(), event.getAction());
    }

    private static void onInput(Minecraft minecraft, int key, int action) {
        if (minecraft.screen == null && ModKeybinds.abilityWheelKey.getKey().getValue() == key) {
            if (action != GLFW.GLFW_PRESS) return;
            if (AbilitiesOverlay.learnedAbilities.size() <= 2) return;
            minecraft.setScreen(new AbilitySelectionScreen());
            ModNetwork.CHANNEL.sendToServer(new AbilityKeyMessage(true));

        } else if (minecraft.screen == null && ModKeybinds.openAbilityTree.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(new OpenSkillTreeMessage());

        } else if (minecraft.screen == null && ModKeybinds.abilityKey.getKey().getValue() == key) {
            if (action == GLFW.GLFW_RELEASE) {
                ModNetwork.CHANNEL.sendToServer(new AbilityKeyMessage(true, false, false, false));

            } else if (action == GLFW.GLFW_PRESS) {
                ModNetwork.CHANNEL.sendToServer(new AbilityKeyMessage(false, true, false, false));
            }
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollEvent event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) return;

        double scrollDelta = event.getScrollDelta();

        if (ModKeybinds.abilityKey.isDown()) {
            if (minecraft.screen == null) {
                if (scrollDelta < 0) {
                    ModNetwork.CHANNEL.sendToServer(new AbilityKeyMessage(false, false, false, true));

                } else {
                    ModNetwork.CHANNEL.sendToServer(new AbilityKeyMessage(false, false, true, false));
                }
            }
            event.setCanceled(true);
        }
    }

}

package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.effect.ExecuteEffect;
import iskallia.vault.effect.GhostWalkEffect;
import iskallia.vault.effect.RampageEffect;
import iskallia.vault.effect.TankEffect;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.RegistryEvent;

import java.awt.*;

public class ModEffects {

    public static final Effect GHOST_WALK = new GhostWalkEffect(EffectType.BENEFICIAL, Color.GRAY.getRGB(), Vault.id("ghost_walk"));
    public static final Effect RAMPAGE = new RampageEffect(EffectType.BENEFICIAL, Color.RED.getRGB(), Vault.id("rampage"));
    public static final Effect TANK = new TankEffect(EffectType.BENEFICIAL, Color.WHITE.getRGB(), Vault.id("tank"));
    public static final Effect EXECUTE = new ExecuteEffect(EffectType.BENEFICIAL, Color.YELLOW.getRGB(), Vault.id("execute"));

    public static void register(RegistryEvent.Register<Effect> event) {
        register(GHOST_WALK, event);
        register(RAMPAGE, event);
        register(TANK, event);
        register(EXECUTE, event);
    }

    /* --------------------------------------------- */

    private static <T extends Effect> void register(T effect, RegistryEvent.Register<Effect> event) {
        event.getRegistry().register(effect);
    }

}

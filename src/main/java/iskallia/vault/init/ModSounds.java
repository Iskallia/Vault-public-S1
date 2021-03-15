package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.util.LazySoundType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;

public class ModSounds {

    public static SoundEvent GRASSHOPPER_BRRR;
    public static SoundEvent RAFFLE_SFX;
    public static SoundEvent VAULT_AMBIENT_LOOP;
    public static SoundEvent VAULT_AMBIENT;
    public static SoundEvent VAULT_BOSS_LOOP;
    public static SoundEvent TIMER_KILL_SFX;
    public static SoundEvent TIMER_PANIC_TICK_SFX;
    public static SoundEvent CONFETTI_SFX;
    public static SoundEvent MEGA_JUMP_SFX;
    public static SoundEvent DASH_SFX;
    public static SoundEvent VAULT_EXP_SFX;
    public static SoundEvent VAULT_LEVEL_UP_SFX;
    public static SoundEvent SKILL_TREE_LEARN_SFX;
    public static SoundEvent SKILL_TREE_UPGRADE_SFX;
    public static SoundEvent VENDING_MACHINE_SFX;
    public static SoundEvent ARENA_HORNS_SFX;
    public static SoundEvent BOOSTER_PACK_SUCCESS_SFX;
    public static SoundEvent BOOSTER_PACK_FAIL_SFX;
    public static SoundEvent GIFT_BOMB_SFX;
    public static SoundEvent GIFT_BOMB_GAIN_SFX;
    public static SoundEvent MEGA_GIFT_BOMB_GAIN_SFX;
    public static SoundEvent BOSS_TP_SFX;
    public static SoundEvent VAULT_GEM_HIT;
    public static SoundEvent VAULT_GEM_BREAK;
    public static SoundEvent ROBOT_HURT;
    public static SoundEvent ROBOT_DEATH;
    public static SoundEvent BOOGIE_AMBIENT;
    public static SoundEvent BOOGIE_HURT;
    public static SoundEvent BOOGIE_DEATH;
    public static SoundEvent VAULT_PORTAL_OPEN;
    public static SoundEvent VAULT_PORTAL_LEAVE;
    public static SoundEvent CLEANSE_SFX;
    public static SoundEvent GHOST_WALK_SFX;
    public static SoundEvent INVISIBILITY_SFX;
    public static SoundEvent NIGHT_VISION_SFX;
    public static SoundEvent RAMPAGE_SFX;
    public static SoundEvent VAMPIRE_HISSING_SFX;

    public static LazySoundType VAULT_GEM = new LazySoundType();

    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        GRASSHOPPER_BRRR = registerSound(event, "grasshopper_brrr");
        RAFFLE_SFX = registerSound(event, "raffle");
        VAULT_AMBIENT_LOOP = registerSound(event, "vault_ambient_loop");
        VAULT_AMBIENT = registerSound(event, "vault_ambient");
        VAULT_BOSS_LOOP = registerSound(event, "boss_loop");
        TIMER_KILL_SFX = registerSound(event, "timer_kill");
        TIMER_PANIC_TICK_SFX = registerSound(event, "timer_panic_tick");
        CONFETTI_SFX = registerSound(event, "confetti");
        MEGA_JUMP_SFX = registerSound(event, "mega_jump");
        DASH_SFX = registerSound(event, "dash");
        VAULT_EXP_SFX = registerSound(event, "vault_exp");
        VAULT_LEVEL_UP_SFX = registerSound(event, "vault_level_up");
        SKILL_TREE_LEARN_SFX = registerSound(event, "skill_tree_learn");
        SKILL_TREE_UPGRADE_SFX = registerSound(event, "skill_tree_upgrade");
        VENDING_MACHINE_SFX = registerSound(event, "vending_machine");
        ARENA_HORNS_SFX = registerSound(event, "arena_horns");
        BOOSTER_PACK_SUCCESS_SFX = registerSound(event, "booster_pack");
        BOOSTER_PACK_FAIL_SFX = registerSound(event, "booster_pack_fail");
        GIFT_BOMB_SFX = registerSound(event, "gift_bomb");
        GIFT_BOMB_GAIN_SFX = registerSound(event, "sub_bomb_gain");
        MEGA_GIFT_BOMB_GAIN_SFX = registerSound(event, "sub_bomb_gain_mega");
        BOSS_TP_SFX = registerSound(event, "boss_tp");
        VAULT_GEM_HIT = registerSound(event, "vault_gem_hit");
        VAULT_GEM_BREAK = registerSound(event, "vault_gem_break");
        ROBOT_HURT = registerSound(event, "robot_hurt");
        ROBOT_DEATH = registerSound(event, "robot_death");
        BOOGIE_AMBIENT = registerSound(event, "boogie_ambient");
        BOOGIE_HURT = registerSound(event, "boogie_hurt");
        BOOGIE_DEATH = registerSound(event, "boogie_death");
        VAULT_PORTAL_OPEN = registerSound(event, "vault_portal_open");
        VAULT_PORTAL_LEAVE = registerSound(event, "vault_portal_leave");
        CLEANSE_SFX = registerSound(event, "cleanse");
        GHOST_WALK_SFX = registerSound(event, "ghost_walk");
        INVISIBILITY_SFX = registerSound(event, "invisibility");
        NIGHT_VISION_SFX = registerSound(event, "night_vision");
        RAMPAGE_SFX = registerSound(event, "rampage");
        VAMPIRE_HISSING_SFX = registerSound(event, "vampire_hissing");
    }

    public static void registerSoundTypes() {
        VAULT_GEM.initialize(0.75f, 1f, VAULT_GEM_BREAK, null, null, VAULT_GEM_HIT, null);
    }

    /* ---------------------------- */

    private static SoundEvent registerSound(RegistryEvent.Register<SoundEvent> event, String soundName) {
        ResourceLocation location = Vault.id(soundName);
        SoundEvent soundEvent = new SoundEvent(location);
        soundEvent.setRegistryName(location);
        event.getRegistry().register(soundEvent);
        return soundEvent;
    }

}

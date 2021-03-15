package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GhostWalkAbility extends EffectAbility {

    @Expose private int durationTicks;

    public int getDurationTicks() {
        return durationTicks;
    }

    public GhostWalkAbility(int cost, Effect effect, int level, int durationTicks, Type type, Behavior behavior) {
        super(cost, effect, level, type, behavior);
        this.durationTicks = durationTicks;
    }

    @Override
    public void onTick(PlayerEntity player, boolean active) {

    }

    @Override
    public void onAction(PlayerEntity player, boolean active) {
        EffectInstance activeEffect = player.getActivePotionEffect(this.getEffect());
        EffectInstance newEffect = new EffectInstance(this.getEffect(),
                getDurationTicks(), this.getAmplifier(), false,
                this.getType().showParticles, this.getType().showIcon);

        if (activeEffect == null) {
            player.addPotionEffect(newEffect);
        }

        player.world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(),
                ModSounds.GHOST_WALK_SFX, SoundCategory.MASTER, 0.7f, 1f);
        player.playSound(ModSounds.GHOST_WALK_SFX, SoundCategory.MASTER, 0.7f, 1f);
    }

    @Override
    public void onBlur(PlayerEntity player) {} // Do not remove effect on blur

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        Entity e = event.getSource().getTrueSource();
        if (e instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) e;
            EffectInstance ghostWalk = living.getActivePotionEffect(ModEffects.GHOST_WALK);
            if (ghostWalk != null) {
                living.removePotionEffect(ModEffects.GHOST_WALK);
            }
        }
    }

}

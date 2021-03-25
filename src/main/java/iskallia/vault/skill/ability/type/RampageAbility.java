package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;

public class RampageAbility extends EffectAbility {

    @Expose private int durationTicks;
    @Expose private int damageIncrease;

    public int getDurationTicks() {
        return durationTicks;
    }

    public int getDamageIncrease() {
        return damageIncrease;
    }

    public RampageAbility(int cost, Effect effect, int level, int damageIncrease, int durationTicks, int cooldown, Type type, Behavior behavior) {
        super(cost, effect, level, type, behavior);
        this.damageIncrease = damageIncrease;
        this.durationTicks = durationTicks;
        this.cooldown = cooldown;
    }

    @Override
    public void onTick(PlayerEntity player, boolean active) {}

    @Override
    public void onAction(PlayerEntity player, boolean active) {
        EffectInstance activeEffect = player.getEffect(this.getEffect());
        EffectInstance newEffect = new EffectInstance(this.getEffect(),
                getDurationTicks(), this.getAmplifier(), false,
                this.getType().showParticles, this.getType().showIcon);

        if (activeEffect == null) {
            player.addEffect(newEffect);
        }

        player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.RAMPAGE_SFX, SoundCategory.MASTER, 0.7f * 0.25f, 1f);
        player.playNotifySound(ModSounds.RAMPAGE_SFX, SoundCategory.MASTER, 0.7f * 0.25f, 1f);
    }

    @Override
    public void onBlur(PlayerEntity player) {} // Do not remove effect on blur

}

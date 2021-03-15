package iskallia.vault.skill.ability.type;

import iskallia.vault.init.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;

import java.util.List;
import java.util.stream.Collectors;

public class CleanseAbility extends PlayerAbility {

    public CleanseAbility(int cost, int cooldown) {
        super(cost, Behavior.RELEASE_TO_PERFORM);
        this.cooldown = cooldown;
    }

    @Override
    public void onAction(PlayerEntity player, boolean active) {
        List<Effect> effects = player.getActivePotionEffects().stream()
                .map(EffectInstance::getPotion)
                .filter(effect -> !effect.isBeneficial())
                .collect(Collectors.toList());

        effects.forEach(player::removePotionEffect);

        player.world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(),
                ModSounds.CLEANSE_SFX, SoundCategory.MASTER, 0.7f, 1f);
        player.playSound(ModSounds.CLEANSE_SFX, SoundCategory.MASTER, 0.7f, 1f);
    }

}

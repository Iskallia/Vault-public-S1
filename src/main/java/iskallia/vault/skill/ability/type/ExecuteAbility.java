package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExecuteAbility extends EffectAbility {

    @Expose
    private float damageIncrease;

    public ExecuteAbility(int cost, Effect effect, int level, Type type, Behavior behavior, float damageIncrease) {
        super(cost, effect, level, type, behavior);
        this.damageIncrease = damageIncrease;
    }

    public float getDamageIncrease() {
        return this.damageIncrease;
    }

    @Override
    public void onAction(PlayerEntity player, boolean active) {
        EffectInstance activeEffect = player.getActivePotionEffect(this.getEffect());
        EffectInstance newEffect = new EffectInstance(this.getEffect(),
                Integer.MAX_VALUE, this.getAmplifier(), false,
                this.getType().showParticles, this.getType().showIcon);

        if (activeEffect == null) {
            player.addPotionEffect(newEffect);
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (event.getEntity().world.isRemote) return;
        if (!(event.getSource().getTrueSource() instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
        EffectInstance execute = player.getActivePotionEffect(ModEffects.EXECUTE);

        if(execute == null) return;

        float damageIncrease = ModConfigs.ABILITIES.EXECUTE.getAbility(execute.getAmplifier() + 1).getDamageIncrease();

        LivingEntity entity = event.getEntityLiving();
        float health = entity.getMaxHealth();
        float damage = health * damageIncrease + event.getAmount();

        event.setAmount(damage);

        player.getHeldItemMainhand().damageItem((int) damage, player, playerEntity -> {
        });

        player.world.playSound(
                null,
                player.getPosX(),
                player.getPosY(),
                player.getPosZ(),
                SoundEvents.ENTITY_EVOKER_CAST_SPELL,
                SoundCategory.MASTER,
                1F, 1F
        );

        player.removePotionEffect(ModEffects.EXECUTE);
    }

    @Override
    public void onTick(PlayerEntity player, boolean active) {

    }

    @Override
    public void onBlur(PlayerEntity player) {

    }
}

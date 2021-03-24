package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExecuteAbility extends EffectAbility {

    @Expose
    private float damageMultiplier;

    public ExecuteAbility(int cost, Effect effect, int level, Type type, Behavior behavior, float damageMultiplier) {
        super(cost, effect, level, type, behavior);
        this.damageMultiplier = damageMultiplier;
    }

    public float getDamageMultiplier() {
        return this.damageMultiplier;
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

        if (execute == null) return;

        float damageMultiplier = ModConfigs.ABILITIES.EXECUTE.getAbility(execute.getAmplifier() + 1).getDamageMultiplier();

        LivingEntity entity = event.getEntityLiving();
        float currentHealth = entity.getHealth();
        float health = entity.getMaxHealth();
        float damage = (health - currentHealth) * damageMultiplier;

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

//        String damageText = String.valueOf(Math.max(Math.round(damage), 1));
//        Vector3d offset = getOffset(player.getHorizontalFacing().rotateY());
//        for (int i = 0; i < damageText.length(); i++) {
//            char c = damageText.charAt(i);
//            ExecuteParticleData particleData = new ExecuteParticleData(String.valueOf(c));
//            ((ServerWorld) player.world).spawnParticle(particleData, entity.getPosX(), player.getPosY() + player.getEyeHeight(), entity.getPosZ(), 1, i * offset.x, 0, i * offset.z, 0);
//        }


        player.removePotionEffect(ModEffects.EXECUTE);
    }

    private static Vector3d getOffset(Direction direction) {

        switch (direction) {
            case SOUTH:
                return new Vector3d(0, 0, -1d);
            case WEST:
                return new Vector3d(-1d, 0, 0);
            case EAST:
                return new Vector3d(0, 0, 1d);
            default:
                return new Vector3d(1d, 0, 0);
        }
    }

    @Override
    public void onTick(PlayerEntity player, boolean active) {}

    @Override
    public void onBlur(PlayerEntity player) {}
}

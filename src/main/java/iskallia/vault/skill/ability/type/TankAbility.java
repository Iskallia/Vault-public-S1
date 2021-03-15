package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TankAbility extends EffectAbility {

    @Expose
    private int durationTicks;

    public TankAbility(int cost, Effect effect, int level, int durationTicks, Type type, Behavior behavior) {
        super(cost, effect, level, type, behavior);
        this.durationTicks = durationTicks;
    }

    @Override
    public void onTick(PlayerEntity player, boolean active) {
    }

    public int getDurationTicks() {
        return this.durationTicks;
    }

    @Override
    public void onAction(PlayerEntity player, boolean active) {

        EffectInstance activeEffect = player.getActivePotionEffect(this.getEffect());
        EffectInstance newEffect = new EffectInstance(ModEffects.TANK,
                this.getDurationTicks(), this.getAmplifier(), false,
                this.getType().showParticles, this.getType().showIcon);

        if (activeEffect == null) {
            player.addPotionEffect(newEffect);
        }

//        player.world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(),
//                ModSounds.GHOST_WALK_SFX, SoundCategory.MASTER, 0.7f, 1f);
//        player.playSound(ModSounds.GHOST_WALK_SFX, SoundCategory.MASTER, 0.7f, 1f);
    }

    @Override
    public void onBlur(PlayerEntity player) {
    }


    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntityLiving();
        EffectInstance tank = entity.getActivePotionEffect(ModEffects.TANK);
        if (tank == null) return;

        float reduction = (float) (tank.getAmplifier() + 1) * 0.1f; //TODO Extract reduction percentage to config
        event.setAmount(event.getAmount() - (event.getAmount() * reduction));
    }

    public static HashMap<PlayerEntity, Vector3d> prevPositions = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.side == LogicalSide.CLIENT) return;

        PlayerEntity player = event.player;
        EffectInstance tank = player.getActivePotionEffect(ModEffects.TANK);

        if (tank != null) {
            double multiplier = (tank == null ? 1 : 1 - Math.abs((50D - ((double) tank.getAmplifier() * 5D)) * .01D));

            Vector3d currentPos = player.getPositionVec();
            Vector3d prevPos = prevPositions.get(player) == null ? player.getPositionVec() : prevPositions.get(player);

            Vector3d direction = new Vector3d(
                    prevPos.getX() - currentPos.getX(),
                    prevPos.getY() - currentPos.getY(),
                    prevPos.getZ() - currentPos.getZ()
            );

            player.setMotion(
                    direction.getX() * -multiplier,
                    player.getMotion().getY(),
                    direction.getZ() * -multiplier

            );
            player.velocityChanged = true;
        }
        prevPositions.put(player, player.getPositionVec());
    }
}



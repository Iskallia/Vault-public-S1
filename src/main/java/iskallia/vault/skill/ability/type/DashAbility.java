package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.easteregg.GrasshopperNinja;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.MathUtilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class DashAbility extends PlayerAbility {

    @Expose private final int extraRadius;

    public DashAbility(int cost, int extraRadius) {
        super(cost, Behavior.RELEASE_TO_PERFORM);
        this.extraRadius = extraRadius;
    }

    public int getExtraRadius() {
        return extraRadius;
    }

    @Override
    public void onAction(PlayerEntity player, boolean active) {
        Vector3d lookVector = player.getLookAngle();

        double magnitude = (10 + extraRadius) * 0.15;
        double extraPitch = 10;

        Vector3d dashVector = new Vector3d(
                lookVector.x(),
                lookVector.y(),
                lookVector.z()
        );

        float initialYaw = (float) MathUtilities.extractYaw(dashVector);

        dashVector = MathUtilities.rotateYaw(dashVector, initialYaw);

        double dashPitch = Math.toDegrees(MathUtilities.extractPitch(dashVector));

        if (dashPitch + extraPitch > 90) {
            dashVector = new Vector3d(0, 1, 0);
            dashPitch = 90;
        } else {
            dashVector = MathUtilities.rotateRoll(dashVector, (float) Math.toRadians(-extraPitch));
            dashVector = MathUtilities.rotateYaw(dashVector, -initialYaw);
            dashVector = dashVector.normalize();
        }

        double coef = 1.6 - MathUtilities.map(Math.abs(dashPitch),
                0.0d, 90.0d,
                0.6, 1.0d);

        dashVector = dashVector.scale(magnitude * coef);

        player.push(
                dashVector.x(),
                dashVector.y(),
                dashVector.z()
        );

        player.hurtMarked = true;

        ((ServerWorld) player.level).sendParticles(ParticleTypes.POOF,
                player.getX(), player.getY(), player.getZ(),
                50, 1D, 0.5D, 1D, 0.0D);

        if (GrasshopperNinja.isGrasshopperShape(player)) {
            player.level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    ModSounds.GRASSHOPPER_BRRR, SoundCategory.MASTER, 1f, 1f);
            player.playNotifySound(ModSounds.GRASSHOPPER_BRRR, SoundCategory.MASTER, 1f, 1f);
            GrasshopperNinja.achieve((ServerPlayerEntity) player);

        } else {
            player.level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    ModSounds.DASH_SFX, SoundCategory.MASTER, 1f, 1f);
            player.playNotifySound(ModSounds.DASH_SFX, SoundCategory.MASTER, 1f, 1f);
        }

    }

}

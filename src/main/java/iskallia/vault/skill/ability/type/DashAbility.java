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
        Vector3d lookVector = player.getLookVec();

        double magnitude = (10 + extraRadius) * 0.15;
        double extraPitch = 10;

        Vector3d dashVector = new Vector3d(
                lookVector.getX(),
                lookVector.getY(),
                lookVector.getZ()
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

        player.addVelocity(
                dashVector.getX(),
                dashVector.getY(),
                dashVector.getZ()
        );

        player.velocityChanged = true;

        ((ServerWorld) player.world).spawnParticle(ParticleTypes.POOF,
                player.getPosX(), player.getPosY(), player.getPosZ(),
                50, 1D, 0.5D, 1D, 0.0D);

        if (GrasshopperNinja.isGrasshopperShape(player)) {
            player.world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(),
                    ModSounds.GRASSHOPPER_BRRR, SoundCategory.MASTER, 1f, 1f);
            player.playSound(ModSounds.GRASSHOPPER_BRRR, SoundCategory.MASTER, 1f, 1f);
            GrasshopperNinja.achieve((ServerPlayerEntity) player);

        } else {
            player.world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(),
                    ModSounds.DASH_SFX, SoundCategory.MASTER, 1f, 1f);
            player.playSound(ModSounds.DASH_SFX, SoundCategory.MASTER, 1f, 1f);
        }

    }

}

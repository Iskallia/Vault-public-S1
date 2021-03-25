package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class MegaJumpAbility extends PlayerAbility {

    @Expose private final int extraHeight;

    public MegaJumpAbility(int cost, int extraHeight) {
        super(cost, Behavior.RELEASE_TO_PERFORM);
        this.extraHeight = extraHeight;
    }

    public int getExtraHeight() {
        return extraHeight;
    }

    @Override
    public void onAction(PlayerEntity player, boolean active) {
        double magnitude = (10 + extraHeight) * 0.15;

        Vector3d jumpVector = new Vector3d(0, 1, 0);

        jumpVector = jumpVector.scale(magnitude);

        player.push(
                jumpVector.x(),
                jumpVector.y(),
                jumpVector.z()
        );

        player.startFallFlying();

        player.hurtMarked = true;

        player.playNotifySound(ModSounds.MEGA_JUMP_SFX, SoundCategory.MASTER, 0.7f, 1f);
        ((ServerWorld) player.level).sendParticles(ParticleTypes.POOF,
                player.getX(), player.getY(), player.getZ(),
                50, 1D, 0.5D, 1D, 0.0D);
    }

}

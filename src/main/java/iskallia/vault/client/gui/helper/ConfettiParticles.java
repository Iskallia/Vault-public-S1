package iskallia.vault.client.gui.helper;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.vector.Vector3d;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ConfettiParticles {

    public static final int[] PARTICLE_COLORS = {
            0xFF_be1ed9, 0xFF_cf224e,
            0xFF_1ec621, 0xFF_463fd3,
            0xFF_7c454e, 0xFF_e2950f,
            0xFF_ec3423, 0xFF_f1d00c
    };

    protected Random random;

    protected float angleMin, angleMax;
    protected float speedMin, speedMax;
    protected int delayMin, delayMax;
    protected int lifetimeMin, lifetimeMax;
    protected int sizeMin, sizeMax;
    protected int quantityMin, quantityMax;
    protected Vector3d spawnerPos;

    protected List<ConfettiParticle> particles;

    public ConfettiParticles() {
        this.random = new Random();
        this.particles = new LinkedList<>();
        this.spawnerPos = new Vector3d(0, 0, 0);
    }

    public ConfettiParticles angleRange(float min, float max) {
        this.angleMin = (float) Math.toRadians(min);
        this.angleMax = (float) Math.toRadians(max);
        return this;
    }

    public ConfettiParticles speedRange(float min, float max) {
        this.speedMin = min;
        this.speedMax = max;
        return this;
    }

    public ConfettiParticles delayRange(int min, int max) {
        this.delayMin = min;
        this.delayMax = max;
        return this;
    }

    public ConfettiParticles lifespanRange(int min, int max) {
        this.lifetimeMin = min;
        this.lifetimeMax = max;
        return this;
    }

    public ConfettiParticles sizeRange(int min, int max) {
        this.sizeMin = min;
        this.sizeMax = max;
        return this;
    }

    public ConfettiParticles quantityRange(int min, int max) {
        this.quantityMin = min;
        this.quantityMax = max;
        return this;
    }

    public ConfettiParticles spawnedPosition(int x, int y) {
        this.spawnerPos = new Vector3d(x, y, 0);
        return this;
    }

    private int randi(int min, int max) {
        if (min == max) return min;
        return random.nextInt(max - min) + min;
    }

    private float randf(float min, float max) {
        if (min == max) return min;
        return random.nextFloat() * (max - min) + min;
    }

    public void tick() {
        for (ConfettiParticle particle : particles) {
            if (particle.hasDelay()) {
                particle.tickDelay--;
                continue;
            }

            particle.pos = particle.pos.add(particle.velocity);

            particle.velocity = particle.velocity.scale(0.97);
            particle.velocity = particle.velocity.add(0, 0.1, 0);

            particle.ticksLived++;
        }

        particles.removeIf(ConfettiParticle::shouldDespawn);
    }

    public void render(MatrixStack matrixStack) {
        for (ConfettiParticle particle : particles) {
            double x0 = particle.pos.x() - particle.size / 2.0;
            double y0 = particle.pos.y() - particle.size / 2.0;
            double x1 = x0 + particle.size;
            double y1 = y0 + particle.size;
            AbstractGui.fill(matrixStack,
                    (int) x0, (int) y0,
                    (int) x1, (int) y1,
                    particle.color);
        }
    }

    public void pop() {
        int quantity = randi(quantityMin, quantityMax);

        for (int i = 0; i < quantity; i++) {
            ConfettiParticle particle = new ConfettiParticle();
            particle.pos = new Vector3d(spawnerPos.x, spawnerPos.y, 0);
            particle.velocity = new Vector3d(1, 0, 0)
                    .zRot(-randf(angleMin, angleMax))
                    .scale(randf(speedMin, speedMax));
            particle.size = randi(sizeMin, sizeMax);
            particle.color = PARTICLE_COLORS[random.nextInt(PARTICLE_COLORS.length)];
            particle.tickDelay = randi(delayMin, delayMax);
            particle.ticksLifespan = randi(lifetimeMin, lifetimeMax);
            this.particles.add(particle);
        }
    }

    /* ---------------------------- */

    protected static class ConfettiParticle {
        public Vector3d pos;
        public Vector3d velocity;
        public int size;
        public int color;
        public int ticksLived, ticksLifespan;
        public int tickDelay;

        public boolean hasDelay() {
            return tickDelay > 0;
        }

        public boolean shouldDespawn() {
            return ticksLived >= ticksLifespan;
        }
    }

}

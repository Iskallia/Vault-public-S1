package iskallia.vault.entity.ai;

import net.minecraft.entity.LivingEntity;

public class RegenAfterAWhile<T extends LivingEntity> {

    private final int startTicksUntilRegen;
    private final int ticksUntilPulse;
    private final float regenPercentage;

    public T entity;
    public int ticksUntilRegen;
    public int ticksUntilNextPulse;

    public RegenAfterAWhile(T entity) {
        this(entity, (int)(4.5 * 20), 10, 0.05F);
    }

    public RegenAfterAWhile(T entity, int startTicksUntilRegen, int ticksUntilPulse, float regenPercentage) {
        this.entity = entity;
        this.startTicksUntilRegen = startTicksUntilRegen;
        this.ticksUntilPulse = ticksUntilPulse;
        this.regenPercentage = regenPercentage;
    }

    private void resetTicks() {
        this.ticksUntilRegen = this.startTicksUntilRegen;
        resetPulseTicks();
    }

    private void resetPulseTicks() {
        this.ticksUntilNextPulse = this.ticksUntilPulse;
    }

    public void onDamageTaken() {
        resetTicks();
    }

    public void tick() {
        if(this.ticksUntilRegen <= 0) {
            if(this.ticksUntilNextPulse <= 0) {
                float maxHealth = entity.getMaxHealth();
                float currentHealth = entity.getHealth();

                this.entity.setHealth(
                        Math.min(maxHealth, currentHealth + maxHealth * this.regenPercentage)
                );

                this.resetPulseTicks();
            } else {
                this.ticksUntilNextPulse--;
            }

        } else {
            this.ticksUntilRegen--;
        }
    }

}

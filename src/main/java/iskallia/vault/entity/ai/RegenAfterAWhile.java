package iskallia.vault.entity.ai;

import net.minecraft.entity.LivingEntity;

public class RegenAfterAWhile<T extends LivingEntity> {

    // TODO: Maybe extract to a config later?
    public static int TICKS_UNTIL_REGEN = (int) (4.5 * 20);
    public static int TICKS_UNTIL_PULSE = 10;
    public static float REGEN_PERCENT_PER_PULSE = 0.05f;

    public T entity;
    public int ticksUntilRegen;
    public int ticksUntilNextPulse;

    public RegenAfterAWhile(T entity) {
        this.entity = entity;
    }

    private void resetTicks() {
        this.ticksUntilRegen = TICKS_UNTIL_REGEN;
        resetPulseTicks();
    }

    private void resetPulseTicks() {
        this.ticksUntilNextPulse = TICKS_UNTIL_PULSE;
    }

    public void onDamageTaken() {
        resetTicks();
    }

    public void tick() {
        if (this.ticksUntilRegen <= 0) {
            if (this.ticksUntilNextPulse <= 0) {
                float maxHealth = entity.getMaxHealth();
                float currentHealth = entity.getHealth();
                entity.setHealth(
                        Math.min(maxHealth, currentHealth + maxHealth * REGEN_PERCENT_PER_PULSE)
                );
                resetPulseTicks();

            } else {
                this.ticksUntilNextPulse--;
            }

        } else {
            this.ticksUntilRegen--;
        }
    }

}

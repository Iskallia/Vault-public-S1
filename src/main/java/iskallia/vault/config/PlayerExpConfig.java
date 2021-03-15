package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.MathUtilities;

public class PlayerExpConfig extends Config {

    @Expose private int expPerVaultBoss;

    public int getRelicBoosterPackExp() {
        return (int) (expPerVaultBoss * MathUtilities.randomFloat(0.01f, 0.20f));
    }

    public int getExpPerVaultBoss() {
        return expPerVaultBoss;
    }

    @Override
    public String getName() {
        return "player_exp";
    }

    @Override
    protected void reset() {
        this.expPerVaultBoss = 10_000;
    }

}

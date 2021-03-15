package iskallia.vault.config;

import com.google.gson.annotations.Expose;

import java.util.Random;

public class VaultCrystalConfig extends Config {

    @Expose
    public int NORMAL_WEIGHT;
    @Expose
    public int RARE_WEIGHT;
    @Expose
    public int EPIC_WEIGHT;
    @Expose
    public int OMEGA_WEIGHT;


    private Random rand = new Random();

    @Override
    public String getName() {
        return "vault_crystal";
    }

    @Override
    protected void reset() {

        NORMAL_WEIGHT = 20;
        RARE_WEIGHT = 10;
        EPIC_WEIGHT = 5;
        OMEGA_WEIGHT = 1;

    }


}

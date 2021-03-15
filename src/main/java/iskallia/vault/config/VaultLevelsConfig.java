package iskallia.vault.config;

import com.google.gson.annotations.Expose;

import java.util.LinkedList;
import java.util.List;

public class VaultLevelsConfig extends Config {

    @Expose public List<VaultLevelMeta> levelMetas;

    @Override
    public String getName() {
        return "vault_levels";
    }

    public VaultLevelMeta getLevelMeta(int level) {
        int maxLevelTNLAvailable = levelMetas.size() - 1;

        if (level < 0 || level > maxLevelTNLAvailable)
            return levelMetas.get(maxLevelTNLAvailable);

        return levelMetas.get(level);
    }

    @Override
    protected void reset() {
        levelMetas = new LinkedList<>();

        for (int i = 0; i < 80; i++) { // Dunno why but 80 is always the max level in my mind
            VaultLevelMeta vaultLevel = new VaultLevelMeta();
            vaultLevel.level = i;
            vaultLevel.tnl = defaultTNLFunction(i);
            levelMetas.add(vaultLevel);
        }
    }

    public int defaultTNLFunction(int level) {
        return level * 500 + 10_000;
    }

    public static class VaultLevelMeta {
        @Expose public int level;
        @Expose public int tnl;
    }

}

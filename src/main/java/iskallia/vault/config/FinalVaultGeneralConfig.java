package iskallia.vault.config;

import com.google.gson.annotations.Expose;

import java.util.Arrays;
import java.util.List;

public class FinalVaultGeneralConfig extends Config {

	@Expose public List<String> bossNames;

	@Override
	public String getName() {
		return "final_vault_general";
	}

	@Override
	protected void reset() {
		this.bossNames = Arrays.asList("iskall85", "HBomb94", "Stressmonster101", "CaptainSparklez", "AntonioAsh", "Renthedog");
	}

}

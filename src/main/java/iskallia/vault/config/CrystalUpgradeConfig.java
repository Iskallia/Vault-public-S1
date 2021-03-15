package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class CrystalUpgradeConfig extends Config {

	@Expose public int COMMON_TO_RARE;
	@Expose public int RARE_TO_EPIC;
	@Expose public int EPIC_TO_OMEGA;

	@Override
	public String getName() {
		return "crystal_upgrade";
	}

	@Override
	protected void reset() {
		this.COMMON_TO_RARE = 8;
		this.RARE_TO_EPIC = 6;
		this.EPIC_TO_OMEGA = 4;
	}

}

package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.WeightedList;

public class VaultStewConfig extends Config {

	@Expose public WeightedList<String> STEW_POOL;

	@Override
	public String getName() {
		return "vault_stew";
	}

	@Override
	protected void reset() {
		this.STEW_POOL = new WeightedList<>();

		this.STEW_POOL.add(ModItems.VAULT_STEW_NORMAL.getRegistryName().toString(), 20)
				.add(ModItems.VAULT_STEW_RARE.getRegistryName().toString(), 10)
				.add(ModItems.VAULT_STEW_EPIC.getRegistryName().toString(), 5)
				.add(ModItems.VAULT_STEW_OMEGA.getRegistryName().toString(), 1).strip();
	}

}

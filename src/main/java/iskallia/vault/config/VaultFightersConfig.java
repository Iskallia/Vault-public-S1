package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.Collections;
import java.util.List;


/**
 * This config allows to switch which name vault fighters will have.
 * It operates in 4 modes:
 * Mode.PLAYER - the player who started the vault will be a fighter.
 * Mode.ONLINE_PLAYERS - the random player from all online players will be a fighter.
 * Mode.WHITELIST - the random player from server whitelist will be a fighter.
 * Mode.LIST - the random name from the given fighter list will be a fighter.
 *
 * FIGHTER_LIST will operate only in LIST mode.
 */
public class VaultFightersConfig extends Config {
	/**
	 * Variable that stores currently active pool mode.
	 */
	@Expose public Mode POOL_MODE;

	/**
	 * List of custom defined fighters which will be active if mode is set to the list.
	 */
	@Expose public List<String> FIGHTER_LIST;


	/**
	 * Different modes for the raffle fighter choosing.
	 */
	public enum Mode {
		// The player who started the vault
		PLAYER,
		// Random player from online player list.
		ONLINE_PLAYERS,
		// Random player from whitelisted player list.
		WHITELIST,
		// Random player from given list.
		LIST
	}

	@Override
	public String getName() {
		return "vault_fighters";
	}

	@Override
	protected void reset() {
		POOL_MODE = Mode.PLAYER;
		FIGHTER_LIST = Collections.emptyList();
	}
}

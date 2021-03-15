package iskallia.vault.config;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class VaultTimerConfig extends Config {

	@Expose private List<Time> TIME_OVERRIDES = new ArrayList<>();

	@Override
	public String getName() {
		return "vault_timer";
	}

	public int getForLevel(int level) {
		for(int i = 0; i < this.TIME_OVERRIDES.size(); i++) {
			if(level < this.TIME_OVERRIDES.get(i).MIN_LEVEL) {
				if(i == 0)break;
				return this.TIME_OVERRIDES.get(i - 1).TIME;
			} else if(i == this.TIME_OVERRIDES.size() - 1) {
				return this.TIME_OVERRIDES.get(i).TIME;
			}
		}

		return 20 * 60 * 25;
	}


	@Override
	protected void reset() {
		this.TIME_OVERRIDES.add(new Time(0, 20 * 60 * 25));
	}

	public static class Time {
		@Expose public int MIN_LEVEL;
		@Expose public int TIME;

		public Time(int minLevel, int time) {
			this.MIN_LEVEL = minLevel;
			this.TIME = time;
		}
	}

}

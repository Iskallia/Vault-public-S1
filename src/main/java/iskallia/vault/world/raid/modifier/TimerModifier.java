package iskallia.vault.world.raid.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.util.ResourceLocation;

public class TimerModifier extends VaultModifier {

	@Expose private final int timerAddend;

	public TimerModifier(String name, ResourceLocation icon, int timerAddend) {
		super(name, icon);
		this.timerAddend = timerAddend;

		if(this.timerAddend > 0) {
			this.format(this.getColor(), "Adds " + (this.timerAddend / 20) + " seconds to the clock.");
		} else if(this.timerAddend < 0) {
			this.format(this.getColor(), "Removes " + -(this.timerAddend / 20) + " seconds from the clock.");
		} else {
			this.format(this.getColor(), "Does nothing at all. A bit of a waste of a modifier...");
		}
	}

	@Override
	public void apply(VaultRaid raid) {
		raid.sTickLeft += this.timerAddend;
		raid.ticksLeft += this.timerAddend;
	}

}

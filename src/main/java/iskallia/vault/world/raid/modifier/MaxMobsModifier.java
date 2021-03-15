package iskallia.vault.world.raid.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.util.ResourceLocation;

public class MaxMobsModifier extends VaultModifier {

	@Expose private final int maxMobsAddend;

	public MaxMobsModifier(String name, ResourceLocation icon, int maxMobsAddend) {
		super(name, icon);
		this.maxMobsAddend = maxMobsAddend;

		if(this.maxMobsAddend > 0) {
			this.format(this.getColor(), "Spawns " + this.maxMobsAddend + (this.maxMobsAddend == 1 ? " more mob." : " more mobs."));
		} else if(this.maxMobsAddend < 0) {
			this.format(this.getColor(), "Spawns " + -this.maxMobsAddend + (-this.maxMobsAddend == 1 ? " less mob." : " less mobs."));
		} else {
			this.format(this.getColor(), "Does nothing at all. A bit of a waste of a modifier...");
		}
	}

	@Override
	public void apply(VaultRaid raid) {
		raid.spawner.maxMobs += this.maxMobsAddend;
	}

}

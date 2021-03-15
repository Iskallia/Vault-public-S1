package iskallia.vault.world.raid.modifier;

import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.util.ResourceLocation;

public class NoExitModifier extends VaultModifier {

	public NoExitModifier(String name, ResourceLocation icon) {
		super(name, icon);
		this.format(this.getColor(), "F");
	}

	@Override
	public void apply(VaultRaid raid) {
		raid.cannotExit = true;
	}

}

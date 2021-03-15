package iskallia.vault;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {

	@Override
	public void connect() {
		Mixins.addConfigurations("assets/" + Vault.MOD_ID + "/" + Vault.MOD_ID + ".mixins.json");
	}

}

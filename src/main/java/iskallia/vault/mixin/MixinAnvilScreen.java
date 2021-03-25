package iskallia.vault.mixin;

import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilScreen.class)
public class MixinAnvilScreen {

    @ModifyConstant(method = "renderLabels", constant = @Constant(intValue = 40))
    private int overrideMaxRepairLevel(int oldValue) {
        return Integer.MAX_VALUE;
    }

}

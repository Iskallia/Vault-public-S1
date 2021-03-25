package iskallia.vault.mixin;

import net.minecraft.inventory.container.RepairContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(RepairContainer.class)
public class MixinRepairContainer {

    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40, ordinal = 2))
    private int overrideMaxRepairLevel(int oldValue) {
        return Integer.MAX_VALUE;
    }

}

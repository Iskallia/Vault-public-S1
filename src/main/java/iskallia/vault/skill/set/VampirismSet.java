package iskallia.vault.skill.set;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.gear.VaultGear;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class VampirismSet extends PlayerSet {

    @Expose private final float leechRatio;

    public VampirismSet(float leechRatio) {
        super(VaultGear.Set.VAMPIRE);
        this.leechRatio = leechRatio;
    }

    public float getLeechRatio() {
        return leechRatio;
    }
}

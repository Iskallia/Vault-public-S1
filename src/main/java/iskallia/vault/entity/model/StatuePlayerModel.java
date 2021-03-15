package iskallia.vault.entity.model;

import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;

public class StatuePlayerModel<T extends LivingEntity> extends PlayerModel<T> {

    public StatuePlayerModel(float modelSize, boolean smallArmsIn) {
        super(modelSize, smallArmsIn);
    }

}

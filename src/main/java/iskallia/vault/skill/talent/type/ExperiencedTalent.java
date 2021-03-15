package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ExperiencedTalent extends PlayerTalent {

    @Expose private final float increasedExpPercentage;

    public ExperiencedTalent(int cost, float increasedExpPercentage) {
        super(cost);
        this.increasedExpPercentage = increasedExpPercentage;
    }

    public float getIncreasedExpPercentage() {
        return increasedExpPercentage;
    }

    @SubscribeEvent
    public static void onOrbPickup(PlayerXpEvent.PickupXp event) {
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        TalentTree abilities = PlayerTalentsData.get(player.getServerWorld()).getTalents(player);

        for (TalentNode<?> node : abilities.getNodes()) {
            if (!(node.getTalent() instanceof ExperiencedTalent)) continue;
            ExperiencedTalent experienced = ((ExperiencedTalent) node.getTalent());
            ExperienceOrbEntity orb = event.getOrb();
            orb.xpValue *= (1 + experienced.getIncreasedExpPercentage());
        }
    }

}

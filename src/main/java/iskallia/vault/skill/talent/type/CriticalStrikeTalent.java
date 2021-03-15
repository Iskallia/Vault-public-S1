package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CriticalStrikeTalent extends PlayerTalent {

	@Expose private final float chance;

	public CriticalStrikeTalent(int cost, float chance) {
		super(cost);
		this.chance = chance;
	}

	public float getChance() {
		return this.chance;
	}

	@SubscribeEvent
	public static void onCriticalHit(CriticalHitEvent event) {
		if(event.getEntity().world.isRemote)return;
		ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
		TalentTree abilities = PlayerTalentsData.get(player.getServerWorld()).getTalents(player);

		for(TalentNode<?> node : abilities.getNodes()) {
			if(!(node.getTalent() instanceof CriticalStrikeTalent))continue;

			if(player.world.rand.nextFloat() < ((CriticalStrikeTalent)node.getTalent()).getChance()) {
				event.setResult(Event.Result.ALLOW);
				return;
			}
		}
	}

}

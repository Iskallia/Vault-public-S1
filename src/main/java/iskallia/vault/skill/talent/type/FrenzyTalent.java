package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FrenzyTalent extends PlayerTalent {

	@Expose private final float threshold;
	@Expose private final float damageMultiplier;

	public FrenzyTalent(int cost, float threshold, float damageMultiplier) {
		super(cost);
		this.threshold = threshold;
		this.damageMultiplier = damageMultiplier;
	}

	public float getThreshold() {
		return this.threshold;
	}

	public float getDamageMultiplier() {
		return this.damageMultiplier;
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		if(event.getEntity().world.isRemote)return;
		if(!(event.getSource().getTrueSource() instanceof PlayerEntity))return;

		ServerPlayerEntity player = (ServerPlayerEntity)event.getSource().getTrueSource();
		TalentTree abilities = PlayerTalentsData.get(player.getServerWorld()).getTalents(player);

		for(TalentNode<?> node : abilities.getNodes()) {
			if(!(node.getTalent() instanceof FrenzyTalent))continue;
			FrenzyTalent talent = (FrenzyTalent)node.getTalent();

			if(player.getHealth() / player.getMaxHealth() <= talent.getThreshold()) {
				event.setAmount(event.getAmount() * talent.getDamageMultiplier());
			}
		}
	}

}

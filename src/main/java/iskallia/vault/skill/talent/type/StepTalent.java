package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.StepHeightMessage;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkDirection;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StepTalent extends PlayerTalent {

	@Expose private final float stepHeightAddend;

	public StepTalent(int cost, float stepHeightAddend) {
		super(cost);
		this.stepHeightAddend = stepHeightAddend;
	}

	public float getStepHeightAddend() {
		return this.stepHeightAddend;
	}

	@Override
	public void onAdded(PlayerEntity player) {
		//System.out.println(player.stepHeisght);
		//player.stepHeight += this.stepHeightAddend;
		set((ServerPlayerEntity) player, player.stepHeight + this.stepHeightAddend);
		//System.out.println(player.stepHeight);
	}

	@Override
	public void tick(PlayerEntity player) {
		//System.out.println(player.stepHeight);
	}

	@Override
	public void onRemoved(PlayerEntity player) {
		set((ServerPlayerEntity)player, player.stepHeight - this.stepHeightAddend);
	}

	@SubscribeEvent
	public static void onEntityCreated(EntityJoinWorldEvent event) {
		if(event.getEntity().world.isRemote)return;
		if(!(event.getEntity() instanceof PlayerEntity))return;

		ServerPlayerEntity player = (ServerPlayerEntity)event.getEntity();
		TalentTree abilities = PlayerTalentsData.get(player.getServerWorld()).getTalents(player);
		float totalStepHeight = 0.0F;

		for(TalentNode<?> node : abilities.getNodes()) {
			if(!(node.getTalent() instanceof StepTalent))continue;
			StepTalent talent = (StepTalent)node.getTalent();
			totalStepHeight += talent.getStepHeightAddend();
		}

		// Add stepHeight from default value, what is 1.0F
		if (totalStepHeight != 0.0F) {
			set(player, 1.0F + totalStepHeight);
		}
	}

	public static void set(ServerPlayerEntity player, float stepHeight) {
		ModNetwork.CHANNEL.sendTo(new StepHeightMessage(stepHeight), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		player.stepHeight = stepHeight;
	}

}

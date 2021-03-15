package iskallia.vault.event;

import iskallia.vault.init.ModModels;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class ClientEvents {

	@SubscribeEvent
	public static void onColorHandlerRegister(ColorHandlerEvent.Item event) {
		ModModels.registerItemColors(event.getItemColors());
	}

}

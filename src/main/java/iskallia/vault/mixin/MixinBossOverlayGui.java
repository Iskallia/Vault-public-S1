package iskallia.vault.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.client.gui.overlay.BossOverlayGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(BossOverlayGui.class)
public abstract class MixinBossOverlayGui {

	@Shadow @Final private Map<UUID, ClientBossInfo> mapBossInfos;

	@Redirect(method = "func_238484_a_", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
	private Collection<ClientBossInfo> thing(Map<UUID, ClientBossInfo> map) {
		Map<UUID, Entity> entities = new HashMap<>();

		Minecraft.getInstance().world.getAllEntities().forEach(entity -> {
			entities.put(entity.getUniqueID(), entity);
		});

		return this.mapBossInfos.entrySet().stream()
				.sorted(Comparator.comparingDouble(o -> {
					PlayerEntity player = Minecraft.getInstance().player;
					Entity entity = entities.get(o.getKey());

					if(entity == null) {
						return Integer.MAX_VALUE;
					} else if(player.getName().getString().equals(entity.getCustomName().getString())) {
						return Integer.MIN_VALUE;
					}

					return player.getDistance(entity);
				}))
				.map(Map.Entry::getValue)
				.collect(Collectors.toList());
	}

}

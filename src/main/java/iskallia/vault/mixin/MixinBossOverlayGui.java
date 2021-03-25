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

	@Shadow @Final private Map<UUID, ClientBossInfo> events;

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
	private Collection<ClientBossInfo> render(Map<UUID, ClientBossInfo> map) {
		Map<UUID, Entity> entities = new HashMap<>();

		Minecraft.getInstance().level.entitiesForRendering().forEach(entity -> {
			entities.put(entity.getUUID(), entity);
		});

		return this.events.entrySet().stream()
				.sorted(Comparator.comparingDouble(o -> {
					PlayerEntity player = Minecraft.getInstance().player;
					Entity entity = entities.get(o.getKey());

					if(entity == null) {
						return Integer.MAX_VALUE;
					} else if(player.getName().getString().equals(entity.getCustomName().getString())) {
						return Integer.MIN_VALUE;
					}

					return player.distanceTo(entity);
				}))
				.map(Map.Entry::getValue)
				.collect(Collectors.toList());
	}

}

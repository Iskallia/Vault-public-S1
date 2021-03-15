package iskallia.vault.world.raid.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public abstract class VaultModifier {

	public static final Random RANDOM = new Random();

	@Expose private final String name;
	@Expose private final String icon;
	@Expose private String color = String.valueOf(0x00FFFF);
	@Expose private String description = "This is a description.";

	public VaultModifier(String name, ResourceLocation icon) {
		this.name = name;
		this.icon = icon.toString();
	}

	public VaultModifier format(int color, String description) {
		this.color = String.valueOf(color);
		this.description = description;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public ResourceLocation getIcon() {
		return new ResourceLocation(this.icon);
	}

	public int getColor() {
		return Integer.parseInt(this.color);
	}

	public String getDescription() {
		return this.description;
	}

	public abstract void apply(VaultRaid raid);

	public void tick(ServerWorld world, PlayerEntity player) {

	}

}

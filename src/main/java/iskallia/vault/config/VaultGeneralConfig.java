package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.Vault;
import iskallia.vault.init.ModConfigs;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VaultGeneralConfig extends Config {

	@Expose private int TICK_COUNTER;
	@Expose private int NO_EXIT_CHANCE;
	@Expose private int OBELISK_DROP_CHANCE;
	@Expose private List<String> ITEM_BLACKLIST;
	@Expose private List<String> BLOCK_BLACKLIST;
	@Expose private Map<Integer, String> MONTHS_TO_TAG_COLOR;
	@Expose public float VAULT_EXIT_TNL_MIN;
	@Expose public float VAULT_EXIT_TNL_MAX;

	@Override
	public String getName() {
		return "vault_general";
	}

	public int getTickCounter() {
		return this.TICK_COUNTER;
	}

	public int getNoExitChance() {
		return this.NO_EXIT_CHANCE;
	}

	public int getObeliskDropChance() {
		return OBELISK_DROP_CHANCE;
	}

	@Override
	protected void reset() {
		this.TICK_COUNTER = 20 * 60 * 25;
		this.NO_EXIT_CHANCE = 10;

		this.ITEM_BLACKLIST = new ArrayList<>();
		this.ITEM_BLACKLIST.add(Items.ENDER_CHEST.getRegistryName().toString());

		this.BLOCK_BLACKLIST = new ArrayList<>();
		this.BLOCK_BLACKLIST.add(Blocks.ENDER_CHEST.getRegistryName().toString());
		this.OBELISK_DROP_CHANCE = 2;

		this.MONTHS_TO_TAG_COLOR = new LinkedHashMap<>();
		this.MONTHS_TO_TAG_COLOR.put(1, TextFormatting.AQUA.name());
		this.MONTHS_TO_TAG_COLOR.put(7, TextFormatting.DARK_AQUA.name());
		this.MONTHS_TO_TAG_COLOR.put(13, TextFormatting.YELLOW.name());
		this.MONTHS_TO_TAG_COLOR.put(25, TextFormatting.GOLD.name());
		this.MONTHS_TO_TAG_COLOR.put(48, TextFormatting.RED.name());

		this.VAULT_EXIT_TNL_MIN = 0.0F;
		this.VAULT_EXIT_TNL_MAX = 0.0F;
	}

	public TextFormatting getTagFormat(int months) {
		for(Map.Entry<Integer, String> e: this.MONTHS_TO_TAG_COLOR.entrySet()) {
			if(months >= e.getKey())return TextFormatting.valueOf(e.getValue());
		}

		return TextFormatting.WHITE;
	}

	@SubscribeEvent
	public static void cancelItemInteraction(PlayerInteractEvent event) {
		if(event.getPlayer().level.dimension() != Vault.VAULT_KEY)return;

		if(ModConfigs.VAULT_GENERAL.ITEM_BLACKLIST.contains(event.getItemStack().getItem().getRegistryName().toString())) {
			if(event.isCancelable())
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void cancelBlockInteraction(PlayerInteractEvent event) {
		if(event.getPlayer().level.dimension() != Vault.VAULT_KEY)return;
		BlockState state = event.getWorld().getBlockState(event.getPos());

		if(ModConfigs.VAULT_GENERAL.BLOCK_BLACKLIST.contains(state.getBlock().getRegistryName().toString())) {
			if(event.isCancelable())
				event.setCanceled(true);
		}
	}

}

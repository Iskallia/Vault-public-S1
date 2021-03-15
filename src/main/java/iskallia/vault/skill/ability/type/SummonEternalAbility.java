package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.Vault;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.world.data.EternalsData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SummonEternalAbility extends PlayerAbility {

	@Expose private int despawnTime;
	@Expose private boolean vaultOnly;
	@Expose private int count;

	public SummonEternalAbility(int cost, int cooldown, int despawnTime, boolean vaultOnly, int count) {
		super(cost, Behavior.RELEASE_TO_PERFORM);
		this.cooldown = cooldown;
		this.despawnTime = despawnTime;
		this.vaultOnly = vaultOnly;
		this.count = count;
	}

	public int getDespawnTime() {
		return this.despawnTime;
	}

	public boolean isVaultOnly() {
		return this.vaultOnly;
	}

	public int getCount() {
		return this.count;
	}

	@Override
	public void onAction(PlayerEntity player, boolean active) {
		if(player.getEntityWorld().isRemote)return;
		EternalsData.EternalGroup eternals = EternalsData.get((ServerWorld)player.world).getEternals(player);

		if(eternals.getEternals().isEmpty()) {
			player.sendMessage(new StringTextComponent(TextFormatting.RED + "You have no eternals to summon."), player.getUniqueID());
		} else if(player.getEntityWorld().getDimensionKey() != Vault.VAULT_KEY && this.isVaultOnly()) {
			player.sendMessage(new StringTextComponent(TextFormatting.RED + "You can only summon eternals in the Vault!"), player.getUniqueID());
		} else {
			for(int i = 0; i < this.getCount(); i++) {
				EternalEntity eternal = eternals.getRandom(player.world.getRandom()).create(player.world);
				eternal.setLocationAndAngles(player.getPosX(), player.getPosY(), player.getPosZ(), player.rotationYaw, player.rotationPitch);
				eternal.setDespawnTime(player.getServer().getTickCounter() + this.getDespawnTime());
				eternal.owner = player.getUniqueID();
				eternal.setGlowing(true);
				player.world.addEntity(eternal);
			}
		}
	}

	@SubscribeEvent
	public static void onDamage(LivingAttackEvent event) {
		LivingEntity damagedEntity = event.getEntityLiving();
		Entity dealerEntity = event.getSource().getTrueSource();

		if (damagedEntity instanceof EternalEntity && dealerEntity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) dealerEntity;
			if (!player.isCreative()) {
				event.setCanceled(true);
			}
		}
	}

}

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
		if(player.getCommandSenderWorld().isClientSide)return;
		EternalsData.EternalGroup eternals = EternalsData.get((ServerWorld)player.level).getEternals(player);

		if(eternals.getEternals().isEmpty()) {
			player.sendMessage(new StringTextComponent(TextFormatting.RED + "You have no eternals to summon."), player.getUUID());
		} else if(player.getCommandSenderWorld().dimension() != Vault.VAULT_KEY && this.isVaultOnly()) {
			player.sendMessage(new StringTextComponent(TextFormatting.RED + "You can only summon eternals in the Vault!"), player.getUUID());
		} else {
			for(int i = 0; i < this.getCount(); i++) {
				EternalEntity eternal = eternals.getRandom(player.level.getRandom()).create(player.level);
				eternal.moveTo(player.getX(), player.getY(), player.getZ(), player.yRot, player.xRot);
				eternal.setDespawnTime(player.getServer().getTickCount() + this.getDespawnTime());
				eternal.owner = player.getUUID();
				eternal.setGlowing(true);
				player.level.addFreshEntity(eternal);
			}
		}
	}

	@SubscribeEvent
	public static void onDamage(LivingAttackEvent event) {
		LivingEntity damagedEntity = event.getEntityLiving();
		Entity dealerEntity = event.getSource().getEntity();

		if (damagedEntity instanceof EternalEntity && dealerEntity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) dealerEntity;
			if (!player.isCreative()) {
				event.setCanceled(true);
			}
		}
	}

}

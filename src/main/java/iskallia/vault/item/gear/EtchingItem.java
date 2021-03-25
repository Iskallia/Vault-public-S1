package iskallia.vault.item.gear;

import iskallia.vault.item.BasicItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;

import net.minecraft.item.Item.Properties;

public class EtchingItem extends BasicItem implements VaultGear<EtchingItem> {

	public EtchingItem(ResourceLocation id, Properties properties) {
		super(id, properties);
	}

	@Override
	public int getModelsFor(Rarity rarity) {
		return Set.values().length;
	}

	//===========================================================================================================//

	@Override
	public boolean isDamageable(ItemStack stack) {
		return this.isDamageable(this, stack);
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return this.getMaxDamage(this, stack, super.getMaxDamage(stack));
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		return this.getDisplayName(this, stack, super.getName(stack));
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		return this.onItemRightClick(this, world, player, hand, super.use(world, player, hand));
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, world, entity, itemSlot, isSelected);
		this.inventoryTick(this, stack, world, entity, itemSlot, isSelected);
	}

	@Override
	public void appendHoverText(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		super.appendHoverText(stack, world, tooltip, flag);
		this.addInformation(this, stack, world, tooltip, flag);
	}

	@Override
	public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
		return this.canElytraFly(this, stack, entity);
	}

	@Override
	public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
		return this.elytraFlightTick(this, stack, entity, flightTicks);
	}

}

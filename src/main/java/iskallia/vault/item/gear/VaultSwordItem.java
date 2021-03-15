package iskallia.vault.item.gear;

import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;

public class VaultSwordItem extends SwordItem implements VaultGear<VaultSwordItem> {

	public VaultSwordItem(ResourceLocation id, Properties builder) {
		super(Tier.INSTANCE, 0, -2.4F, builder);
		this.setRegistryName(id);
	}

	@Override
	public int getModelsFor(Rarity rarity) {
		return rarity == Rarity.SCRAPPY ? 2 : 2;
	}

	//===========================================================================================================//

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		return this.getAttributeModifiers(this, slot, stack, super.getAttributeModifiers(slot, stack));
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return this.isDamageable(this, stack);
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return this.getMaxDamage(this, stack, super.getMaxDamage(stack));
	}

	@Override
	public ITextComponent getDisplayName(ItemStack itemStack) {
		return this.getDisplayName(this, itemStack, super.getDisplayName(itemStack));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		return this.onItemRightClick(this, world, player, hand, super.onItemRightClick(world, player, hand));
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, world, entity, itemSlot, isSelected);
		this.inventoryTick(this, stack, world, entity, itemSlot, isSelected);
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		super.addInformation(stack, world, tooltip, flag);
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

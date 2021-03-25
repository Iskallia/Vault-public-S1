package iskallia.vault.item.gear;

import com.google.common.collect.Multimap;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.attribute.EnumAttribute;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class VaultArmorItem extends DyeableArmorItem implements VaultGear<VaultArmorItem> {

    public VaultArmorItem(ResourceLocation id, EquipmentSlotType slot, Properties builder) {
        super(Material.INSTANCE, slot, builder);
        this.setRegistryName(id);
    }

    @Override
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
        return this.getSlot();
    }

    @Override
    public int getModelsFor(Rarity rarity) {
        return 12 - 1;
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
    public ITextComponent getName(ItemStack itemStack) {
        return this.getDisplayName(this, itemStack, super.getName(itemStack));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        EquipmentSlotType slot = MobEntity.getEquipmentSlotForItem(heldStack);
        return this.onItemRightClick(this, world, player, hand,
                this.canEquip(heldStack, slot, player) ? super.use(world, player, hand) : ActionResult.fail(heldStack));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        this.inventoryTick(this, stack, world, entity, itemSlot, isSelected);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(itemStack, world, tooltip, flag);
        this.addInformation(this, itemStack, world, tooltip, flag);
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return this.canElytraFly(this, stack, entity);
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        return this.elytraFlightTick(this, stack, entity, flightTicks);
    }

    //===========================================================================================================//

    @Override
    public int getColor(ItemStack stack) {
        return this.getColor(this, stack);
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlotType armorType, Entity entity) {
        EnumAttribute<State> stateAttribute = ModAttributes.GEAR_STATE.get(stack).orElse(null);
        return stateAttribute != null && stateAttribute.getValue(stack) == State.IDENTIFIED
                && super.canEquip(stack, armorType, entity);
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        return this.getArmorModel(this, entityLiving, itemStack, armorSlot, _default);
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public String getArmorTexture(ItemStack itemStack, Entity entity, EquipmentSlotType slot, String type) {
        return this.getArmorTexture(this, itemStack, entity, slot, type);
    }

}

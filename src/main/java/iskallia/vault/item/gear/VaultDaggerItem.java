package iskallia.vault.item.gear;

import com.google.common.collect.Multimap;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AttackOffHandMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.List;

import net.minecraft.item.Item.Properties;

public class VaultDaggerItem extends SwordItem implements VaultGear<VaultDaggerItem> {

	public VaultDaggerItem(ResourceLocation id, Properties builder) {
		super(Tier.INSTANCE, 0, -2.4F, builder);
		this.setRegistryName(id);
	}

	@Override
	public int getModelsFor(Rarity rarity) {
		return rarity == Rarity.SCRAPPY ? 1 : 1;
	}

	public void attackOffHand() {
		Minecraft mc = Minecraft.getInstance();

		if(Minecraft.getInstance().level != null && Minecraft.getInstance().screen == null
				&& !Minecraft.getInstance().isPaused() && mc.player != null && !mc.player.isBlocking()) {
			RayTraceResult rayTrace = getEntityMouseOverExtended(6.0F);

			if(rayTrace instanceof EntityRayTraceResult) {
				EntityRayTraceResult entityRayTrace = (EntityRayTraceResult)rayTrace;
				Entity entityHit = entityRayTrace.getEntity();

				if(entityHit != mc.player && entityHit != mc.player.getVehicle()) {
					ModNetwork.CHANNEL.sendTo(new AttackOffHandMessage(entityHit.getId()), mc.player.connection.getConnection(), NetworkDirection.PLAY_TO_SERVER);
				}
			}
		}
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

	//===========================================================================================================//

	private static RayTraceResult getEntityMouseOverExtended(float reach) {
		RayTraceResult result = null;
		Minecraft mc = Minecraft.getInstance();
		Entity viewEntity = mc.cameraEntity;

		if(viewEntity != null && mc.level != null) {
			double reachDistance = reach;
			RayTraceResult rayTrace = viewEntity.pick(reachDistance, 0.0F, false);
			Vector3d eyePos = viewEntity.getEyePosition(0.0F);
			boolean hasExtendedReach = false;
			double attackReach;

			if(mc.gameMode != null) {
				if (mc.gameMode.hasFarPickRange() && reachDistance < 6.0D) {
					attackReach = 6.0D;
					reachDistance = attackReach;
				} else if (reachDistance > (double)reach) {
					hasExtendedReach = true;
				}
			}

			attackReach = rayTrace.getLocation().distanceToSqr(eyePos);

			Vector3d lookVec = viewEntity.getViewVector(1.0F);
			Vector3d attackVec = eyePos.add(lookVec.x * reachDistance, lookVec.y * reachDistance, lookVec.z * reachDistance);
			AxisAlignedBB axisAlignedBB = viewEntity.getBoundingBox().expandTowards(lookVec.scale(reachDistance)).inflate(1.0D, 1.0D, 1.0D);
			EntityRayTraceResult entityRayTrace = ProjectileHelper.getEntityHitResult(viewEntity, eyePos, attackVec, axisAlignedBB, (entity) -> !entity.isSpectator() && entity.isPickable(), attackReach);

			if(entityRayTrace != null) {
				Vector3d hitVec = entityRayTrace.getLocation();
				double squareDistanceTo = eyePos.distanceToSqr(hitVec);

				if(hasExtendedReach && squareDistanceTo > (double)(reach * reach)) {
					result = BlockRayTraceResult.miss(hitVec, Direction.getNearest(lookVec.x, lookVec.y, lookVec.z), new BlockPos(hitVec));
				} else if(squareDistanceTo < attackReach) {
					result = entityRayTrace;
				}
			} else {
				result = BlockRayTraceResult.miss(attackVec, Direction.getNearest(lookVec.x, lookVec.y, lookVec.z), new BlockPos(attackVec));
			}
		}

		return result;
	}

}

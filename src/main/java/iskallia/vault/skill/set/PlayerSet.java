package iskallia.vault.skill.set;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.item.gear.attribute.EnumAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiPredicate;

public class PlayerSet {

	@Expose private String set;

	public PlayerSet(VaultGear.Set set) {
		this.set = set.name();
	}

	public VaultGear.Set getSet() {
		return VaultGear.Set.valueOf(this.set);
	}

	public boolean shouldBeActive(PlayerEntity player) {
		return isActive(this.getSet(), player);
	}

	public void onAdded(PlayerEntity player) {

	}

	public void onTick(PlayerEntity player) {

	}

	public void onRemoved(PlayerEntity player) {

	}

	public static boolean allMatch(PlayerEntity player, BiPredicate<EquipmentSlotType, ItemStack> predicate, EquipmentSlotType... slots) {
		return Arrays.stream(slots).allMatch(slot -> predicate.test(slot, player.getItemBySlot(slot)));
	}

	public static boolean isActive(VaultGear.Set set, PlayerEntity player) {
		return allMatch(player, (slot, stack) -> {
			Optional<EnumAttribute<VaultGear.Set>> attribute = ModAttributes.GEAR_SET.get(stack);
			return attribute.isPresent() && attribute.get().getValue(stack) == set;
		}, EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET);
	}

}

package iskallia.vault.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OverlevelEnchantHelper {

    public static int getOverlevels(ItemStack enchantedBookStack) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(enchantedBookStack);
        for (Enchantment enchantment : enchantments.keySet()) {
            int level = enchantments.get(enchantment);
            if (level > enchantment.getMaxLevel()) {
                return level - enchantment.getMaxLevel();
            }
        }
        return -1;
    }

    public static Map<Enchantment, Integer> getEnchantments(ItemStack stack) {
        CompoundNBT nbt = Optional.ofNullable(stack.getTag()).orElseGet(CompoundNBT::new);
        ListNBT enchantmentsNBT = nbt.getList(stack.getItem() == Items.ENCHANTED_BOOK
                ? "StoredEnchantments" : "Enchantments", Constants.NBT.TAG_COMPOUND);

        HashMap<Enchantment, Integer> enchantments = new HashMap<>();

        for (int i = 0; i < enchantmentsNBT.size(); i++) {
            CompoundNBT enchantmentNBT = enchantmentsNBT.getCompound(i);
            ResourceLocation id = new ResourceLocation(enchantmentNBT.getString("id"));
            int level = enchantmentNBT.getInt("lvl");

            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(id);
            if (enchantment != null) enchantments.put(enchantment, level);
        }

        return enchantments;
    }

}

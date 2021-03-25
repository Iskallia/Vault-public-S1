package iskallia.vault.block.item;

import iskallia.vault.init.ModItems;
import iskallia.vault.util.nbt.NBTSerializer;
import iskallia.vault.vending.TraderCore;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class AdvancedVendingMachineBlockItem extends BlockItem {

    public AdvancedVendingMachineBlockItem(Block block) {
        super(block, new Properties()
                .tab(ModItems.VAULT_MOD_GROUP)
                .stacksTo(64));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT nbt = stack.getTag();

        if (nbt != null) {
            CompoundNBT blockEntityTag = nbt.getCompound("BlockEntityTag");
            ListNBT cores = blockEntityTag.getList("coresList", Constants.NBT.TAG_COMPOUND);
            for (INBT tag : cores) {
                TraderCore core;
                try {
                    core = NBTSerializer.deserialize(TraderCore.class, (CompoundNBT) tag);
                    StringTextComponent text = new StringTextComponent(" Vendor: " + core.getName());
                    text.setStyle(Style.EMPTY.withColor(Color.fromRgb(0xFF_ff9966)));
                    tooltip.add(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}

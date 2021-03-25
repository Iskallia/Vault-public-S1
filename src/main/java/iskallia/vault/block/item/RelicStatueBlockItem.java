package iskallia.vault.block.item;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.RelicSet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RelicStatueBlockItem extends BlockItem {

    public RelicStatueBlockItem() {
        super(ModBlocks.RELIC_STATUE, new Item.Properties()
                .tab(ModItems.VAULT_MOD_GROUP)
                .stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT nbt = stack.getTag();

        if (nbt != null) {
            CompoundNBT blockEntityTag = nbt.getCompound("BlockEntityTag");
            String relicSet = blockEntityTag.getString("RelicSet");

            RelicSet set = RelicSet.REGISTRY.get(new ResourceLocation(relicSet));

            if(set != null) {
                StringTextComponent titleText = new StringTextComponent(" Relic Set: " + set.getName());
                titleText.setStyle(Style.EMPTY.withColor(Color.fromRgb(0xFF_ff9966)));
                tooltip.add(titleText);
            }
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    public static ItemStack withRelicSet(RelicSet relicSet) {
        ItemStack itemStack = new ItemStack(ModBlocks.RELIC_STATUE);

        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("RelicSet", relicSet.getId().toString());

        CompoundNBT stackNBT = new CompoundNBT();
        stackNBT.put("BlockEntityTag", nbt);
        itemStack.setTag(stackNBT);

        return itemStack;
    }

}

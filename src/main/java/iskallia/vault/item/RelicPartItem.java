package iskallia.vault.item;

import iskallia.vault.util.RelicSet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RelicPartItem extends Item {

    protected RelicSet relicSet;

    public RelicPartItem(ItemGroup group, ResourceLocation id) {
        super(new Properties()
                .group(group)
                .maxStackSize(64));

        this.setRegistryName(id);
    }

    public RelicSet getRelicSet() {
        return this.relicSet;
    }

    public void setRelicSet(RelicSet relicSet) {
        this.relicSet = relicSet;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        StringTextComponent line = new StringTextComponent("Vault Relic - " + this.relicSet.getName());
        line.setStyle(Style.EMPTY.setColor(Color.fromInt(0xFF_c6b11e)));
        tooltip.add(new StringTextComponent(""));
        tooltip.add(line);

        super.addInformation(stack, world, tooltip, flag);
    }

}

package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.VaultRarity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemLegendaryTreasure extends Item {

    private VaultRarity vaultRarity;

    public ItemLegendaryTreasure(ItemGroup group, ResourceLocation id, VaultRarity vaultRarity) {
        super(new Properties()
                .group(group)
                .maxStackSize(1));

        this.setRegistryName(id);
        this.vaultRarity = vaultRarity;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (worldIn.isRemote) return super.onItemRightClick(worldIn, playerIn, handIn);
        if (handIn != Hand.MAIN_HAND) return super.onItemRightClick(worldIn, playerIn, handIn);
        ItemStack stack = playerIn.getHeldItemMainhand();
        if (stack.getItem() instanceof ItemLegendaryTreasure) {
            ItemLegendaryTreasure item = (ItemLegendaryTreasure) stack.getItem();
            ItemStack toDrop = ItemStack.EMPTY;
            switch (item.getRarity()) {
                case COMMON:
                    toDrop = ModConfigs.LEGENDARY_TREASURE_NORMAL.getRandom();
                    break;
                case RARE:
                    toDrop = ModConfigs.LEGENDARY_TREASURE_RARE.getRandom();
                    break;
                case EPIC:
                    toDrop = ModConfigs.LEGENDARY_TREASURE_EPIC.getRandom();
                    break;
                case OMEGA:
                    toDrop = ModConfigs.LEGENDARY_TREASURE_OMEGA.getRandom();
                    break;
            }
            playerIn.dropItem(toDrop, false);
            stack.shrink(1);
            ItemRelicBoosterPack.successEffects(worldIn, playerIn.getPositionVec());
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.getItem() instanceof ItemLegendaryTreasure) {
            ItemLegendaryTreasure item = (ItemLegendaryTreasure) stack.getItem();
            tooltip.add(new StringTextComponent(TextFormatting.GOLD + "Right-Click to identify..."));
            tooltip.add(new StringTextComponent("Rarity: " + item.getRarity().color + item.getRarity()));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        if (stack.getItem() instanceof ItemLegendaryTreasure) {
            ItemLegendaryTreasure item = (ItemLegendaryTreasure) stack.getItem();
            return new StringTextComponent(item.getRarity().color + "Legendary Treasure");
        }
        return super.getDisplayName(stack);
    }

    public VaultRarity getRarity() {
        return vaultRarity;
    }


}

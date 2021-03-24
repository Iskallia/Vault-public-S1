package iskallia.vault.block.item;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.StatueType;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PlayerStatueBlockItem extends BlockItem {

    public PlayerStatueBlockItem() {
        super(ModBlocks.PLAYER_STATUE, new Item.Properties()
                .group(ModItems.VAULT_MOD_GROUP)
                .maxStackSize(1));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT nbt = stack.getTag();

        if (nbt != null) {
            CompoundNBT blockEntityTag = nbt.getCompound("BlockEntityTag");
            String nickname = blockEntityTag.getString("PlayerNickname");
            boolean hasCrown = blockEntityTag.getBoolean("HasCrown");

            StringTextComponent titleText = new StringTextComponent(hasCrown ? " Arena Champion" : " Vault Boss");
            titleText.setStyle(Style.EMPTY.setColor(Color.fromInt(0xFF_ff9966)));
            tooltip.add(titleText);

            StringTextComponent text = new StringTextComponent(" Nickname: " + nickname);
            text.setStyle(Style.EMPTY.setColor(Color.fromInt(0xFF_ff9966)));
            tooltip.add(text);
        } else {
            tooltip.add(new StringTextComponent(""));
            tooltip.add(new StringTextComponent("Statue: "));
            StringTextComponent tip = new StringTextComponent(" Right-click to generate trade!");
            tip.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_FFAA00)));
            tooltip.add(tip);
            return;
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    protected boolean canPlace(BlockItemUseContext p_195944_1_, BlockState p_195944_2_) {
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (handIn == Hand.OFF_HAND) return super.onItemRightClick(worldIn, playerIn, handIn);
        ItemStack statue = LootStatueBlockItem.getStatueBlockItem(playerIn.getName().getString(), StatueType.values()[worldIn.rand.nextInt(StatueType.values().length)], false, true);
        playerIn.setHeldItem(Hand.MAIN_HAND, statue);
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}

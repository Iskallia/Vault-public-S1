package iskallia.vault.block.item;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static ItemStack forArenaChampion(String nickname) {
        ItemStack itemStack = new ItemStack(ModBlocks.PLAYER_STATUE_BLOCK_ITEM);

        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("PlayerNickname", nickname);
        nbt.putBoolean("HasCrown", true);

        CompoundNBT stackNBT = new CompoundNBT();
        stackNBT.put("BlockEntityTag", nbt);
        itemStack.setTag(stackNBT);

        return itemStack;
    }

    public static ItemStack forVaultBoss(String nickname) {
        ItemStack itemStack = new ItemStack(ModBlocks.PLAYER_STATUE_BLOCK_ITEM);

        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("PlayerNickname", nickname);
        nbt.putBoolean("HasCrown", false);

        CompoundNBT stackNBT = new CompoundNBT();
        stackNBT.put("BlockEntityTag", nbt);
        itemStack.setTag(stackNBT);

        return itemStack;
    }

}

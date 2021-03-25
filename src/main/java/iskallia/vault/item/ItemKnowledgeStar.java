package iskallia.vault.item;

import iskallia.vault.Vault;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class ItemKnowledgeStar extends Item {

    public ItemKnowledgeStar(ItemGroup group) {
        super(new Properties()
                .tab(group)
                .stacksTo(64));

        this.setRegistryName(Vault.id("knowledge_star"));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack heldItemStack = player.getItemInHand(hand);

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_LEVELUP, SoundCategory.NEUTRAL,
                0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

        if (!world.isClientSide) {
            PlayerVaultStatsData statsData = PlayerVaultStatsData.get((ServerWorld) world);
            statsData.addKnowledgePoints(((ServerPlayerEntity) player), 1);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.abilities.instabuild) {
            heldItemStack.shrink(1);
        }

        return ActionResult.sidedSuccess(heldItemStack, world.isClientSide());
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        return ((IFormattableTextComponent) super.getName(stack))
                .setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_40d7b1)));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

}

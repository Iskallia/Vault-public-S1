package iskallia.vault.block.item;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.StatueType;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class LootStatueBlockItem extends BlockItem {

    public LootStatueBlockItem(Block block) {
        super(block, new Properties()
                .tab(ModItems.VAULT_MOD_GROUP)
                .stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT nbt = stack.getTag();

        if (nbt != null) {
            CompoundNBT blockEntityTag = nbt.getCompound("BlockEntityTag");
            String nickname = blockEntityTag.getString("PlayerNickname");

            StringTextComponent text = new StringTextComponent(" Nickname: " + nickname);
            text.setStyle(Style.EMPTY.withColor(Color.fromRgb(0xFF_ff9966)));
            tooltip.add(text);
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    public static ItemStack forVaultBoss(String nickname, int variant, boolean hasCrown) {
        return getStatueBlockItem(nickname, StatueType.values()[variant], hasCrown, false);
    }

    public static ItemStack forArenaChampion(String nickname, int variant, boolean hasCrown) {
        return getStatueBlockItem(nickname, StatueType.values()[variant], hasCrown, false);
    }

    public static ItemStack forGift(String nickname, int variant, boolean hasCrown) {
        return getStatueBlockItem(nickname, StatueType.values()[variant], hasCrown, false);

    }

    public static ItemStack getStatueBlockItem(String nickname, StatueType type, boolean hasCrown, boolean blankStatue) {

        ItemStack itemStack = ItemStack.EMPTY;
        switch (type) {
            case GIFT_NORMAL:
                itemStack = new ItemStack(ModBlocks.GIFT_NORMAL_STATUE);
                break;
            case GIFT_MEGA:
                itemStack = new ItemStack(ModBlocks.GIFT_MEGA_STATUE);
                break;
            case VAULT_BOSS:
                itemStack = new ItemStack(ModBlocks.VAULT_PLAYER_LOOT_STATUE);
                break;
            case ARENA_CHAMPION:
                itemStack = new ItemStack(ModBlocks.ARENA_PLAYER_LOOT_STATUE);
                break;
        }

        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("PlayerNickname", nickname);
        nbt.putInt("StatueType", type.ordinal());
        nbt.putInt("Interval", ModConfigs.STATUE_LOOT.getInterval(type));
        ItemStack loot;
        if (blankStatue) loot = ModConfigs.STATUE_LOOT.getLoot();
        else loot = ModConfigs.STATUE_LOOT.randomLoot(type);
        nbt.put("LootItem", loot.serializeNBT());
        nbt.putBoolean("HasCrown", hasCrown);


        CompoundNBT stackNBT = new CompoundNBT();
        stackNBT.put("BlockEntityTag", nbt);
        itemStack.setTag(stackNBT);

        return itemStack;
    }


//
//    @Override
//    public ActionResultType onItemUse(ItemUseContext context) {
//        if(context.getWorld().isRemote) return ActionResultType.SUCCESS;
//
//        PlayerEntity player = context.getPlayer();
//        ModConfigs.STATUE_LOOT.dumpAll(player);
//        return ActionResultType.SUCCESS;
//        //return this.tryPlace(new BlockItemUseContext(context));
//    }

//    public ActionResultType tryPlace(BlockItemUseContext context) {
//        if (!context.canPlace()) {
//            return ActionResultType.FAIL;
//        } else {
//            BlockItemUseContext blockitemusecontext = this.getBlockItemUseContext(context);
//            if (blockitemusecontext == null) {
//                return ActionResultType.FAIL;
//            } else {
//                BlockState blockstate = this.getStateForPlacement(blockitemusecontext);
//                if (blockstate == null) {
//                    return ActionResultType.FAIL;
//                } else if (!this.placeBlock(blockitemusecontext, blockstate)) {
//                    return ActionResultType.FAIL;
//                } else {
//                    BlockPos blockpos = blockitemusecontext.getPos();
//                    World world = blockitemusecontext.getWorld();
//                    PlayerEntity playerentity = blockitemusecontext.getPlayer();
//                    ItemStack itemstack = blockitemusecontext.getItem();
//                    BlockState blockstate1 = world.getBlockState(blockpos);
//                    Block block = blockstate1.getBlock();
//                    if (block == blockstate.getBlock()) {
//                        System.out.println(block);
//                        //blockstate1 = this.updateBlockStateFromTag(blockpos, world, itemstack, blockstate1);
//                        this.onBlockPlaced(blockpos, world, playerentity, itemstack, blockstate1);
//                        block.onBlockPlacedBy(world, blockpos, blockstate1, playerentity, itemstack);
//                        if (playerentity instanceof ServerPlayerEntity) {
//                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) playerentity, blockpos, itemstack);
//                        }
//                    }
//
//                    SoundType soundtype = blockstate1.getSoundType(world, blockpos, context.getPlayer());
//                    world.playSound(playerentity, blockpos, this.getPlaceSound(blockstate1, world, blockpos, context.getPlayer()), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
//                    if (playerentity == null || !playerentity.abilities.isCreativeMode) {
//                        itemstack.shrink(1);
//                    }
//
//                    return ActionResultType.sidedSuccess(world.isRemote);
//                }
//            }
//        }
//    }
}

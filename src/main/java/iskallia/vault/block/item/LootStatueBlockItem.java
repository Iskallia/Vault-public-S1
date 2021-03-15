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

public class LootStatueBlockItem extends BlockItem {

    public LootStatueBlockItem(Block block) {
        super(block, new Properties()
                .group(ModItems.VAULT_MOD_GROUP)
                .maxStackSize(1));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT nbt = stack.getTag();

        if (nbt != null) {
            CompoundNBT blockEntityTag = nbt.getCompound("BlockEntityTag");
            String nickname = blockEntityTag.getString("PlayerNickname");

            StringTextComponent text = new StringTextComponent(" Nickname: " + nickname);
            text.setStyle(Style.EMPTY.setColor(Color.fromInt(0xFF_ff9966)));
            tooltip.add(text);
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static ItemStack forVaultBoss(String nickname, int variant, boolean hasCrown) {
        return getStatueBlockItem(nickname, variant, hasCrown, new ItemStack(ModBlocks.VAULT_PLAYER_LOOT_STATUE));
    }

    public static ItemStack forArenaChampion(String nickname, int variant, boolean hasCrown) {
        return getStatueBlockItem(nickname, variant, hasCrown, new ItemStack(ModBlocks.ARENA_PLAYER_LOOT_STATUE));
    }

    public static ItemStack forGift(String nickname, int variant, boolean hasCrown) {
        StatueType type = StatueType.values()[variant];
        switch (type) {
            case GIFT_NORMAL:
                return getStatueBlockItem(nickname, variant, hasCrown, new ItemStack(ModBlocks.GIFT_NORMAL_STATUE));
            case GIFT_MEGA:
                return getStatueBlockItem(nickname, variant, hasCrown, new ItemStack(ModBlocks.GIFT_MEGA_STATUE));
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack getStatueBlockItem(String nickname, int variant, boolean hasCrown, ItemStack itemStack) {

        StatueType type = StatueType.values()[variant];

        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("PlayerNickname", nickname);
        nbt.putInt("StatueType", variant);
        nbt.putInt("Interval", ModConfigs.STATUE_LOOT.getInterval(type));
        ItemStack loot = ModConfigs.STATUE_LOOT.randomLoot(type);
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
//                        //blockstate1 = this.func_219985_a(blockpos, world, itemstack, blockstate1);
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
//                    return ActionResultType.func_233537_a_(world.isRemote);
//                }
//            }
//        }
//    }
}

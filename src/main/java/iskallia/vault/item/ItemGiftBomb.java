package iskallia.vault.item;

import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.client.gui.overlay.GiftBombOverlay;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import net.minecraft.item.Item.Properties;

public class ItemGiftBomb extends Item {

    protected Variant variant;

    public ItemGiftBomb(ItemGroup group, Variant variant, ResourceLocation id) {
        super(new Properties()
                .tab(group)
                .stacksTo(64));

        this.variant = variant;
        this.setRegistryName(id);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        Item heldItem = heldStack.getItem();

        if (heldItem instanceof ItemGiftBomb) {
            ItemGiftBomb giftBomb = (ItemGiftBomb) heldItem;

            if (!world.isClientSide) {
                ItemStack randomLoot = ModConfigs.GIFT_BOMB.randomLoot(giftBomb.variant);

                while(randomLoot.getCount() > 0) {
                    int amount = Math.min(randomLoot.getCount(), randomLoot.getMaxStackSize());
                    ItemStack copy = randomLoot.copy();
                    copy.setCount(amount);
                    randomLoot.shrink(amount);
                    player.drop(copy, false, false);
                }

                heldStack.shrink(1);

                if (variant.ordinal != -1) {
                    CompoundNBT nbt = Optional.ofNullable(heldStack.getTag()).orElse(new CompoundNBT());
                    String gifter = nbt.getString("Gifter");
                    ItemStack gifterStatue = LootStatueBlockItem.forGift(gifter, variant.ordinal, false);
                    player.drop(gifterStatue, false, false);
                }

                Vector3d position = player.position();

                world.playSound(
                        null,
                        position.x,
                        position.y,
                        position.z,
                        ModSounds.GIFT_BOMB_SFX,
                        SoundCategory.PLAYERS,
                        0.55f, 1f
                );

                ((ServerWorld) world).sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                        position.x,
                        position.y,
                        position.z,
                        3,
                        1, 1, 1,
                        0.5
                );

            } else {
                GiftBombOverlay.pop();
            }
        }

        return ActionResult.sidedSuccess(heldStack, world.isClientSide());
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        IFormattableTextComponent displayName = (IFormattableTextComponent) super.getName(stack);
        displayName.setStyle(Style.EMPTY.withColor(colorForVariant(variant)));
        return displayName;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        Color color = colorForVariant(variant);

        if (stack.hasTag()) {
            tooltip.add(new StringTextComponent(""));

            CompoundNBT nbt = stack.getTag();
            String gifter = nbt.getString("Gifter");
            int giftedSubs = nbt.getInt("GiftedSubs");

            tooltip.add(getPropertyInfo("Gifter", gifter, color));
            tooltip.add(getPropertyInfo("Gifted", giftedSubs + " subscribers", color));
        }

        super.appendHoverText(stack, world, tooltip, flagIn);
    }

    private IFormattableTextComponent getPropertyInfo(String title, String value, Color color) {
        StringTextComponent titleComponent = new StringTextComponent(title + ": ");
        titleComponent.setStyle(Style.EMPTY.withColor(color));

        StringTextComponent valueComponent = new StringTextComponent(value);
        valueComponent.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FFFFFF)));

        return titleComponent.append(valueComponent);
    }

    private static Color colorForVariant(Variant variant) {
        if (variant == Variant.NORMAL) {
            return Color.fromRgb(0x00_bc0b0b);

        } else if (variant == Variant.SUPER) {
            return Color.fromRgb(0x00_9f0bbc);

        } else if (variant == Variant.MEGA) {
            return Color.fromRgb(0x00_0b8fbc);

        } else if (variant == Variant.OMEGA) {
            int color = (int) System.currentTimeMillis();
            return Color.fromRgb(color);
        }

        throw new InternalError("Unknown variant -> " + variant);
    }

    public static ItemStack forGift(Variant variant, String gifter, int giftedSubs) {
        ItemStack giftBomb = new ItemStack(ofVariant(variant));

        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("Gifter", gifter);
        nbt.putInt("GiftedSubs", giftedSubs);

        giftBomb.setTag(nbt);

        return giftBomb;
    }

    public static Item ofVariant(Variant variant) {
        switch (variant) {
            case NORMAL:
                return ModItems.NORMAL_GIFT_BOMB;
            case SUPER:
                return ModItems.SUPER_GIFT_BOMB;
            case MEGA:
                return ModItems.MEGA_GIFT_BOMB;
            case OMEGA:
                return ModItems.OMEGA_GIFT_BOMB;
        }

        throw new InternalError("Unknown Gift Bomb variant: " + variant);
    }

    public enum Variant {
        NORMAL(-1),
        SUPER(-1),
        MEGA(0),
        OMEGA(1);

        int ordinal;

        Variant(int ordinal) {
            this.ordinal = ordinal;
        }
    }

}

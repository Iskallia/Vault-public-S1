package iskallia.vault.item;

import iskallia.vault.Vault;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class ItemVaultFruit extends Item {

    public static Food VAULT_FRUIT_FOOD = new Food.Builder()
            .saturationMod(0).nutrition(0)
            .fast().alwaysEat().build();

    protected int extraVaultTicks;

    public ItemVaultFruit(ItemGroup group, ResourceLocation id, int extraVaultTicks) {
        super(new Properties()
                .tab(group)
                .food(VAULT_FRUIT_FOOD)
                .stacksTo(64));

        this.setRegistryName(id);

        this.extraVaultTicks = extraVaultTicks;
    }

    public int getExtraVaultTicks() {
        return extraVaultTicks;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemStack = playerIn.getItemInHand(handIn);
        if (playerIn.level.dimension() != Vault.VAULT_KEY)
            return ActionResult.fail(itemStack);
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(""));
        StringTextComponent comp = new StringTextComponent("[!] Only edible inside a Vault");
        comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FF0000)).withItalic(true));
        tooltip.add(comp);

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        IFormattableTextComponent displayName = (IFormattableTextComponent) super.getName(stack);
        return displayName.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_fcbd00)));
    }

    public static class BitterLemon extends ItemVaultFruit {
        protected DamageSource damageSource = new DamageSource("bitter_lemon").bypassArmor();

        public BitterLemon(ItemGroup group, ResourceLocation id, int extraVaultTicks) {
            super(group, id, extraVaultTicks);
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
            if (!worldIn.isClientSide && entityLiving instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entityLiving;
                VaultRaid raid = VaultRaidData.get((ServerWorld) worldIn).getActiveFor(player);
                raid.ticksLeft += getExtraVaultTicks();
                raid.sTickLeft += this.getExtraVaultTicks();

                player.hurt(this.damageSource, 6);

                worldIn.playSound(null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.CONDUIT_ACTIVATE,
                        SoundCategory.MASTER,
                        1.0F, 1.0F);
            }

            return super.finishUsingItem(stack, worldIn, entityLiving);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            StringTextComponent comp;

            tooltip.add(new StringTextComponent(""));
            comp = new StringTextComponent("A magical lemon with a bitter taste");
            comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_BEEBEE)).withItalic(true));
            tooltip.add(comp);
            comp = new StringTextComponent("It is grown on the gorgeous trees of Iskallia.");
            comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_BEEBEE)).withItalic(true));
            tooltip.add(comp);

            tooltip.add(new StringTextComponent(""));
            comp = new StringTextComponent("- Wipes away 3 hearts");
            comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FF0000)));
            tooltip.add(comp);
            comp = new StringTextComponent("- Adds 30 seconds to the Vault Timer");
            comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_00FF00)));
            tooltip.add(comp);

            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    }

    public static class SourOrange extends ItemVaultFruit {
        protected DamageSource damageSource = new DamageSource("sour_orange").bypassArmor();

        public SourOrange(ItemGroup group, ResourceLocation id, int extraVaultTicks) {
            super(group, id, extraVaultTicks);
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
            if (!worldIn.isClientSide && entityLiving instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entityLiving;
                VaultRaid raid = VaultRaidData.get((ServerWorld) worldIn).getActiveFor(player);
                raid.ticksLeft += getExtraVaultTicks();
                raid.sTickLeft += this.getExtraVaultTicks();

                player.hurt(this.damageSource, 10);

                worldIn.playSound(null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.CONDUIT_ACTIVATE,
                        SoundCategory.MASTER,
                        1.0F, 1.0F);
            }

            return super.finishUsingItem(stack, worldIn, entityLiving);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            StringTextComponent comp;

            tooltip.add(new StringTextComponent(""));
            comp = new StringTextComponent("A magical orange with a sour taste");
            comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_BEEBEE)).withItalic(true));
            tooltip.add(comp);
            comp = new StringTextComponent("It is grown on the gorgeous trees of Iskallia.");
            comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_BEEBEE)).withItalic(true));
            tooltip.add(comp);

            tooltip.add(new StringTextComponent(""));
            comp = new StringTextComponent("- Wipes away 5 hearts");
            comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FF0000)));
            tooltip.add(comp);
            comp = new StringTextComponent("- Adds 60 seconds to the Vault Timer");
            comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_00FF00)));
            tooltip.add(comp);

            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    }

    public static class MysticPear extends ItemVaultFruit {
        protected DamageSource damageSource = new DamageSource("mystic_pear").bypassArmor();

        public MysticPear(ItemGroup group, ResourceLocation id, int extraVaultTicks) {
            super(group, id, extraVaultTicks);
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
            if (!worldIn.isClientSide && entityLiving instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entityLiving;
                VaultRaid raid = VaultRaidData.get((ServerWorld) worldIn).getActiveFor(player);
                raid.ticksLeft += getExtraVaultTicks();
                raid.sTickLeft += this.getExtraVaultTicks();

                player.hurt(this.damageSource, MathUtilities.getRandomInt(10, 20));

                if (MathUtilities.randomFloat(0, 100) <= 50) {
                    player.addEffect(new EffectInstance(Effects.POISON, 30 * 20));
                } else {
                    player.addEffect(new EffectInstance(Effects.WITHER, 30 * 20));
                }

                worldIn.playSound(null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.CONDUIT_ACTIVATE,
                        SoundCategory.MASTER,
                        1.0F, 1.0F);
            }

            return super.finishUsingItem(stack, worldIn, entityLiving);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            StringTextComponent comp;

            tooltip.add(new StringTextComponent(""));
            comp = new StringTextComponent("A magical pear with a strange taste");
            comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_BEEBEE)).withItalic(true));
            tooltip.add(comp);
            comp = new StringTextComponent("It is grown on the gorgeous trees of Iskallia.");
            comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_BEEBEE)).withItalic(true));
            tooltip.add(comp);

            tooltip.add(new StringTextComponent(""));
            comp = new StringTextComponent("- Wipes away 5 to 10 hearts");
            comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FF0000)));
            tooltip.add(comp);
            comp = new StringTextComponent("- Inflicts with either Wither or Poison effect");
            comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FF0000)));
            tooltip.add(comp);
            comp = new StringTextComponent("- Adds 5 minutes to the Vault Timer");
            comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_00FF00)));
            tooltip.add(comp);

            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    }

    public static class SweetKiwi extends ItemVaultFruit {
        public SweetKiwi(ItemGroup group, ResourceLocation id, int extraVaultTicks) {
            super(group, id, extraVaultTicks);
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
            if (!worldIn.isClientSide && entityLiving instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entityLiving;
                VaultRaid raid = VaultRaidData.get((ServerWorld) worldIn).getActiveFor(player);
                raid.ticksLeft += getExtraVaultTicks();
                raid.sTickLeft += this.getExtraVaultTicks();

                worldIn.playSound(null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.CONDUIT_ACTIVATE,
                        SoundCategory.MASTER,
                        1.0F, 1.0F);
            }

            return super.finishUsingItem(stack, worldIn, entityLiving);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            StringTextComponent comp;
            tooltip.add(new StringTextComponent(""));
            comp = new StringTextComponent("- Adds 5 seconds to the Vault Timer");
            comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_00FF00)));
            tooltip.add(comp);
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    }

}

package iskallia.vault.item;

import iskallia.vault.Vault;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.RenameType;
import iskallia.vault.util.TextUtil;
import iskallia.vault.util.nbt.NBTSerializer;
import iskallia.vault.vending.Product;
import iskallia.vault.vending.Trade;
import iskallia.vault.vending.TraderCore;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import net.minecraft.item.Item.Properties;

public class ItemTraderCore extends Item {

    public ItemTraderCore(ItemGroup group, ResourceLocation id) {
        super(new Properties()
                .tab(group)
                .stacksTo(1));

        this.setRegistryName(id);
    }

    public static ItemStack generate(String nickname, int value, boolean megahead, CoreType type) {
        List<Trade> trades;
        switch (type) {
            case OMEGA:
                trades = ModConfigs.TRADER_CORE_OMEGA.TRADES.stream().filter(Trade::isValid).collect(Collectors.toList());
                break;
            case RAFFLE:
                trades = ModConfigs.TRADER_CORE_RAFFLE.TRADES.stream().filter(Trade::isValid).collect(Collectors.toList());
                break;
            default:
                trades = ModConfigs.TRADER_CORE_COMMON.TRADES.stream().filter(Trade::isValid).collect(Collectors.toList());
                break;
        }
        Collections.shuffle(trades);

        Optional<Trade> trade = trades.stream().findFirst();
        if (trade.isPresent())
            return getStackFromCore(new TraderCore(nickname, trade.get(), value, megahead, type.ordinal()), type);

        Vault.LOGGER.error("Attempted to generate a Trader Circuit.. No Trades in config.");
        return ItemStack.EMPTY;
    }

    public static ItemStack getStackFromCore(TraderCore core, CoreType type) {
        ItemStack stack;
        switch (type) {
            case OMEGA:
                stack = new ItemStack(ModItems.TRADER_CORE_OMEGA, 1);
                break;
            case RAFFLE:
                stack = new ItemStack(ModItems.TRADER_CORE_RAFFLE, 1);
                break;
            default:
                stack = new ItemStack(ModItems.TRADER_CORE, 1);
                break;
        }
        CompoundNBT nbt = new CompoundNBT();
        try {
            nbt.put("core", NBTSerializer.serialize(core));
            stack.setTag(nbt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stack;
    }

    public static TraderCore getCoreFromStack(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getTag();
        if (nbt == null) return null;
        try {
            return NBTSerializer.deserialize(TraderCore.class, nbt.getCompound("core"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT nbt = stack.getOrCreateTag();
        if (nbt.contains("core")) {
            TraderCore core;
            try {
                core = NBTSerializer.deserialize(TraderCore.class, (CompoundNBT) nbt.get("core"));
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            Trade trade = core.getTrade();
            if (trade == null) {
                tooltip.add(new StringTextComponent(""));
                tooltip.add(new StringTextComponent("Trader: "));
                StringTextComponent tip = new StringTextComponent(" Right-click to generate trade!");
                tip.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FFAA00)));
                tooltip.add(tip);
                return;
            }
            if (!trade.isValid()) return;

            Product buy = trade.getBuy();
            Product extra = trade.getExtra();
            Product sell = trade.getSell();
            tooltip.add(new StringTextComponent(""));
            tooltip.add(new StringTextComponent("Trader: "));
            StringTextComponent traderName = new StringTextComponent(" " + core.getName());
            traderName.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FFAA00)));
            tooltip.add(traderName);
            tooltip.add(new StringTextComponent(""));
            tooltip.add(new StringTextComponent("Trades: "));
            if (buy != null && buy.isValid()) {
                StringTextComponent comp = new StringTextComponent(" - Buy: ");
                TranslationTextComponent name = new TranslationTextComponent(buy.getItem().getDescriptionId());
                name.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FFAA00)));
                comp.append(name).append(new StringTextComponent(" x" + buy.getAmount()));
                tooltip.add(comp);
            }
            if (extra != null && extra.isValid()) {
                StringTextComponent comp = new StringTextComponent(" - Extra: ");
                TranslationTextComponent name = new TranslationTextComponent(extra.getItem().getDescriptionId());
                name.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FFAA00)));
                comp.append(name).append(new StringTextComponent(" x" + extra.getAmount()));
                tooltip.add(comp);
            }
            if (sell != null && sell.isValid()) {
                StringTextComponent comp = new StringTextComponent(" - Sell: ");
                TranslationTextComponent name = new TranslationTextComponent(sell.getItem().getDescriptionId());
                name.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FFAA00)));
                comp.append(name).append(new StringTextComponent(" x" + sell.getAmount()));
                tooltip.add(comp);
            }

            if (core.isMegahead()) {
                tooltip.add(new StringTextComponent(""));
                StringTextComponent comp = new StringTextComponent("MEGAHEAD!");
                comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_00FF00)));
                tooltip.add(comp);
            }

            tooltip.add(new StringTextComponent(""));
            if (trade.getTradesLeft() == 0) {
                StringTextComponent comp = new StringTextComponent("[0] Sold out, sorry!");
                comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FF0000)));
                tooltip.add(comp);
            } else if (trade.getTradesLeft() == -1) {
                StringTextComponent comp = new StringTextComponent("[\u221e] Has unlimited trades.");
                comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_00AAFF)));
                tooltip.add(comp);
            } else {
                StringTextComponent comp = new StringTextComponent("[" + trade.getTradesLeft() + "] Has a limited stock.");
                comp.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FFAA00)));
                tooltip.add(comp);
            }
        } else {
            tooltip.add(new StringTextComponent(""));
            tooltip.add(new StringTextComponent("Trader: "));
            StringTextComponent tip = new StringTextComponent(" Right-click to generate trade!");
            tip.setStyle(Style.EMPTY.withColor(Color.fromRgb(0x00_FFAA00)));
            tooltip.add(tip);
            return;
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.EPIC;
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        ITextComponent text = super.getName(stack);
        CompoundNBT nbt = stack.getOrCreateTag();

        if (nbt.contains("core", Constants.NBT.TAG_COMPOUND)) {
            try {
                TraderCore core = NBTSerializer.deserialize(TraderCore.class, nbt.getCompound("core"));
                text = new StringTextComponent(core.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return text;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity player, Hand handIn) {
        if (worldIn.isClientSide) return super.use(worldIn, player, handIn);
        if (handIn == Hand.OFF_HAND) return super.use(worldIn, player, handIn);
        ItemStack stack = player.getMainHandItem();

        if (player.isShiftKeyDown()) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("RenameType", RenameType.TRADER_CORE.ordinal());
            nbt.put("Data", stack.serializeNBT());
            NetworkHooks.openGui(
                    (ServerPlayerEntity) player,
                    new INamedContainerProvider() {
                        @Override
                        public ITextComponent getDisplayName() {
                            return new StringTextComponent("Trader Core");
                        }

                        @Nullable
                        @Override
                        public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                            return new RenamingContainer(windowId, nbt);
                        }
                    },
                    (buffer) -> {
                        buffer.writeNbt(nbt);
                    }
            );
        } else {
            CompoundNBT nbt = stack.getOrCreateTag();
            if (nbt.contains("core", Constants.NBT.TAG_COMPOUND)) {
                TraderCore core;
                try {
                    core = NBTSerializer.deserialize(TraderCore.class, nbt.getCompound("core"));
                } catch (Exception e) {
                    e.printStackTrace();
                    return super.use(worldIn, player, handIn);
                }
                if (core.getTrade() == null) {
                    String name = "Trader";
                    if (core.getName() != null && !core.getName().isEmpty()) {
                        name = core.getName();
                    }
                    ItemStack newTraderCore = generate(name, 1, false, CoreType.COMMON);
                    player.setItemInHand(Hand.MAIN_HAND, newTraderCore);
                }
            } else {
                ItemStack newTraderCore = generate("Trader", 1, false, CoreType.COMMON);
                player.setItemInHand(Hand.MAIN_HAND, newTraderCore);
            }
        }
        return super.use(worldIn, player, handIn);
    }

    public static String getTraderName(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        TraderCore core;
        try {
            core = NBTSerializer.deserialize(TraderCore.class, (CompoundNBT) nbt.get("core"));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return core.getName();
    }

    public static void updateTraderName(ItemStack stack, String newName) {
        CompoundNBT nbt = stack.getOrCreateTag();
        TraderCore core;
        try {
            core = NBTSerializer.deserialize(TraderCore.class, (CompoundNBT) nbt.get("core"));
            core.setName(newName);
            CompoundNBT coreNBT = new CompoundNBT();
            nbt.put("core", NBTSerializer.serialize(core));
            stack.setTag(coreNBT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public enum CoreType {
        COMMON(new StringTextComponent(TextFormatting.WHITE + "Common")),
        RARE(new StringTextComponent(TextFormatting.YELLOW + "Rare")),
        EPIC(new StringTextComponent(TextFormatting.LIGHT_PURPLE + "Epic")),
        OMEGA(new StringTextComponent(TextFormatting.GREEN + "Omega")),
        RAFFLE(TextUtil.applyRainbowTo("Raffle"));

        private StringTextComponent displayName;

        CoreType(StringTextComponent displayName) {
            this.displayName = displayName;
        }

        public StringTextComponent getDisplayName() {
            return this.displayName;
        }

    }
}

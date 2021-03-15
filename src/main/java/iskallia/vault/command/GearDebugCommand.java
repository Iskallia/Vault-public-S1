package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.util.EntityHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;

import java.util.Random;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class GearDebugCommand extends Command {

    private static final Random COLOR_RAND = new Random();

    @Override
    public String getName() {
        return "gear_debug";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder
                .then(literal("helmet")
                        .then(argument("model", IntegerArgumentType.integer(0, 12 - 1))
                                .executes(ctx -> giveHelmet(ctx, IntegerArgumentType.getInteger(ctx, "model")))))
                .then(literal("chestplate")
                        .then(argument("model", IntegerArgumentType.integer(0, 12 - 1))
                                .executes(ctx -> giveChestplate(ctx, IntegerArgumentType.getInteger(ctx, "model")))))
                .then(literal("leggings")
                        .then(argument("model", IntegerArgumentType.integer(0, 12 - 1))
                                .executes(ctx -> giveLeggings(ctx, IntegerArgumentType.getInteger(ctx, "model")))))
                .then(literal("boots")
                        .then(argument("model", IntegerArgumentType.integer(0, 12 - 1))
                                .executes(ctx -> giveBoots(ctx, IntegerArgumentType.getInteger(ctx, "model")))))
                .build();
    }

    private int giveHelmet(CommandContext<CommandSource> context, int model) throws CommandSyntaxException {
        ItemStack helmetStack = new ItemStack(ModItems.HELMET);
        configureGear(helmetStack, model);
        EntityHelper.giveItem(context.getSource().asPlayer(), helmetStack);
        return 0;
    }

    private int giveChestplate(CommandContext<CommandSource> context, int model) throws CommandSyntaxException {
        ItemStack chestStack = new ItemStack(ModItems.CHESTPLATE);
        configureGear(chestStack, model);
        EntityHelper.giveItem(context.getSource().asPlayer(), chestStack);
        return 0;
    }

    private int giveLeggings(CommandContext<CommandSource> context, int model) throws CommandSyntaxException {
        ItemStack leggingsStack = new ItemStack(ModItems.LEGGINGS);
        configureGear(leggingsStack, model);
        EntityHelper.giveItem(context.getSource().asPlayer(), leggingsStack);
        return 0;
    }

    private int giveBoots(CommandContext<CommandSource> context, int model) throws CommandSyntaxException {
        ItemStack bootsStack = new ItemStack(ModItems.BOOTS);
        configureGear(bootsStack, model);
        EntityHelper.giveItem(context.getSource().asPlayer(), bootsStack);
        return 0;
    }

    private void configureGear(ItemStack gearStack, int model) {
        ModAttributes.GEAR_STATE.create(gearStack, VaultGear.State.IDENTIFIED);
        gearStack.getOrCreateTag().remove("RollTicks");
        gearStack.getOrCreateTag().remove("LastModelHit");
        ModAttributes.GEAR_RARITY.create(gearStack, VaultGear.Rarity.OMEGA);
        ModAttributes.GEAR_SET.create(gearStack, VaultGear.Set.NONE);
        ModAttributes.GEAR_MODEL.create(gearStack, model);
        ModAttributes.GEAR_COLOR.create(gearStack, 0xFF_FFFFFF);
    }

    @Override
    public boolean isDedicatedServerOnly() {
        return false;
    }

}

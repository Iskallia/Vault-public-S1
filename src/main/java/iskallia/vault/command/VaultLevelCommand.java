package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class VaultLevelCommand extends Command {

    @Override
    public String getName() {
        return "vault_level";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean isDedicatedServerOnly() {
        return false;
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
                Commands.literal("add_exp")
                        .then(Commands.argument("exp", IntegerArgumentType.integer())
                                .executes(this::addExp))
        );

        builder.then(
                Commands.literal("set_level")
                        .then(Commands.argument("level", IntegerArgumentType.integer())
                                .executes(this::setLevel))
        );

        builder.then(
                Commands.literal("reset_all")
                        .executes(this::resetAll)
        );
    }

    private int setLevel(CommandContext<CommandSource> context) throws CommandSyntaxException {
        int level = IntegerArgumentType.getInteger(context, "level");
        CommandSource source = context.getSource();
        PlayerVaultStatsData.get(source.getWorld()).setVaultLevel(source.asPlayer(), level);
        return 0;
    }

    private int addExp(CommandContext<CommandSource> context) throws CommandSyntaxException {
        int exp = IntegerArgumentType.getInteger(context, "exp");
        CommandSource source = context.getSource();
        PlayerVaultStatsData.get(source.getWorld()).addVaultExp(source.asPlayer(), exp);
        return 0;
    }

    private int resetAll(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        PlayerVaultStatsData.get(source.getWorld()).reset(source.asPlayer());
        PlayerAbilitiesData.get(source.getWorld()).resetAbilityTree(source.asPlayer());
        PlayerTalentsData.get(source.getWorld()).resetTalentTree(source.asPlayer());
        PlayerResearchesData.get(source.getWorld()).resetResearchTree(source.asPlayer());
        return 0;
    }

}

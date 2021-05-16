package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import iskallia.vault.Vault;
import iskallia.vault.world.data.GlobalTraderData;
import net.minecraft.command.CommandSource;

public class ResetTraderCommand extends Command {

    @Override
    public String getName() {
        return "reset_trader";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(this::resetTrader);
    }

    private int resetTrader(CommandContext<CommandSource> commandSourceCommandContext) {
        GlobalTraderData.reset(commandSourceCommandContext.getSource().getWorld());
        return 0;
    }

    @Override
    public boolean isDedicatedServerOnly() {
        return false;
    }

}

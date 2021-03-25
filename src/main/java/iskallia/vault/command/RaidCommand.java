package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.VaultRaidData;
import net.minecraft.command.CommandSource;

import static net.minecraft.command.Commands.literal;

public class RaidCommand extends Command {

	@Override
	public String getName() {
		return "raid";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		for(Type type: Type.values()) {
			builder.then(literal(type.name().toUpperCase())
					.then(literal("start")
							.executes(context -> this.startRaid(context, type))));
		}
	}

	private int startRaid(CommandContext<CommandSource> context, Type type) throws CommandSyntaxException {
		if(type == Type.VAULT) {
			VaultRaidData.get(context.getSource().getWorld()).startNew(context.getSource().asPlayer(), ModItems.VAULT_CRYSTAL_OMEGA, false);
		} else if(type == Type.FINAL_VAULT) {
			VaultRaidData.get(context.getSource().getWorld()).startNew(context.getSource().asPlayer(), ModItems.VAULT_CRYSTAL_OMEGA, true);
		}

		return 0;
	}

	@Override
	public boolean isDedicatedServerOnly() {
		return false;
	}

	public enum Type {
		VAULT, FINAL_VAULT
	}

}

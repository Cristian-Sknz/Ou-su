package me.skiincraft.discord.ousu.commands;

import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.ousu.common.OusuCommand;
import net.dv8tion.jda.api.entities.Member;

import java.util.Collections;

public class RestartCommand extends OusuCommand {

	public RestartCommand() {
		super("restart", Collections.singletonList("corerestart"), "restart");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Owner;
	}

	public void execute(Member user, String[] args, InteractChannel channel) {
		if (!isOwner(user.getUser())) {
			return;
		}
		channel.reply("OusuCore será reiniciado.", m -> {
			m.addReaction("U+2705").queue();
			OusuCore.shutdown();
		});
	}

}

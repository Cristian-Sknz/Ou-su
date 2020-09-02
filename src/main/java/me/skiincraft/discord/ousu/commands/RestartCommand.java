package me.skiincraft.discord.ousu.commands;

import java.io.IOException;
import java.util.Arrays;

import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class RestartCommand extends Comando {

	public RestartCommand() {
		super("restart", Arrays.asList("corerestart"), "restart");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Owner;
	}

	public void execute(User user, String[] args, TextChannel channel) {
		if (!isOwner(user)) {
			return;
		}
		reply("Ousucore será reiniciado.", m -> {
			m.addReaction("U+2705").queue();
			try {
				OusuCore.restart();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

}

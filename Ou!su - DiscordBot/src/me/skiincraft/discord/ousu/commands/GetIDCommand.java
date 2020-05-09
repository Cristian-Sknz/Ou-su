package me.skiincraft.discord.ousu.commands;

import java.util.Arrays;

import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class GetIDCommand extends Commands {

	public GetIDCommand() {
		super("ou!", "getid", "getid <playername>", Arrays.asList("pegarid"));
	}

	@Override
	public String[] helpMessage(LanguageManager langm) {
		return langm.translatedArrayHelp("");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Utilidade;
	}

	@Override
	public void action(String[] args, User user, TextChannel channel) {
		if (isInsuficient()) {
			
			return;
		}
		
		if (args.length >= 2) {
			
		}
	}

}

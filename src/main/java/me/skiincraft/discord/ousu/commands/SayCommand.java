package me.skiincraft.discord.ousu.commands;

import java.util.Arrays;

import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class SayCommand extends Comando{

	public SayCommand() {
		super("say", Arrays.asList("falar", "fala"), "falar <args>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Owner;
	}

	public void execute(User user, String[] args, TextChannel channel) {
		if (!isOwner(user)) {
			reply("> Somente o Developer pode utilizar este comando.");
			return;
		}
		reply(new MessageBuilder(StringUtils.arrayToString2(0, args)).build());
	}

}

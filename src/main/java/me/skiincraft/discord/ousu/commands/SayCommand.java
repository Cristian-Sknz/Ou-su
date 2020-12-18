package me.skiincraft.discord.ousu.commands;

import java.util.Arrays;

import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.common.OusuCommand;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;

public class SayCommand extends OusuCommand {

	public SayCommand() {
		super("say", Arrays.asList("falar", "fala"), "say <args>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Owner;
	}

	public void execute(Member user, String[] args, InteractChannel channel) {
		if (!isOwner(user.getUser())) {
			channel.reply("> Somente o Developer pode utilizar este comando.");
			return;
		}
		channel.reply(new MessageBuilder(StringUtils.arrayToString2(0, args)).build());
	}

}

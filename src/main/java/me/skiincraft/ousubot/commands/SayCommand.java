package me.skiincraft.ousubot.commands;

import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.ousubot.api.AbstractCommand;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.util.Arrays;

@CommandMap
public class SayCommand extends AbstractCommand {

	public SayCommand() {
		super("say", Arrays.asList("falar", "fala"), "say <args>");
	}

	public CommandType getCategory() {
		return CommandType.Owner;
	}

	public void execute(Member user, String[] args, InteractChannel channel) {
		if (!isOwner(user.getUser())) {
			channel.reply("> Somente o Developer pode utilizar este comando.");
			return;
		}
		channel.reply(new MessageBuilder(String.join(" ", args)).build());
	}

}

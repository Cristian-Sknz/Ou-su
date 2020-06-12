package me.skiincraft.discord.ousu.commands;

import java.time.temporal.ChronoUnit;

import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.utils.Emoji;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class PingCommand extends Commands {

	public PingCommand() {
		super("ou!", "ping", "ou!ping", null);
	}

	@Override
	public String[] helpMessage(LanguageManager langm) {
		return null;
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Sobre;
	}

	@Override
	public void action(String[] args, String label, TextChannel channel) {
		MessageBuilder message = new MessageBuilder();
		message.append(getEvent().getAuthor().getAsMention() + " | " + Emoji.SMALL_BLUE_DIAMOND.getAsMention() + " Pong!");
		message.append("\n" + Emoji.TIMER.getAsMention() + "| GatewayPing: `" + channel.getJDA().getGatewayPing());
		message.append("ms`\n" + Emoji.INCOMING_ENVELOPE.getAsMention() +"| API Ping: `{?}ms`");
		channel.sendMessage(message.build()).queue(msg ->{
			long ping = getEvent().getMessage().getTimeCreated().until(msg.getTimeCreated(), ChronoUnit.MILLIS);
			message.replace("{?}", ping+"");
			msg.editMessage(message.build()).queue();
		});
	}

}

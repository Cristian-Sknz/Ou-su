package me.skiincraft.discord.ousu.commands;

import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class PingCommand extends Comando {

	public PingCommand() {
		super("ping", null, "ping");
	}

	public CommandCategory getCategory() {
		return CommandCategory.About;
	}
	
	public void execute(Member user, String[] args, InteractChannel channel) {
		final long inicialms = System.currentTimeMillis(); 
		channel.reply(message(user.getUser(), channel.getTextChannel()).build(), m -> m.editMessage(message(user.getUser(), channel.getTextChannel()).replace("{?}", (System.currentTimeMillis()-inicialms) + "").build()).queue());
	}
	
	public MessageBuilder message(User user, TextChannel channel) {
		MessageBuilder message = new MessageBuilder();
		message.append(user.getAsMention()).append(" Pong!");
		message.append("\n:timer:| GatewayPing: `").append(String.valueOf(channel.getJDA().getGatewayPing()));
		message.append("ms`\n:incoming_envelope:| API Ping: `{?}ms`");
		
		return message;
	}

}

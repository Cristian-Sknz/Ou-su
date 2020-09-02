package me.skiincraft.discord.ousu.commands;

import me.skiincraft.discord.core.utils.Emoji;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class PingCommand extends Comando {

	public PingCommand() {
		super("ping", null, "ping");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Sobre;
	}
	
	public void execute(User user, String[] args, TextChannel channel) {
		final long inicialms = System.currentTimeMillis(); 
		reply(message(user, channel).build(), m -> {
			m.editMessage(message(user, channel).replace("{?}", (System.currentTimeMillis()-inicialms) + "").build()).queue();
		});
	}
	
	public MessageBuilder message(User user, TextChannel channel) {
		MessageBuilder message = new MessageBuilder();
		message.append(user.getAsMention() + " Pong!");
		message.append("\n" + Emoji.TIMER.getAsMention() + "| GatewayPing: `" + channel.getJDA().getGatewayPing());
		message.append("ms`\n" + Emoji.INCOMING_ENVELOPE.getAsMention() +"| API Ping: `{?}ms`");
		
		return message;
	}

}

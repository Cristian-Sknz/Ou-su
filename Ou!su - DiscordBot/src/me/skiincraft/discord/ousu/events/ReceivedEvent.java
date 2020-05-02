package me.skiincraft.discord.ousu.events;

import java.awt.Color;

import me.skiincraft.discord.ousu.OusuBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReceivedEvent extends ListenerAdapter {

	public MessageEmbed v() {
		EmbedBuilder e = new EmbedBuilder();
		User user = OusuBot.getOusu().getJda().getUserById("247096601242238991");

		e.setColor(Color.red);
		e.setDescription("Esta área ainda não esta pronta, mas em breve poderá usufruir dessa estrutura!");
		e.setFooter(user.getName() + "#" + user.getDiscriminator() + " | Yagateiro Master", user.getAvatarUrl());
		e.setImage("https://i.imgur.com/LxG1qGl.gif");
		e.setTitle("Olá!", "https://discord.gg/xCkzjtm");

		return e.build();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!event.isFromType(ChannelType.PRIVATE)) {
			return;
		}

		if (event.getAuthor().isBot() == true) {
			return;
		}

		SelfUser a = event.getJDA().getSelfUser();

		if (a.getName() == event.getAuthor().getName()) {
			return;
		}

		System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(), event.getMessage().getContentDisplay());

		if (!OusuBot.listPrivated.contains(event.getAuthor().getName())) {
			event.getAuthor().openPrivateChannel().complete().sendMessage(v()).queue();
			OusuBot.listPrivated.add(event.getAuthor().getName());
		}

	}
}

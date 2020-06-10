package me.skiincraft.discord.ousu.events;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.sqlite.GuildsDB;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReceivedEvent extends ListenerAdapter {

	public static List<String> listPrivated = new ArrayList<String>();
	
	public MessageEmbed portuguese() {
		EmbedBuilder e = new EmbedBuilder();
		User user = OusuBot.getUserById("247096601242238991");

		e.setColor(Color.red);
		e.setTitle("Olá!");
		e.setDescription("Esta área ainda não esta pronta, mas em breve poderá usufruir dessa estrutura!");
		e.setFooter(user.getName() + "#" + user.getDiscriminator() + " | Yagateiro Master", user.getAvatarUrl());
		e.setImage("https://i.imgur.com/LxG1qGl.gif");

		return e.build();
	}

	public MessageEmbed english() {
		EmbedBuilder e = new EmbedBuilder();
		User user = OusuBot.getUserById("247096601242238991");

		e.setColor(Color.red);
		e.setTitle("Hi!");
		e.setDescription("This area is not yet ready, but you will soon be able to enjoy this structure!");
		e.setFooter("Ou!su Bot | Created by " + user.getName(), user.getAvatarUrl());
		e.setImage("https://i.imgur.com/LxG1qGl.gif");

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
		if (event.getAuthor().getMutualGuilds().isEmpty()) {
			return;
		}

		if (!listPrivated.contains(event.getAuthor().getName())) {
			List<Guild> mutualguilds = event.getAuthor().getMutualGuilds();

			int pt = 0;
			int en = 0;

			for (Guild guild : mutualguilds) {
				GuildsDB sql = new GuildsDB(guild);
				if (sql.exists()) {
					String lang = sql.get("language");
					if (lang.equalsIgnoreCase("portuguese")) {
						pt++;
					}
					if (lang.equalsIgnoreCase("english")) {
						en++;
					}
				}
			}

			if (pt > en) {
				event.getAuthor().openPrivateChannel().complete().sendMessage(portuguese()).queue();
			} else {
				event.getAuthor().openPrivateChannel().complete().sendMessage(english()).queue();
			}

			if (pt == en) {
				event.getAuthor().openPrivateChannel().complete().sendMessage(portuguese()).queue();
			}

			listPrivated.add(event.getAuthor().getName());
		}

	}
}

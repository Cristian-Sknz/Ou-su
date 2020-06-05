package me.skiincraft.discord.ousu.owneraccess;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.manager.DefaultReaction;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;

public class ServersCommand extends Commands {

	public ServersCommand() {
		super("ou!", "servers", "servers", null);
	}

	@Override
	public String[] helpMessage(LanguageManager langm) {
		return null;
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Owner;
	}

	@Override
	public void action(String[] args, String label, TextChannel channel) {
		if (!isOwner()) {
			return;
		}
		List<Guild> guildas = OusuBot.getJda().getGuilds();

		List<EmbedBuilder> build = new ArrayList<EmbedBuilder>();
		for (int i = 0; i < guildas.size(); i++) {
			build.add(embed(guildas, i));
		}

		Collections.sort(build, new Comparator<EmbedBuilder>() {

			@Override
			public int compare(EmbedBuilder o1, EmbedBuilder o2) {
				Date data1 = null;
				Date data2 = null;
				for (Field field : o1.getFields()) {
					if (field.getName().contains("Adicionado em")) {
						try {
							data1 = new SimpleDateFormat("dd/MM/yyyy - HH:mm").parse(field.getValue());
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}

				for (Field field : o1.getFields()) {
					if (field.getName().contains("Adicionado em")) {
						try {
							data2 = new SimpleDateFormat("dd/MM/yyyy - HH:mm").parse(field.getValue());
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				return data1.compareTo(data2);
			}
		});

		channel.sendMessage(build.get(0).build()).queue(message -> {
				message.addReaction("U+25C0").queue();
				message.addReaction("U+25B6").queue();

				EmbedBuilder[] bm = new EmbedBuilder[build.size()];
				build.toArray(bm);

				new ReactionMessage().addToCooldown(new DefaultReaction(message.getId(), bm, 0, this), 20);
		});
	}

	public EmbedBuilder embed(List<Guild> guildas, int value) {
		EmbedBuilder builder = new EmbedBuilder();
		Guild guild = guildas.get(value);
		builder.setTitle(guild.getName());

		builder.setThumbnail(guild.getIconUrl());
		builder.addField("Guild ID:", guild.getId(), true);
		builder.addField("Região:", guild.getRegion().getName(), true);

		builder.addField("Canais:",
				"Texto - " + guild.getTextChannels().size() + "\nAudio - " + guild.getVoiceChannels().size(), true);

		builder.addField("Membros", "" + guild.getMemberCount(), true);
		builder.addField("Adicionado em:", new SQLAccess(guild).get("adicionado em"), true);

		return builder;
	}

}

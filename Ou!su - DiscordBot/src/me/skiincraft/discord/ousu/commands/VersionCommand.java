package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Date;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.customemoji.OsuEmoji;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class VersionCommand extends Commands {

	public VersionCommand() {
		super("ou!", "ver", "ou!ver", Arrays.asList("version"));
	}

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("HELPMESSAGE_VERSION");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Sobre;
	}

	@Override
	public void action(String[] args, String label, User user, TextChannel channel) {
		channel.sendMessage(embed(channel.getGuild()).build()).queue();

	}

	public EmbedBuilder embed(Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();

		User user = OusuBot.getJda().getUserById("247096601242238991");
		SelfUser self = OusuBot.getJda().getSelfUser();
		embed.setAuthor(self.getName() + "#" + self.getDiscriminator(), "https://github.com/skiincraft",
				self.getAvatarUrl());
		OffsetDateTime data = self.getTimeCreated();

		embed.setDescription(getLang().translatedMessages("VERSION_COMMAND_MESSAGE")
				.replace("{emoji1}", OsuEmoji.OusuEmoji.getEmojiString()).replace("{emoji2}", ":stopwatch:") + " "
				+ new SimpleDateFormat("dd/MM/yyyy").format(Date.from(data.toInstant())));

		embed.addField("Versão", "1.1", true);

		if (guild.isMember(user)) {
			embed.addField("Author", user.getAsMention() + " - [Sknz](https://github.com/skiincraft)", true);
		} else {
			embed.addField("Author", "[Sknz](https://github.com/skiincraft)", true);
		}

		embed.setThumbnail("https://i.imgur.com/WxEN1bw.jpg");

		embed.setColor(Color.YELLOW);
		embed.setFooter(getLang().translatedBot("FOOTER_DEFAULT"), user.getAvatarUrl());
		return embed;
	}
}

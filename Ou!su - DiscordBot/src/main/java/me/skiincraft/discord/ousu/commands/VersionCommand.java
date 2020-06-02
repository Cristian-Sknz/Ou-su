package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Date;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.customemoji.OusuEmojis;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.utils.Emoji;
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
	public void action(String[] args, String label, TextChannel channel) {
		channel.sendMessage(embed(channel.getGuild()).build()).queue();

	}

	public EmbedBuilder embed(Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();

		User user = OusuBot.getJda().getUserById("247096601242238991");
		SelfUser self = OusuBot.getJda().getSelfUser();
		embed.setAuthor(self.getName() + "#" + self.getDiscriminator(), "https://github.com/skiincraft", self.getAvatarUrl());
		OffsetDateTime data = self.getTimeCreated();

		embed.setDescription(getLang().translatedMessages("VERSION_COMMAND_MESSAGE")
				.replace("{emoji1}", OusuEmojis.getEmoteAsMention("osulogo")).replace("{emoji2}", Emoji.STOPWATCH.getAsMention()) + " "
				+ new SimpleDateFormat("dd/MM/yyyy").format(Date.from(data.toInstant())));
		
		//String jDA = "<:jda:411518264267767818>";
		//String jAVA = "<:java:467443707160035329>";
		
		embed.addField("Versão", "1.1.1", true);
		embed.addField("Author", "["+ user.getName() + "#" + user.getDiscriminator() + "](https://github.com/skiincraft)", true);

		embed.setThumbnail("https://i.imgur.com/WxEN1bw.jpg");

		embed.setColor(Color.YELLOW);
		embed.setFooter(getLang().translatedBot("FOOTER_DEFAULT"), user.getAvatarUrl());
		return embed;
	}
}

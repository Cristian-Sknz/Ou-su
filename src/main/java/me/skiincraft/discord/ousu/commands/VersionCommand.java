package me.skiincraft.discord.ousu.commands;

import me.skiincraft.discord.core.utils.Emoji;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.emojis.OusuEmote;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class VersionCommand extends Comando {

	public VersionCommand() {
		super("version", Arrays.asList("ver", "info"), "version");
	}
	
	public CommandCategory getCategory() {
		return CommandCategory.Sobre;
	}

	@Override
	public void execute(User user , String[] args, TextChannel channel) {
		MessageEmbed embed = embed().build();
		reply(embed);
	}

	public EmbedBuilder embed() {
		EmbedBuilder embed = new EmbedBuilder();

		SelfUser self = Objects.requireNonNull(OusuBot.getInstance().getShardManager().getShardById(0)).getSelfUser();
		embed.setAuthor(self.getName() + "#" + self.getDiscriminator(), "https://github.com/skiincraft", self.getAvatarUrl());
		OffsetDateTime data = self.getTimeCreated();

		embed.setDescription(getLanguageManager().getString("Messages", "VERSION_COMMAND_MESSAGE").replace("{l}", "\n")
				.replace("{emoji1}", OusuEmote.getEmoteAsMention("osulogo")).replace("{emoji2}", Emoji.STOPWATCH.getAsMention()) + " "
				+ new SimpleDateFormat("dd/MM/yyyy").format(Date.from(data.toInstant())));
		
		embed.addField("Versão", "2.0.1", true);
		embed.addField("Author", "[Sknz#4260](https://github.com/skiincraft)", true);

		embed.setThumbnail("https://i.imgur.com/WxEN1bw.jpg");

		embed.setColor(Color.YELLOW);
		embed.setFooter(getLanguageManager().getString("Default", "FOOTER_DEFAULT"));
		return embed;
	}
}

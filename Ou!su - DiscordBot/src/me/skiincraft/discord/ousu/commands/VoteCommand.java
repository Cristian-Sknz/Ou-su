package me.skiincraft.discord.ousu.commands;

import java.awt.Color;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.customemoji.OsuEmoji;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class VoteCommand extends Commands {

	public VoteCommand() {
		super("ou!", "vote", "vote", null);
	}

	@Override
	public String[] helpMessage(LanguageManager langm) {
		return langm.translatedArrayHelp("HELPMESSAGE_VOTE");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Sobre;
	}

	private String voteurl = "https://top.gg/bot/701825726449582192/vote";

	@Override
	public void action(String[] args, String label, User user, TextChannel channel) {
		EmbedBuilder embed = new EmbedBuilder();
		String[] str = getLang().translatedArrayMessages("VOTE_COMMAND_MESSAGE");
		embed.setTitle(OsuEmoji.Pippi.getEmojiString() + str[0], voteurl);
		embed.setThumbnail(
				"https://cdn.discordapp.com/attachments/710231271623753738/712095645397418004/Pippi_Cartooni.png");
		// :small_orange_diamond:
		embed.setDescription(StringUtils.commandMessage(str).replace("{user}", user.getAsMention()).replace("{logo}",
				OsuEmoji.OsuLogo.getEmojiString()));

		embed.setImage("https://media.discordapp.net/attachments/710231271623753738/712106708087865354/voteimage.png");
		embed.setFooter("Link: " + voteurl, OusuBot.getSelfUser().getAvatarUrl());
		embed.setColor(Color.PINK);
		channel.sendMessage(embed.build()).queue();

	}

}

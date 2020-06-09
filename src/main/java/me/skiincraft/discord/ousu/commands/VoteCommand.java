package me.skiincraft.discord.ousu.commands;

import java.awt.Color;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.customemoji.OusuEmojis;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

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
	public void action(String[] args, String label, TextChannel channel) {
		EmbedBuilder embed = new EmbedBuilder();
		String[] str = getLang().translatedArrayMessages("VOTE_COMMAND_MESSAGE");
		embed.setTitle(OusuEmojis.getEmoteAsMention("pippi") + str[0], voteurl);
		embed.setThumbnail(
				"https://cdn.discordapp.com/attachments/710231271623753738/712095645397418004/Pippi_Cartooni.png");
		// :small_orange_diamond:
		embed.setDescription(StringUtils.commandMessage(str).replace("{user}", getUser().getAsMention()).replace("{logo}",
				OusuEmojis.getEmoteAsMention("osulogo")));

		embed.setImage("https://media.discordapp.net/attachments/710231271623753738/712106708087865354/voteimage.png");
		embed.addField("Vote :3","[Here!](" + voteurl + ")", false);
		embed.setFooter("Link: " + voteurl, OusuBot.getShardmanager().getShardById(0).getSelfUser().getAvatarUrl());
		embed.setColor(Color.PINK);
		channel.sendMessage(embed.build()).queue();

	}

}

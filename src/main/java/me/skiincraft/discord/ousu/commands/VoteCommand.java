package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;

import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.emojis.OusuEmote;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class VoteCommand extends Comando {

	public VoteCommand() {
		super("vote", Collections.singletonList("votar"), "vote");
	}
	
	public CommandCategory getCategory() {
		return CommandCategory.Sobre;
	}

	@Override
	public void execute(User user, String[] args, TextChannel channel) {
		EmbedBuilder embed = new EmbedBuilder();
		LanguageManager lang = getLanguageManager();
		String[] str = lang.getStrings("Messages", "VOTE_COMMAND_MESSAGE");
		String voteurl = "https://top.gg/bot/701825726449582192/vote";
		embed.setTitle(OusuEmote.getEmoteAsMention("pippi") + str[0], voteurl);
		embed.setThumbnail("https://cdn.discordapp.com/attachments/710231271623753738/712095645397418004/Pippi_Cartooni.png");
		// :small_orange_diamond:
		embed.setDescription(StringUtils.commandMessage(str).replace("{user}", user.getAsMention()).replace("{logo}",
				OusuEmote.getEmoteAsMention("osulogo")));

		embed.setImage("https://media.discordapp.net/attachments/710231271623753738/712106708087865354/voteimage.png");
		embed.addField("Vote :3","[Here!](" + voteurl + ")", false);
		embed.setFooter("Link: " + voteurl, OusuBot.getInstance().getShardManager().getShardById(0).getSelfUser().getAvatarUrl());
		embed.setColor(Color.PINK);
		
		reply(embed.build());

	}

}

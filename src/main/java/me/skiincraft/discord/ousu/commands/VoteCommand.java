package me.skiincraft.discord.ousu.commands;

import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.common.OusuCommand;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.util.Collections;

public class VoteCommand extends OusuCommand {

	public VoteCommand() {
		super("vote", Collections.singletonList("votar"), "vote");
	}
	
	public CommandCategory getCategory() {
		return CommandCategory.About;
	}

	@Override
	public void execute(Member user, String[] args, InteractChannel channel) {
		EmbedBuilder embed = new EmbedBuilder();
		LanguageManager lang = getLanguageManager(channel.getTextChannel().getGuild());
		String[] str = lang.getStrings("Messages", "VOTE_COMMAND_MESSAGE");
		String voteurl = "https://top.gg/bot/701825726449582192/vote";
		embed.setTitle(GenericsEmotes.getEmoteAsMention("pippi") + str[0], voteurl);
		embed.setThumbnail("https://cdn.discordapp.com/attachments/710231271623753738/712095645397418004/Pippi_Cartooni.png");
		// :small_orange_diamond:
		embed.setDescription(StringUtils.commandMessage(str).replace("{user}", user.getAsMention()).replace("{logo}",
				GenericsEmotes.getEmoteAsMention("osulogo")));

		embed.setImage("https://media.discordapp.net/attachments/710231271623753738/712106708087865354/voteimage.png");
		embed.addField("Vote :3","[Here!](" + voteurl + ")", false);
		embed.setFooter("Link: " + voteurl);
		embed.setColor(Color.PINK);
		
		channel.reply(embed.build());

	}

}

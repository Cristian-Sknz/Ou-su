package me.skiincraft.ousubot.commands;

import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.language.Language;
import me.skiincraft.ousubot.api.AbstractCommand;
import me.skiincraft.ousubot.view.emotes.GenericsEmotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

@CommandMap
public class VoteCommand extends AbstractCommand {

	public VoteCommand() {
		super("vote", Collections.singletonList("votar"), "vote");
	}
	
	public CommandType getCategory() {
		return CommandType.About;
	}

	@Override
	public void execute(Member user, String[] args, InteractChannel channel) {
		EmbedBuilder embed = new EmbedBuilder();
		Language lang = Language.getGuildLanguage(channel.getTextChannel().getGuild());

		String[] str = lang.getStrings("command.messages.vote");
		String voteUrl = "https://top.gg/bot/701825726449582192/vote";
		GenericsEmotes emotes = OusuCore.getInjector().getInstanceOf(GenericsEmotes.class);

		embed.setTitle(emotes.getEmoteAsMention("pippi") + str[0], voteUrl);
		embed.setThumbnail("https://cdn.discordapp.com/attachments/710231271623753738/712095645397418004/Pippi_Cartooni.png");
		embed.setDescription(String.join("\n", Arrays.copyOfRange(str, 1, str.length)).replace("{user}", user.getAsMention()).replace("{logo}",
				emotes.getEmoteAsMention("osulogo")));

		embed.setImage("https://media.discordapp.net/attachments/710231271623753738/712106708087865354/voteimage.png");
		embed.addField("Vote :3","[Here!](" + voteUrl + ")", false);
		embed.setFooter("Link: " + voteUrl);
		embed.setColor(Color.PINK);
		
		channel.reply(embed.build());

	}

}

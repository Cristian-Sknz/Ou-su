package me.skiincraft.discord.ousu.commands;

import java.util.Arrays;

import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.configuration.GuildDB;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.common.OusuCommand;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import me.skiincraft.discord.ousu.messages.TypeEmbed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

public class PrefixCommand extends OusuCommand {

	public PrefixCommand() {
		super("prefix", Arrays.asList("prefixo", "startwith"), "prefix <prefix>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Configuration;
	}

	public void execute(Member member, String[] args, InteractChannel channel) {
		if (!member.hasPermission(Permission.MANAGE_SERVER)) {
			//TODO message
			return;
		}
		
		if (args.length == 0) {
			replyUsage(channel.getTextChannel());
			return;
		}
		LanguageManager lang = getLanguageManager(channel.getTextChannel().getGuild());
		if (args[0].matches("[a-zA-Z0-9]*")) {
			channel.reply(formatMessage("PREFIX_INCORRECT_USE", lang));
			return;
		}
		if (args[0].length() > 3) {
			channel.reply(formatMessage("PREFIX_INCORRECT_USE2", lang));
			return;
		}

		changePrefix(args[0], channel.getTextChannel().getGuild());
		channel.reply(formatSucessful(args[0], lang));
	}
	
	public void changePrefix(String prefix, Guild guild) {
		GuildDB db = new GuildDB(guild);
		//String oldprefix = db.get("prefix");
		db.set("prefix", prefix);
		//return oldprefix;
	}
	
	public MessageEmbed formatMessage(String line, LanguageManager lang) {
		String[] str = lang.getStrings("Messages", line);
		return TypeEmbed.WarningEmbed(":x:" + str[0], StringUtils.commandMessage(str)).build();
	}
	
	public MessageEmbed formatSucessful(String newPrefix, LanguageManager lang) {
		String[] str = lang.getStrings("Messages", "PREFIX_COMMAND_MESSAGE");
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(":gear:" + str[0]);
		embed.setAuthor(newPrefix + lang.getString("PrefixCommand", "NEW_PREFIX"));
		embed.setThumbnail(GenericsEmotes.getEmoteEquals(getCategory().name()).getEmoteUrl());
		embed.addField("Prefix", newPrefix, true);

		return embed.build();
	}

}

package me.skiincraft.discord.ousu.commands;

import java.util.Arrays;

import me.skiincraft.discord.core.configuration.GuildDB;
import me.skiincraft.discord.core.utils.Emoji;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.emojis.OusuEmote;
import me.skiincraft.discord.ousu.messages.TypeEmbed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class PrefixCommand extends Comando {

	public PrefixCommand() {
		super("prefix", Arrays.asList("prefixo", "startwith"), "prefix <prefix>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Administracao;
	}

	public void execute(User user, String[] args, TextChannel channel) {
		if (!getMember(user).hasPermission(Permission.MANAGE_SERVER)) {
			//TODO message
			return;
		}
		
		if (args.length == 0) {
			replyUsage();
			return;
		}

		if (args[0].matches("[a-zA-Z0-9]*")) {
			reply(formatMessage("PREFIX_INCORRECT_USE"));
			return;
		}
		if (args[0].length() > 3) {
			reply(formatMessage("PREFIX_INCORRECT_USE2"));
			return;
		}

		reply(formatSucessful(changePrefix(args[0], channel.getGuild()), args[0]));
	}
	
	public String changePrefix(String prefix, Guild guild) {
		GuildDB db = new GuildDB(guild);
		String oldprefix = db.get("prefix");
		db.set("prefix", prefix);
		return oldprefix;
	}
	
	public MessageEmbed formatMessage(String line) {
		String[] str = getLanguageManager().getStrings("Messages", line);
		return TypeEmbed.WarningEmbed(Emoji.X.getAsMention() + str[0], StringUtils.commandMessage(str)).build();
	}
	
	public MessageEmbed formatSucessful(String oldPrefix, String newPrefix) {
		String[] str = getLanguageManager().getStrings("Messages", "PREFIX_COMMAND_MESSAGE");
		
		EmbedBuilder defaultembed = TypeEmbed.ConfigEmbed(":gear: " + str[0], OusuEmote.getEmoteAsMention("small_green_diamond")  + str[1]);

		defaultembed.addField(str[2], oldPrefix, true);
		defaultembed.addField(str[3], newPrefix, true);

		return defaultembed.build();
	}

}

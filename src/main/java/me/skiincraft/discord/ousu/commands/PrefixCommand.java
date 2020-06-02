package me.skiincraft.discord.ousu.commands;

import me.skiincraft.discord.ousu.customemoji.OusuEmojis;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import me.skiincraft.discord.ousu.utils.Emoji;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

public class PrefixCommand extends Commands {

	public PrefixCommand() {
		super("ou!", "prefix", "prefix <newprefix>", null);
	}

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("HELPMESSAGE_PREFIX");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Administracao;
	}

	@Override
	public void action(String[] args, String label, TextChannel channel) {
		if (!hasPermission(getUserId(), Permission.MANAGE_CHANNEL)) {
			noPermissionMessage(Permission.MANAGE_SERVER).queue();
			return;
		}

		if (args.length == 0) {
			sendUsage().queue();
			return;
		}

		if (args.length >= 1) {
			if (!StringUtils.containsSpecialCharacters(args[0])) {
				String[] str = getLang().translatedArrayMessages("PREFIX_INCORRECT_USE");
				sendEmbedMessage(TypeEmbed.WarningEmbed(Emoji.X.getAsMention() + str[0], StringUtils.commandMessage(str))).queue();
				return;
			}

			if (args[0].length() > 3) {
				String[] str = getLang().translatedArrayMessages("PREFIX_INCORRECT_USE2");
				sendEmbedMessage(TypeEmbed.WarningEmbed(Emoji.X.getAsMention() + str[0], StringUtils.commandMessage(str))).queue();
				return;
			}

			SQLAccess sql = new SQLAccess(channel.getGuild());

			String oldPrefix = sql.get("prefix");
			String newPrefix = args[0];
			sql.set("prefix", newPrefix);

			String[] str = getLang().translatedArrayMessages("PREFIX_COMMAND_MESSAGE");

			EmbedBuilder defaultembed = TypeEmbed.ConfigEmbed(":gear: " + str[0], OusuEmojis.getEmoteAsMention("small_green_diamond" + str[1]));

			defaultembed.addField(str[2], oldPrefix, true);
			defaultembed.addField(str[3], newPrefix, true);

			sendEmbedMessage(defaultembed.build()).queue();

			return;
		}

	}

}

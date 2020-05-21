package me.skiincraft.discord.ousu.commands;

import me.skiincraft.discord.ousu.customemoji.EmojiCustom;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

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
		return CommandCategory.Administração;
	}

	@Override
	public void action(String[] args, String label, User user, TextChannel channel) {
		if (!hasPermission(user, Permission.MANAGE_CHANNEL)) {
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
				StringBuffer buffer = new StringBuffer();
				for (String append : str) {
					if (append != str[0]) {
						buffer.append(append);
					}
				}

				sendEmbedMessage(TypeEmbed.WarningEmbed("'❌' " + str[0], buffer.toString())).queue();
				return;
			}

			if (args[0].length() > 3) {
				String[] str = getLang().translatedArrayMessages("PREFIX_INCORRECT_USE2");
				StringBuffer buffer = new StringBuffer();
				for (String append : str) {
					if (append != str[0]) {
						buffer.append(append);
					}
				}

				sendEmbedMessage(TypeEmbed.WarningEmbed("'❌' " + str[0], buffer.toString())).queue();
				return;
			}

			SQLAccess sql = new SQLAccess(channel.getGuild());
			channel.getGuild();

			String oldPrefix = sql.get("prefix");
			String newPrefix = args[0];
			sql.set("prefix", newPrefix);

			String[] str = getLang().translatedArrayMessages("PREFIX_COMMAND_MESSAGE");

			EmbedBuilder defaultembed = TypeEmbed.ConfigEmbed(":gear: " + str[0], EmojiCustom.S_GDiamond + str[1]);

			defaultembed.addField(str[2], oldPrefix, true);
			defaultembed.addField(str[3], newPrefix, true);

			sendEmbedMessage(defaultembed.build()).queue();

			return;
		}

	}

}

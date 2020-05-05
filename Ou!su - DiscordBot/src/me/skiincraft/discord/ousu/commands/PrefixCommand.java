package me.skiincraft.discord.ousu.commands;

import java.awt.Color;

import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import me.skiincraft.discord.ousu.utils.DefaultEmbed;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class PrefixCommand extends Commands {

	public PrefixCommand() {
		super("ou!", "prefix");
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
	public void action(String[] args, User user, TextChannel channel) {
		if (!hasPermission(user, Permission.MANAGE_SERVER)) {
			noPermissionMessage(Permission.MANAGE_SERVER);
			return;
		}

		if (isInsuficient()) {
			setUsage(getCommandFull() + " [args]");
			sendUsage().queue();
			return;
		}

		if (args.length >= 1) {
			if (!StringUtils.containsSpecialCharacters(args[1])) {
				String[] str = getLang().translatedArrayMessages("PREFIX_INCORRECT_USE");
				StringBuffer buffer = new StringBuffer();
				for (String append : str) {
					if (append != str[0]) {
						buffer.append(append);
					}
				}

				sendEmbedMessage(new DefaultEmbed("'❌' " + str[0], buffer.toString()).construirEmbed()).queue();
				return;
			}
			if (args[1].length() > 3) {
				String[] str = getLang().translatedArrayMessages("PREFIX_INCORRECT_USE2");
				StringBuffer buffer = new StringBuffer();
				for (String append : str) {
					if (append != str[0]) {
						buffer.append(append);
					}
				}

				sendEmbedMessage(new DefaultEmbed("'❌' " + str[0], buffer.toString()).construirEmbed()).queue();
				return;
			}

			SQLAccess sql = new SQLAccess(channel.getGuild());
			channel.getGuild();

			String oldPrefix = sql.get("prefix");
			String newPrefix = args[1];
			sql.set("prefix", newPrefix);

			String[] str = getLang().translatedArrayMessages("PREFIX_COMMAND_MESSAGE");

			EmbedBuilder defaultembed = new DefaultEmbed(":gear:" + str[0], str[1]).construirEmbed();

			defaultembed.addField(str[2], oldPrefix, true);
			defaultembed.addField(str[3], newPrefix, true);
			defaultembed.setColor(Color.GRAY);

			sendEmbedMessage(defaultembed.build()).queue();

			return;
		}

	}

}

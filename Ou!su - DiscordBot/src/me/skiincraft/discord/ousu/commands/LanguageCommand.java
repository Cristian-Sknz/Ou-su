package me.skiincraft.discord.ousu.commands;

import java.util.Arrays;

import me.skiincraft.discord.ousu.embedtypes.DefaultEmbed;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class LanguageCommand extends Commands {

	public LanguageCommand() {
		super("ou!", "language", "language <lang>", Arrays.asList("lang", "linguagem", "lingua"));
	}

	@Override
	public String[] helpMessage(LanguageManager langm) {
		return langm.translatedArrayHelp("");
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
			String[] str = getLang().translatedArrayMessages("AVAILABLE_LANGUAGE_MESSAGE");

			StringBuffer buffer = new StringBuffer();
			StringBuffer bufferlang = new StringBuffer();

			for (String append : str) {
				if (append != str[0]) {
					buffer.append(append);
				}
			}
			for (Language lang : Language.values()) {
				bufferlang.append("\n" + lang.name() + " - " + lang.getLanguageCode());
			}

			sendEmbedMessage(new DefaultEmbed(str[0], buffer.toString().replace("{LANGUAGES}", bufferlang.toString())))
					.queue();
			return;
		}

		if (args.length == 1) {
			for (Language lang : Language.values()) {
				if (lang.name().equalsIgnoreCase(args[0])) {
					SQLAccess sql = new SQLAccess(channel.getGuild());
					sql.set("language", lang.name());
					LanguageManager m = new LanguageManager(lang);

					String[] str = m.translatedArrayMessages("LANGUAGE_COMMAND_MESSAGE");
					StringBuffer buffer = new StringBuffer();
					for (String append : str) {
						if (append != str[0]) {
							buffer.append(append);
						}
					}

					sendEmbedMessage(new DefaultEmbed(str[0], buffer.toString())).queue();
					return;
				}
			}
			String[] str = getLang().translatedArrayMessages("AVAILABLE_LANGUAGE_MESSAGE");

			StringBuffer buffer = new StringBuffer();
			StringBuffer bufferlang = new StringBuffer();

			for (String append : str) {
				if (append != str[0]) {
					buffer.append(append);
				}
			}
			for (Language lang : Language.values()) {
				bufferlang.append("\n" + lang.name() + " - " + lang.getLanguageCode());
			}

			sendEmbedMessage(new DefaultEmbed(str[0], buffer.toString().replace("{LANGUAGES}", bufferlang.toString())))
					.queue();
		}

	}

}

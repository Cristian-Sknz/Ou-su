package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.util.Arrays;

import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

public class LanguageCommand extends Commands {

	public LanguageCommand() {
		super("ou!", "language", "language <lang>", Arrays.asList("lang", "linguagem", "lingua"));
	}

	@Override
	public String[] helpMessage(LanguageManager langm) {
		return langm.translatedArrayHelp("OSU_HELPMESSAGE_LANGUAGE");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Administração;
	}

	@Override
	public void action(String[] args, String label, TextChannel channel) {
		if (!hasPermission(getUserId(), Permission.MANAGE_CHANNEL)) {
			noPermissionMessage(Permission.MANAGE_SERVER).queue();
			return;
		}

		if (args.length == 0) {
			sendEmbedMessage(availablelang()).queue();
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
							buffer.append(":small_blue_diamond: " + append);
						}
					}

					EmbedBuilder variavel = TypeEmbed.ConfigEmbed(str[0], buffer.toString())
							.setThumbnail("https://i.imgur.com/sxIERAT.png")
							.setFooter("A multilanguage bot!", "https://i.imgur.com/wDczNj3.jpg")
							.setColor(new Color(52, 107, 235));

					sendEmbedMessage(variavel).queue();
					return;
				}
			}
			sendEmbedMessage(availablelang()).queue();
		}
	}

	public EmbedBuilder availablelang() {
		String[] str = getLang().translatedArrayMessages("AVAILABLE_LANGUAGE_MESSAGE");

		StringBuffer buffer = new StringBuffer();
		StringBuffer bufferlang = new StringBuffer();
		bufferlang.append("\n");

		for (String append : str) {
			if (append != str[0]) {
				buffer.append(append);
			}
		}
		for (Language lang : Language.values()) {
			bufferlang.append("\n:small_blue_diamond: " + lang.name() + " - " + lang.getLanguageCode());
		}

		return TypeEmbed.ConfigEmbed(str[0], buffer.toString().replace("{LANGUAGES}", bufferlang.toString()))
				.setThumbnail("https://i.imgur.com/sxIERAT.png")
				.setFooter("A multilanguage bot!", "https://i.imgur.com/wDczNj3.jpg").setColor(new Color(52, 107, 235));
	}

}

package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.skiincraft.discord.core.configuration.GuildDB;
import me.skiincraft.discord.core.configuration.Language;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.utils.Emoji;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.messages.TypeEmbed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class LanguageCommand extends Comando {

	public LanguageCommand() {
		super("language", Arrays.asList("idioma", "lang", "linguagem"), "language <lang>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Administracao;
	}

	public void execute(User user, String[] args, TextChannel channel) {
		if (!getMember(user).hasPermission(Permission.MANAGE_SERVER)) {
			//message
			return;
		}
		List<Language> languages = OusuBot.getMain().getPlugin().getLanguages();
		if (args.length == 0) {
			reply(languages(languages));
			return;
		}

		List<Language> filter = languages.stream()
				.filter(l -> l.getLanguageName().equalsIgnoreCase(args[0]) 
						|| l.getLanguageCode().equalsIgnoreCase(args[0])
						|| l.getName().equalsIgnoreCase(args[0])
						|| l.getCountryCode().equalsIgnoreCase(args[0])
						|| l.getCountry().equalsIgnoreCase(args[0]))
				.collect(Collectors.toList());

		if (filter.size() == 0) {
			reply(languages(languages));
			return;
		}

		LanguageManager lang = new LanguageManager(filter.get(0));
		new GuildDB(channel.getGuild()).set("language", lang.getLanguage().getLanguageName());

		String[] str = lang.getStrings("Messages", "LANGUAGE_COMMAND_MESSAGE");
		StringBuilder buffer = new StringBuilder();
		for (String append : str) {
			if (!append.equals(str[0])) buffer.append(":small_blue_diamond: " + append);
		}

		EmbedBuilder var = TypeEmbed.ConfigEmbed(str[0], buffer.toString())
				.setThumbnail("https://i.imgur.com/sxIERAT.png")
				.setFooter("A multilanguage bot!", "https://i.imgur.com/wDczNj3.jpg")
				.setColor(new Color(52, 107, 235));

		reply(var.build());
	}
	
	public MessageEmbed languages(List<Language> languages) {
		String[] str = getLanguageManager().getStrings("Messages", "AVAILABLE_LANGUAGE_MESSAGE");

		StringBuilder buffer = new StringBuilder();
		StringBuilder bufferlang = new StringBuilder();
		bufferlang.append("\n");

		for (String append : str) {
			if (!append.equals(str[0])) {
				buffer.append(append);
			}
		}
		
		for (Language lang : languages) {
			bufferlang.append("\n" + Emoji.SMALL_BLUE_DIAMOND.getAsMention() + upperFirstWord(lang.getName()) + " - " + lang.getCountry());
		}

		return TypeEmbed.ConfigEmbed(str[0], buffer.toString().replace("{LANGUAGES}", bufferlang.toString()))
				.setThumbnail("https://i.imgur.com/sxIERAT.png")
				.setFooter("A multilanguage bot!", "https://i.imgur.com/wDczNj3.jpg")
				.setColor(new Color(52, 107, 235)).build();
	}

	public String upperFirstWord(String string) {
		return String.valueOf(string.charAt(0)).toUpperCase() + string.substring(1);
	}
	
}

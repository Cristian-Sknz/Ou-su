package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.configuration.GuildDB;
import me.skiincraft.discord.core.configuration.Language;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.messages.TypeEmbed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

public class LanguageCommand extends Comando {

	public LanguageCommand() {
		super("language", Arrays.asList("idioma", "lang", "linguagem"), "language <availablelanguage>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Configuration;
	}

	public void execute(Member user, String[] args, InteractChannel channel) {
		if (!user.hasPermission(Permission.MANAGE_SERVER)) {
			//message
			return;
		}
		LanguageManager langm = getLanguageManager(channel.getTextChannel().getGuild());
		List<Language> languages = OusuCore.getLanguages();
		if (args.length == 0) {
			channel.reply(languages(languages, langm));
			return;
		}

		List<Language> filter = languages.stream()
				.filter(l -> l.getLanguageName().equalsIgnoreCase(args[0]) 
						|| l.getLanguageCode().equalsIgnoreCase(args[0])
						|| l.getName().equalsIgnoreCase(args[0])
						|| l.getCountryCode().equalsIgnoreCase(args[0])
						|| l.getCountry().equalsIgnoreCase(args[0])
						|| removeAccents(l.getLanguageName()).equalsIgnoreCase(args[0])
						|| removeAccents(l.getName()).equalsIgnoreCase(args[0])
						|| removeAccents(l.getCountry()).equalsIgnoreCase(args[0]))
				.collect(Collectors.toList());

		if (filter.size() == 0) {
			channel.reply(languages(languages, langm));
			return;
		}

		LanguageManager lang = new LanguageManager(filter.get(0));
		new GuildDB(channel.getTextChannel().getGuild()).set("language", lang.getLanguage().getLanguageName());

		String[] str = lang.getStrings("Messages", "LANGUAGE_COMMAND_MESSAGE");
		StringBuilder buffer = new StringBuilder();
		for (String append : str) {
			if (!append.equals(str[0])) buffer.append(":small_blue_diamond: ").append(append);
		}

		EmbedBuilder var = TypeEmbed.ConfigEmbed(str[0], buffer.toString())
				.setThumbnail("https://i.imgur.com/sxIERAT.png")
				.setFooter("A multilanguage bot!", "https://i.imgur.com/wDczNj3.jpg")
				.setColor(new Color(52, 107, 235));

		channel.reply(var.build());
	}
	
	public MessageEmbed languages(List<Language> languages, LanguageManager langm) {
		String[] str = langm.getStrings("Messages", "AVAILABLE_LANGUAGE_MESSAGE");

		StringBuilder buffer = new StringBuilder();
		StringBuilder bufferlang = new StringBuilder();
		bufferlang.append("\n");

		for (String append : str) {
			if (!append.equals(str[0])) {
				buffer.append(append);
			}
		}
		
		for (Language lang : languages) {
			bufferlang.append("\n:small_blue_diamond:").append(upperFirstWord(lang.getName())).append(" - ").append(lang.getCountry());
		}

		return TypeEmbed.ConfigEmbed(str[0], buffer.toString().replace("{LANGUAGES}", bufferlang.toString()))
				.setThumbnail("https://i.imgur.com/sxIERAT.png")
				.setFooter("A multilanguage bot!", "https://i.imgur.com/wDczNj3.jpg")
				.setColor(new Color(52, 107, 235)).build();
	}

	public String upperFirstWord(String string) {
		return String.valueOf(string.charAt(0)).toUpperCase() + string.substring(1);
	}

	public static String removeAccents(String text) {
		return text == null ? null : Normalizer.normalize(text, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
}

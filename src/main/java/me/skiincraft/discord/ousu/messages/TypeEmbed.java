package me.skiincraft.discord.ousu.messages;

import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.common.OusuCommand;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TypeEmbed {

	private static String randomHelpImage() {
		Random r = new Random();
		int i = r.nextInt(2);

		return i == 0 ? "https://i.imgur.com/bz1MKtv.jpg"
				: "https://i.imgur.com/pkvpKuJ.jpg";
	}

	public static EmbedBuilder errorMessage(Exception exception, TextChannel channel) {
		EmbedBuilder embed = new EmbedBuilder();
		LanguageManager lang = new LanguageManager(channel.getGuild());
		String[] string = lang.getStrings("ErrorMessage", "PROBLEM_OCURRED");

		embed.setTitle(":tickets: " + string[0]);
		embed.setAuthor("Ou!su - Discord Bot", null, channel.getJDA().getSelfUser().getAvatarUrl());
		embed.addField(string[1], "```css\n" + generateStringException(exception) +"```", false);
		embed.setFooter(lang.getString("ErrorMessage", "CONTACT_DEVELOPER"), "https://i.imgur.com/vt49jhG.png");
		embed.setColor(new Color(18, 138, 133));
		embed.setTimestamp(OffsetDateTime.now(Clock.systemUTC()));

		exception.printStackTrace();

		return embed;
	}

	private static String generateStringException(Exception e){
		StringBuilder builder = new StringBuilder();
		java.util.List<StackTraceElement> allElements = new ArrayList<>(Arrays.asList(e.getStackTrace()));
		List<StackTraceElement> traceElements = allElements.stream()
				.filter(trace -> trace.toString().contains("me.skiincraft.discord"))
				.collect(Collectors.toList());
		builder.append(e.getLocalizedMessage()).append("\n");

		if (traceElements.size() == 0){
			for (StackTraceElement element : allElements){
				if (allElements.get(allElements.size() - 1) == element) {
					builder.append("     in ").append(element.toString()).append("\n");
					break;
				}
				builder.append("   at ").append(element.toString()).append("\n");
			}
		}

		if (traceElements.size() != 0) {
			for (StackTraceElement element : allElements) {
				if (traceElements.get(traceElements.size() - 1) == element) {
					builder.append("     in ").append(element.toString()).append("\n");
					break;
				}
				builder.append("   at ").append(element.toString()).append("\n");
			}
		}
		return builder.toString();
	}

	public static EmbedBuilder inexistentUser(String user, OusuCommand.CommandCategory category, LanguageManager lang){
		String[] str = lang.getStrings("Osu", "INEXISTENT_USER");
		EmbedBuilder embed = new EmbedBuilder();
		embed.setAuthor(user);
		embed.setTitle(str[0]);
		embed.setDescription(StringUtils.commandMessage(str));
		embed.setThumbnail(GenericsEmotes.getEmoteEquals(category.name()).getEmoteUrl());
		embed.setColor(new Color(138,0, 105));
		return embed;
	}

	public static EmbedBuilder WarningEmbed(String title, String description) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(title);
		embed.setDescription(description);

		embed.setThumbnail("https://i.imgur.com/4ZkdIyq.png");// warning
		embed.setColor(Color.RED);
		embed.setFooter("ou!help to help!");

		return embed;
	}

	public static EmbedBuilder SoftWarningEmbed(String title, String description) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(title);
		embed.setDescription(description);

		embed.setThumbnail("https://i.imgur.com/mG7BgFg.png"); // Hatsunemiku chibi
		embed.setColor(new Color(222, 74, 0));// Orange+-

		embed.setFooter("ou!help to help!");
		return embed;
	}

	public static EmbedBuilder HelpEmbed(String title, String description) {
		EmbedBuilder embed = new EmbedBuilder();

		embed.setTitle(title);
		embed.setDescription(description);

		embed.setThumbnail(randomHelpImage());
		embed.setColor(Color.YELLOW);
		embed.setFooter("ou!help to help!");

		return embed;
	}

	public static EmbedBuilder InfoEmbed(String title, String description) {
		EmbedBuilder embed = new EmbedBuilder();

		embed.setTitle(title);
		embed.setDescription(description);

		embed.setThumbnail("https://i.imgur.com/QsOc85X.gif");
		embed.setColor(new Color(158, 158, 158));// Cinza
		embed.setFooter("ou!help to help!");

		return embed;
	}

	public static EmbedBuilder ConfigEmbed(String title, String description) {
		EmbedBuilder embed = new EmbedBuilder();

		embed.setTitle(title);
		embed.setDescription(description);

		embed.setThumbnail("https://i.imgur.com/SSSHW6P.png");
		embed.setColor(new Color(158, 158, 158));// Cinza
		embed.setFooter("ou!help to help!");

		return embed;
	}

	public static EmbedBuilder LoadingEmbed() {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Loading...");
		embed.setColor(Color.YELLOW);
		embed.setThumbnail("https://i.imgur.com/kPLyktW.gif");
		return embed;
	}

}

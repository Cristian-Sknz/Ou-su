package me.skiincraft.discord.ousu.messages;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Random;

public class TypeEmbed {

	private static String randomHelpImage() {
		Random r = new Random();
		int i = r.nextInt(2);

		return i == 0 ? "https://i.imgur.com/bz1MKtv.jpg"
				: "https://i.imgur.com/pkvpKuJ.jpg";
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

	public static EmbedBuilder DefaultEmbed(String title, String description) {
		EmbedBuilder b = new EmbedBuilder();

		b.setColor(Color.PINK);
		b.setTitle(title);
		b.setDescription(description);
		b.setFooter("Ou!su Bot | Created by Sknz#4260");
		return b;
	}

	public static EmbedBuilder LoadingEmbed() {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Loading...");
		embed.setColor(Color.YELLOW);
		embed.setThumbnail("https://i.imgur.com/kPLyktW.gif");
		return embed;
	}

}

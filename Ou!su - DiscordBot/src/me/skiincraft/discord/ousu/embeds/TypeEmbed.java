package me.skiincraft.discord.ousu.embeds;

import java.awt.Color;
import java.util.Random;

import net.dv8tion.jda.api.EmbedBuilder;

public class TypeEmbed {
	
	private static String randomHelpImage() {
		Random r = new Random();
		int i = r.nextInt(2);
		
		if (i == 0) {
			return "https://i.imgur.com/bz1MKtv.jpg";
		}
		if (i == 1) {
			return "https://i.imgur.com/pkvpKuJ.jpg";
		}
		return null;
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
		
		embed.setThumbnail("https://i.imgur.com/mG7BgFg.png"); //Hatsunemiku chibi
		embed.setColor(new Color(222, 74, 0));//Orange+-
		
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
		embed.setColor(new Color(158, 158, 158));//Cinza
		embed.setFooter("ou!help to help!");
		
		
		return embed;
	}

	public static EmbedBuilder ConfigEmbed(String title, String description) {
		EmbedBuilder embed = new EmbedBuilder();
		
		embed.setTitle(title);
		embed.setDescription(description);
		
		embed.setThumbnail("https://i.imgur.com/SSSHW6P.png");
		embed.setColor(new Color(158, 158, 158));//Cinza
		embed.setFooter("ou!help to help!");
		
		
		return embed;
	}
	
}

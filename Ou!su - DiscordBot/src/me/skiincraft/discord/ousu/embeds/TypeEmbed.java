package me.skiincraft.discord.ousu.embeds;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;

public class TypeEmbed {
	
	
	
	public static EmbedBuilder WarningEmbed(String title, String description) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(title);
		embed.setDescription(description);
		
		embed.setThumbnail("https://i.imgur.com/4ZkdIyq.png");// warning
		embed.setColor(Color.RED);
		embed.setFooter("", "");
		
		
		return embed;
	}
	
	public static EmbedBuilder SoftWarningEmbed(String title, String description) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(title);
		embed.setDescription(description);
		
		embed.setThumbnail("https://i.imgur.com/mG7BgFg.png"); //Hatsunemiku chibi
		embed.setColor(new Color(222, 74, 0));//Orange+-
		
		embed.setFooter("", "");
		return embed;
	}
	
	public static EmbedBuilder HelpEmbed(String title, String description) {
		EmbedBuilder embed = new EmbedBuilder();
		
		embed.setTitle(title);
		embed.setDescription(description);
		
		embed.setThumbnail("https://i.imgur.com/pkvpKuJ.jpg");// pippi chibi
		embed.setColor(Color.YELLOW);
		embed.setFooter("", "");
		
		
		return embed;
	}
	
	public static EmbedBuilder InfoEmbed(String title, String description) {
		EmbedBuilder embed = new EmbedBuilder();
		
		embed.setTitle(title);
		embed.setDescription(description);
		
		embed.setThumbnail("https://i.imgur.com/QsOc85X.gif");// add warning thumbnail
		embed.setColor(new Color(158, 158, 158));//Cinza
		embed.setFooter("", "");
		
		
		return embed;
	}

}

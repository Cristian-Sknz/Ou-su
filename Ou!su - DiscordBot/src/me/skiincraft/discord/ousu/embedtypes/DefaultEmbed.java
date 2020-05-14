package me.skiincraft.discord.ousu.embedtypes;

import java.awt.Color;

import me.skiincraft.discord.ousu.OusuBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class DefaultEmbed {

	private String title;
	private String description;
	private String imageUrl;

	public DefaultEmbed(String title, String description) {
		this.title = title;
		this.description = description;
		this.imageUrl = null;
	}

	public DefaultEmbed(String title, String description, String imageurl) {
		this.title = title;
		this.description = description;
		this.imageUrl = null;
	}

	public MessageEmbed construir() {
		EmbedBuilder b = new EmbedBuilder();
		User user = OusuBot.getJda().getUserById("247096601242238991");

		b.setColor(Color.PINK);
		b.setDescription(description);
		b.setTitle(title);
		b.setFooter("Ou!su Bot | Created by " + user.getName() + user.getDiscriminator());

		if (imageUrl == null) {
			return b.build();
		}
		b.setImage(imageUrl);

		return b.build();
	}

	public EmbedBuilder construirEmbed() {
		EmbedBuilder b = new EmbedBuilder();
		User user = OusuBot.getJda().getUserById("247096601242238991");
		b.setFooter(user.getName() + "#" + user.getDiscriminator() + " | Ou!su bot ™", user.getAvatarUrl());

		b.setColor(Color.PINK);
		b.setDescription(description);
		b.setTitle(title);

		if (imageUrl == null) {
			return b;
		}
		b.setImage(imageUrl);

		return b;
	}

}

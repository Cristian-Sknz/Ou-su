package me.skiincraft.discord.ousu.imagebuilders;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import me.skiincraft.api.ousu.users.User;
import me.skiincraft.discord.ousu.api.ImageBuilder;
import me.skiincraft.discord.ousu.api.ImageBuilder.Alignment;
import me.skiincraft.discord.ousu.utils.CustomFont;
import me.skiincraft.discord.ousu.utils.CustomFont.CustomFonts;

public class OsuCard {
	
	public static InputStream drawImage(User user) {
		try {
			ImageBuilder builder = new ImageBuilder("osu_card", 340, 94);
			builder.getGraphic().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			builder.drawImage(new File("resources/osu_images/Card_Overlay.png"), 0, 0, builder.getSize(),
					Alignment.Bottom_left);
			try {
				builder.drawImage(new URL(user.getUserAvatar()), 48, 44, new Dimension(76, 76), Alignment.Center);
			} catch (IOException ex) {
				builder.drawImage(new File("resources/osu_images/nonexistentuser.png"), 48, 44, new Dimension(76, 76),
						Alignment.Center);
			}
			builder.getGraphic().setColor(Color.WHITE);
			builder.addCentralizedStringY(user.getUserName(), 91, 28, CustomFont.getFont(CustomFonts.Aurea, Font.PLAIN, 32));
			builder.drawImage(new URL(user.getUserFlag()), 317, 25, new Dimension(28, 19), Alignment.Center);
			builder.drawImage(new File("resources/osu_images/modes/" + user.getGamemode().name().toLowerCase() + ".png"), 
					293, 26, new Dimension(15, 15), Alignment.Center);
			Font euphemia = CustomFont.getFont(CustomFonts.Euphemia, Font.PLAIN, 11);
			builder.getGraphic().setColor(Color.BLACK);
			NumberFormat nf = NumberFormat.getInstance();
			DecimalFormat df = new DecimalFormat("#.0");
			
			builder.addRightStringY("#"+ nf.format(user.getNacionalRanking())+ "", 283, 25, euphemia);
			builder.addRightStringY(df.format(user.getAccuracy())+ "%", 323, 52, euphemia.deriveFont(14L));
			builder.addRightStringY(nf.format(user.getPlayCount())+ "", 323, 70, euphemia.deriveFont(14L));
			
			return builder.buildInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}

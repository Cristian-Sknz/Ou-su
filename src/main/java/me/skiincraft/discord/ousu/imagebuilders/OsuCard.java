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
import me.skiincraft.discord.ousu.utils.CustomFont;
import me.skiincraft.discord.ousu.utils.CustomFont.CustomFonts;

public class OsuCard {
	
	public static InputStream drawImage(User user) {
		try {
			ImageBuilder builder = new ImageBuilder("osu_card", 340, 94);
			//builder.getGraphic().setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			builder.getGraphic().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			builder.addImage(new File("resources/osu_images/Card_Overlay.png"), 0, 0, new Dimension(340, 94));
			builder.addRoundedImage(new URL(user.getUserAvatar()), 10, 6, new Dimension(76, 76), 3);
			builder.getGraphic().setColor(Color.WHITE);
			builder.addCentralizedStringY(user.getUserName(), 91, 28, CustomFont.getFont(CustomFonts.Aurea, Font.PLAIN, 32));
			builder.addImage(new URL(user.getUserFlag()), 303, 16, new Dimension(28, 19));
			builder.addImage(new File("resources/osu_images/modes/" + user.getGamemode().name().toLowerCase() + ".png"), 
					286, 19, new Dimension(14, 14));
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

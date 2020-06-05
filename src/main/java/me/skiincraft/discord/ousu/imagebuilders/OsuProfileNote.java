package me.skiincraft.discord.ousu.imagebuilders;

import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import me.skiincraft.api.ousu.users.User;
import me.skiincraft.discord.ousu.api.ImageBuilder;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.utils.CustomFont;
import me.skiincraft.discord.ousu.utils.CustomFont.CustomFonts;

public class OsuProfileNote {

	public static InputStream drawImage(User osuUser, Language lang) {
		ImageBuilder builder = new ImageBuilder(osuUser.getUserID() + "_osuProfileNote", 900, 250);
		builder.getGraphic().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		try {
			if (lang != Language.English) {
				builder.addImage(new File("resources/osu_images/notes/Layer.png"), 0, 0, builder.getSize());
			} else {
				builder.addImage(new File("resources/osu_images/notes/LayerEN.png"), 0, 0, builder.getSize());
			}
			builder.getGraphic().setColor(new Color( 138, 0, 103));
			scoreCalculates(builder, osuUser.getSSh(), 150);
			scoreCalculates(builder, osuUser.getSS(), 294);
			scoreCalculates(builder, osuUser.getSh(), 444);
			scoreCalculates(builder, osuUser.getS(), 595);
			scoreCalculates(builder, osuUser.getA(), 739);

			return builder.buildInput();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static void scoreCalculates(ImageBuilder builder, int level, int x) {
		String l = level + "";
		Font cf = CustomFont.getFont(CustomFonts.ArialRound, Font.PLAIN, 34);
		// new Font("Arial", Font.PLAIN, 34)
		if (l.length() == 5) {
			builder.addCentralizedString(l, x, 211, cf);
		} else {
			//new Font("Arial", Font.PLAIN, 38)
			builder.addCentralizedString(l, x, 211, cf.deriveFont(38F));
		}
	}

}

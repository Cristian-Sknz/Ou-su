package me.skiincraft.discord.ousu.imagebuilders;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import me.skiincraft.api.ousu.users.User;
import me.skiincraft.discord.ousu.api.ImageBuilder;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;

public class OsuProfileNote {

	public static InputStream drawImage(User osuUser, Language lang) {
		ImageBuilder builder = new ImageBuilder(osuUser.getUserID() + "_osuProfileNote", 900, 250);
		try {
			if (lang != Language.English) {
				builder.addImage(new File("resources/osu_images/notes/Layer.png"), 0, 0, builder.getSize());
			} else {
				builder.addImage(new File("resources/osu_images/notes/LayerEN.png"), 0, 0, builder.getSize());
			}
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

	public static void levelCalculate(ImageBuilder builder, String level) {
		if (level.length() == 1) {
			builder.addCentralizedString(level, 911, 201, new Font("Arial", Font.PLAIN, 53));
		}

		if (level.length() == 2) {
			builder.addCentralizedString(level, 911, 198, new Font("Arial", Font.PLAIN, 43));
		}

		if (level.length() == 3) {
			builder.addCentralizedString(level, 911, 199, new Font("Arial", Font.PLAIN, 31));
		}
	}

	public static void scoreCalculates(ImageBuilder builder, int level, int x) {
		String l = level + "";
		if (l.length() == 5) {
			builder.addCentralizedString(l, x, 211, new Font("Arial", Font.PLAIN, 34));
		} else {
			builder.addCentralizedString(l, x, 211, new Font("Arial", Font.PLAIN, 38));
		}
	}

}

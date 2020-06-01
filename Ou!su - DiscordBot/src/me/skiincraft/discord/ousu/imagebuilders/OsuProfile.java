package me.skiincraft.discord.ousu.imagebuilders;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import me.skiincraft.api.ousu.users.User;
import me.skiincraft.discord.ousu.api.ImageBuilder;
import me.skiincraft.discord.ousu.api.ProfileImage;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.utils.CustomFont;
import me.skiincraft.discord.ousu.utils.CustomFont.CustomFonts;

public class OsuProfile {

	public static InputStream drawImage(User osuUser, Language lang) {
		if (lang != Language.Portuguese) {
			return drawImageLanguage(osuUser);
		}
		ProfileImage builder = new ProfileImage(osuUser.getUserID() + "_osuProfile", 1000, 750, osuUser);
		try {
			builder.addBackground("resources/osu_images/Background.png");
			builder.addAvatar(osuUser.getUserAvatar());
			builder.addOverlayer("resources/osu_images/Overlayer.png");

			// CIMA
			Font defaultfont = new Font("Arial", Font.PLAIN, 27);
			NumberFormat f = NumberFormat.getNumberInstance();

			builder.addUsername(defaultfont, 345, 85);
			builder.addAccuracy(defaultfont, 370, 120);
			builder.addUserID(defaultfont, 448, 178);
			builder.addPlayerCount(defaultfont, 449, 150);

			// MEIO
			Font midfont = CustomFont.getFont(CustomFonts.Lane_Cane, Font.PLAIN, 78F);
			builder.addRanking(midfont, 768, 354);
			builder.addNationalRanking(midfont, 611, 476);

			builder.addCountry(900, 509);
			builder.addPP(93, 270);

			// Level
			builder.getGraphic().setColor(Color.YELLOW);
			String level = new DecimalFormat("#").format(osuUser.getLevel());
			levelCalculate(builder, level);

			// Baixo
			builder.getGraphic().setColor(Color.WHITE);
			builder.addCentralizedStringY(osuUser.getPlayedHours().toString(), 746, 107, defaultfont.deriveFont(39F));
			builder.addCentralizedStringY(f.format(osuUser.getTotalScore()) + "", 45, 697,
					new Font("Arial", Font.PLAIN, 32));

			// Notas
			scoreCalculates(builder, f.format(osuUser.getSSh()), 445);
			scoreCalculates(builder, f.format(osuUser.getSS()), 562);
			scoreCalculates(builder, f.format(osuUser.getSh()), 683);
			scoreCalculates(builder, f.format(osuUser.getS()), 805);
			scoreCalculates(builder, f.format(osuUser.getA()), 921);
			
			return builder.buildInput();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static InputStream drawImageLanguage(User osuUser) {
		ProfileImage builder = new ProfileImage(osuUser.getUserID() + "_osuProfile", 1000, 750, osuUser);
		try {
			builder.addBackground("resources/osu_images/Background.png");
			builder.addAvatar(osuUser.getUserAvatar());
			builder.addOverlayer("resources/osu_images/OverlayerEN.png");

			// CIMA
			Font defaultfont = new Font("Arial", Font.PLAIN, 27);
			NumberFormat f = NumberFormat.getNumberInstance();

			builder.addUsername(defaultfont, 359, 85);
			builder.addAccuracy(defaultfont, 384, 120);
			builder.addUserID(defaultfont, 349, 179);
			builder.addPlayerCount(defaultfont, 439, 150);

			// MEIO
			Font midfont = CustomFont.getFont(CustomFonts.Lane_Cane, Font.PLAIN, 78F);
			builder.addRanking(midfont, 768, 354);
			builder.addNationalRanking(midfont, 611, 476);

			builder.addCountry(900, 509);
			builder.addPP(93, 270);

			// Level
			builder.getGraphic().setColor(Color.YELLOW);
			String level = new DecimalFormat("#").format(osuUser.getLevel());
			levelCalculate(builder, level);

			// Baixo
			builder.getGraphic().setColor(Color.WHITE);
			builder.addCentralizedStringY(osuUser.getPlayedHours().toString(), 746, 107, defaultfont.deriveFont(39F));
			builder.addCentralizedStringY(f.format(osuUser.getTotalScore()) + "", 45, 697,
					new Font("Arial", Font.PLAIN, 32));

			// Notas
			scoreCalculates(builder, f.format(osuUser.getSSh()), 445);
			scoreCalculates(builder, f.format(osuUser.getSS()), 562);
			scoreCalculates(builder, f.format(osuUser.getSh()), 683);
			scoreCalculates(builder, f.format(osuUser.getS()), 805);
			scoreCalculates(builder, f.format(osuUser.getA()), 921);

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

	public static void scoreCalculates(ImageBuilder builder, String level, int x) {
		String l = level + "";
		if (l.length() == 5) {
			builder.addCentralizedString(l, x, 662, new Font("Arial", Font.PLAIN, 34));
		} else {
			builder.addCentralizedString(l, x, 662, new Font("Arial", Font.PLAIN, 38));
		}
	}

}

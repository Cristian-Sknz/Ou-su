package me.skiincraft.discord.ousu.imagebuilders;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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

public class OsuProfile {

	public static InputStream drawImage(User osuUser) {
		DecimalFormat formatter = new DecimalFormat("#");
		ImageBuilder builder = new ImageBuilder(osuUser.getUserID() + "_osuProfile", 1000, 750);
		try {
			builder.addImage(new File("resource/osu_images/Background.png"), 0, 0, builder.getSize());
			URL url = new URL(osuUser.getUserAvatar());
			builder.addImage(url, 5, 5, new Dimension(192, 192));

			builder.addImage(new File("resource/osu_images/Overlayer.png"), 0, 0, builder.getSize());

			// CIMA
			Font defaultfont = new Font("Arial", Font.PLAIN, 27);

			String accuracy = "";
			String level = "";
			try {
				accuracy = formatter.format(osuUser.getAccuracy());
				level = formatter.format(osuUser.getLevel());
			} catch (Exception ex) {
				System.err.println("Erro ao formatar numero: " + ex);
			}
			accuracy += "%";
			
			NumberFormat f = NumberFormat.getNumberInstance();

			builder.getGraphic().setColor(new Color(0, 137, 255));
			builder.addCentralizedStringY(osuUser.getUserName(), 345, 85, defaultfont);
			builder.addCentralizedStringY(accuracy, 370, 120, defaultfont);
			builder.addCentralizedStringY(osuUser.getUserID() + "", 448, 178, defaultfont);
			builder.addCentralizedStringY(f.format(osuUser.getPlayCount()) + "", 449, 150, defaultfont);

			// MEIO
			Font midfont = CustomFont.getFont(CustomFonts.Lane_Cane, Font.PLAIN, 78F);
			builder.getGraphic().setColor(Color.YELLOW);
			builder.addCentralizedString("#" + f.format(osuUser.getRanking()), 775, 357, midfont);
			builder.addCentralizedStringY("#" + f.format(osuUser.getNacionalRanking()), 611, 476, midfont.deriveFont(44F));

			String pais = osuUser.getCountryCode();

			builder.addImage(new URL("https://osu.ppy.sh/images/flags/" + pais + ".png"), 852, 424,
					new Dimension(96, 65));
			builder.addCentralizedString(pais, 900, 509,
					CustomFont.getFont(CustomFonts.InsanityBurger, Font.PLAIN, 36F));// Bandeira

			builder.getGraphic().setColor(new Color(255, 194, 60));
			builder.addCentralizedString(osuUser.getPP() + "", 93, 270,
					CustomFont.getFont(CustomFonts.InsanityBurger, Font.PLAIN, 56F));
			// Level
			builder.getGraphic().setColor(Color.YELLOW);
			levelCalculate(builder, level);

			// Baixo
			builder.getGraphic().setColor(Color.WHITE);
			builder.addCentralizedStringY(f.format(osuUser.getTotalScore()) + "", 45, 697, new Font("Arial", Font.PLAIN, 32));

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

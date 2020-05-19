package me.skiincraft.discord.ousu.api;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import me.skiincraft.api.ousu.users.User;
import me.skiincraft.discord.ousu.utils.CustomFont;
import me.skiincraft.discord.ousu.utils.CustomFont.CustomFonts;

public class ProfileImage extends ImageBuilder {

	private User osuUser;
	private NumberFormat f = NumberFormat.getNumberInstance();

	public ProfileImage(String imagename, int w, int h, User osuUser) {
		super(imagename, w, h);
		this.osuUser = osuUser;
	}

	public void addBackground(String path) {
		try {
			addImage(new File(path), 0, 0, getSize());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addAvatar(String url) {
		try {
			addImage(new URL(osuUser.getUserAvatar()), 5, 5, new Dimension(192, 192));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addOverlayer(String path) {
		try {
			addImage(new File(path), 0, 0, getSize());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addUsername(Font defaultfont, int x, int y) {
		getGraphic().setColor(new Color(0, 137, 255));
		addCentralizedStringY(osuUser.getUserName(), x, y, defaultfont);
	}

	public void addAccuracy(Font defaultfont, int x, int y) {
		String accuracy = new DecimalFormat("#.0").format(osuUser.getAccuracy());
		accuracy += "%";
		addCentralizedStringY(accuracy, x, y, defaultfont);
	}

	public void addUserID(Font defaultfont, int x, int y) {
		addCentralizedStringY(osuUser.getUserID() + "", x, y, defaultfont);
	}

	public void addPlayerCount(Font defaultfont, int x, int y) {
		addCentralizedStringY(f.format(osuUser.getPlayCount()) + "", x, y, defaultfont);
	}

	public void addRanking(Font midfont, int x, int y) {
		getGraphic().setColor(Color.YELLOW);
		addCentralizedString("#" + f.format(osuUser.getRanking()), x, y, midfont);
	}

	public void addNationalRanking(Font midfont, int x, int y) {
		getGraphic().setColor(Color.YELLOW);
		addCentralizedStringY("#" + f.format(osuUser.getNacionalRanking()), x, y, midfont.deriveFont(44F));
	}

	public void addCountry(int x, int y) {
		try {
			addImage(new URL("https://osu.ppy.sh/images/flags/" + osuUser.getCountryCode() + ".png"), x, y,
					new Dimension(96, 65));
		} catch (IOException e) {
			e.printStackTrace();
		}

		addCentralizedString(osuUser.getCountryCode(), x + (96 / 2), y - 14,
				CustomFont.getFont(CustomFonts.InsanityBurger, Font.PLAIN, 36F));// Bandeira
	}

	public void addPP(int x, int y) {
		getGraphic().setColor(new Color(255, 194, 60));
		addCentralizedString(osuUser.getPP() + "", x, y,
				CustomFont.getFont(CustomFonts.InsanityBurger, Font.PLAIN, 56F));
	}

}

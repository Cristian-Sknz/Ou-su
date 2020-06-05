package me.skiincraft.discord.ousu.utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

public class CustomFont {

	private static String fontPath = "resources/fonts/";

	public enum CustomFonts {
		Aurea("Aurea.ttf"), BebasKai("BebasKai-Regular.ttf"), Euphemia("EUPHEMIA.TTF"),ErasDemi("ERASDEMI.TTF"), InsanityBurger("Insanibu.ttf"),
		MyFont("DISCORDIA.ttf"), Lane_UP("LANEUP_.TTF"), Lane_Cane("LANECANE.TTF"), ArialRound("ARLRDBD.TTF");
		private String fontname;

		CustomFonts(String fontname) {
			this.fontname = fontname;
		}

		public String getFontName() {
			return this.fontname;
		}
	}

	public static Font getFont(CustomFonts font, int style, float size) {
		Font f = null;
		try {
			f = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath + font.getFontName()));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(f);

			f = f.deriveFont(style);
			f = f.deriveFont(size);

			return f;
		} catch (FontFormatException e) {
			System.out.println("Foi utilizado um formato incorreto ao criar uma font (FontFormatException)");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

}

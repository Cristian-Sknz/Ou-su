package me.skiincraft.discord.ousu.imagebuilders;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.discord.ousu.api.ImageBuilder;
import me.skiincraft.discord.ousu.utils.CustomFont;
import me.skiincraft.discord.ousu.utils.CustomFont.CustomFonts;

public class OsuBeatmapSimple {

	private static String path = "resource/osu_images/";

	public static InputStream drawImage(Beatmap beatmap) {
		ImageBuilder builder = new ImageBuilder(beatmap.getBeatmapSetID() + "_cover", 900, 250);

		try {
			builder.addImage(
					new URL("https://assets.ppy.sh/beatmaps/" + beatmap.getBeatmapSetID() + "/covers/cover.jpg?"), 0, 0,
					builder.getSize());
			builder.addImage(new URL("https://a.ppy.sh/" + beatmap.getCreatorId() + "?.jpeg"), 18, 16,
					new Dimension(140, 140));

			builder.addImage(new File(path + "BeatmapOverlayer.png"), 0, 0, builder.getSize());

			Font arialRound = CustomFont.getFont(CustomFonts.ArialRound, Font.PLAIN, 36);
			Font myfont = CustomFont.getFont(CustomFonts.MyFont, Font.PLAIN, 36);

			// Criador
			builder.addCentralizedString(beatmap.getCreator(), 87, 204, arialRound);

			builder.getGraphic().setColor(new Color(68, 17, 136));
			DecimalFormat formatter = new DecimalFormat("#,##");
			String sizemap = formatter.format(beatmap.getDifficultSize()).replace(",", ":");
			builder.addCentralizedStringY(sizemap, 259, 223, arialRound);
			// BPM
			formatter = new DecimalFormat("#");
			String bpm = formatter.format(beatmap.getBPM());
			builder.addCentralizedStringY(bpm, 229, 172, myfont);
			// Nome do mapa
			Font bebaskai = CustomFont.getFont(CustomFonts.BebasKai, Font.PLAIN, 36);
			builder.addCentralizedStringY(beatmap.getTitle(), 197, 50, bebaskai);
			builder.addCentralizedStringY(beatmap.getArtist(), 245, 89, bebaskai);

			builder.addCentralizedString(beatmap.getGenre().name(), 805, 121, arialRound);

			builder.addImage(new File(path + "modes/" + getGamemode(beatmap.getGameMode())), 793, 150,
					new Dimension(74, 74));

			builder.getGraphic().setColor(new Color(21, 156, 123));
			builder.addCentralizedStringY("" + beatmap.getFavoriteCount(), 804, 29, bebaskai);
			builder.addCentralizedStringY("" + beatmap.getPlayCount(), 804, 78, bebaskai);
			return builder.buildInput();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String getGamemode(Gamemode gamemode) {
		Gamemode gm = gamemode;
		Map<Gamemode, String> map = new HashMap<>();

		map.put(Gamemode.Standard, "standard.png");
		map.put(Gamemode.Catch_the_Beat, "catch.png");
		map.put(Gamemode.Mania, "mania.png");
		map.put(Gamemode.Taiko, "taiko.png");

		if (map.containsKey(gm)) {
			return map.get(gamemode);
		}
		return "standard";
	}

}

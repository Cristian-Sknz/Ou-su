package me.skiincraft.discord.ousu.utils;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import me.skiincraft.api.ousu.entity.beatmap.Beatmap;
import me.skiincraft.api.ousu.entity.objects.Approval;
import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.api.ousu.entity.score.RecentScore;
import me.skiincraft.api.ousu.entity.score.Score;
import me.skiincraft.discord.core.utils.ImageUtils;
import me.skiincraft.discord.ousu.emojis.OusuEmote;


public class OusuUtils {
	
	public static Color beatmapColor(Beatmap beatmap) {
		try {
			return ImageUtils.getPredominatColor(ImageIO.read(new URL(beatmap.getBeatmapThumbnailUrl())));
		} catch (IOException ex) {
			return Color.YELLOW;
		}
	}
	
	public static Date getDateAfter(long aftermillis){
		Date date = new Date();
		date.setTime(date.getTime() + aftermillis);
		return date;
	}
	
	public static Date getDateBefore(long aftermillis){
		Date date = new Date();
		date.setTime(date.getTime() + aftermillis);
		return date;
	}
	
	public static String getApproval(Approval approval) {
		Map<Approval, String> map = new HashMap<>();

		map.put(Approval.Ranked, "Ranked");
		map.put(Approval.Qualified, "Qualify");
		map.put(Approval.Pending, "Pending");
		map.put(Approval.Approved, "Approvated");
		map.put(Approval.Loved, "Loved");
		map.put(Approval.Graveyard, "Graveyard");
		map.put(Approval.WIP, "WiP");

		if (map.containsKey(approval)) {
			return map.get(approval);
		}

		return "Unavailable";
	}
	
	public static String getRankEmote(RecentScore score) {
		String rank = score.getRank();
		if (rank.equalsIgnoreCase("SS+")) {
			return OusuEmote.getEmoteAsMentionEquals("ss_plus");
		}
		if (rank.equalsIgnoreCase("SS")) {
			return OusuEmote.getEmoteAsMentionEquals("ss");
		}
		if (rank.equalsIgnoreCase("X")) {
			return OusuEmote.getEmoteAsMentionEquals("ss");
		}
		if (rank.equalsIgnoreCase("S+")) {
			return OusuEmote.getEmoteAsMentionEquals("s_plus");
		}
		if (rank.equalsIgnoreCase("S")) {
			return OusuEmote.getEmoteAsMentionEquals("s_");
		}
		if (rank.equalsIgnoreCase("A")) {
			return OusuEmote.getEmoteAsMentionEquals("a_");
		}
		if (rank.equalsIgnoreCase("B")) {
			return OusuEmote.getEmoteAsMentionEquals("b_");
		}
		if (rank.equalsIgnoreCase("C")) {
			return OusuEmote.getEmoteAsMentionEquals("c_");
		}
		if (rank.equalsIgnoreCase("F")) {
			return OusuEmote.getEmoteAsMentionEquals("f_");
		}
		return OusuEmote.getEmoteAsMention("osulogo");
	}
	
	public static String getRankEmote(Score score) {
		String rank = score.getRank();
		if (rank.equalsIgnoreCase("SS+")) {
			return OusuEmote.getEmoteAsMentionEquals("ss_plus");
		}
		if (rank.equalsIgnoreCase("SS")) {
			return OusuEmote.getEmoteAsMentionEquals("ss");
		}
		if (rank.equalsIgnoreCase("X")) {
			return OusuEmote.getEmoteAsMentionEquals("ss");
		}
		if (rank.equalsIgnoreCase("S+")) {
			return OusuEmote.getEmoteAsMentionEquals("s_plus");
		}
		if (rank.equalsIgnoreCase("S")) {
			return OusuEmote.getEmoteAsMentionEquals("s_");
		}
		if (rank.equalsIgnoreCase("A")) {
			return OusuEmote.getEmoteAsMentionEquals("a_");
		}
		if (rank.equalsIgnoreCase("B")) {
			return OusuEmote.getEmoteAsMentionEquals("b_");
		}
		if (rank.equalsIgnoreCase("C")) {
			return OusuEmote.getEmoteAsMentionEquals("c_");
		}
		if (rank.equalsIgnoreCase("F")) {
			return OusuEmote.getEmoteAsMentionEquals("f_");
		}
		return OusuEmote.getEmoteAsMention("osulogo");
	}
	
	public static String getRankEmote(String score) {
		String rank = score;
		if (rank.equalsIgnoreCase("SS+")) {
			return OusuEmote.getEmoteAsMentionEquals("ss_plus");
		}
		if (rank.equalsIgnoreCase("SS")) {
			return OusuEmote.getEmoteAsMentionEquals("ss");
		}
		if (rank.equalsIgnoreCase("SSH")) {
			return OusuEmote.getEmoteAsMentionEquals("ss");
		}
		if (rank.equalsIgnoreCase("X")) {
			return OusuEmote.getEmoteAsMentionEquals("ss");
		}
		if (rank.equalsIgnoreCase("S+")) {
			return OusuEmote.getEmoteAsMentionEquals("s_plus");
		}
		if (rank.equalsIgnoreCase("SH")) {
			return OusuEmote.getEmoteAsMentionEquals("s_");
		}
		if (rank.equalsIgnoreCase("S")) {
			return OusuEmote.getEmoteAsMentionEquals("s_");
		}
		if (rank.equalsIgnoreCase("A")) {
			return OusuEmote.getEmoteAsMentionEquals("a_");
		}
		if (rank.equalsIgnoreCase("B")) {
			return OusuEmote.getEmoteAsMentionEquals("b_");
		}
		if (rank.equalsIgnoreCase("C")) {
			return OusuEmote.getEmoteAsMentionEquals("c_");
		}
		if (rank.equalsIgnoreCase("F")) {
			return OusuEmote.getEmoteAsMentionEquals("f_");
		}
		return OusuEmote.getEmoteAsMention("osulogo");
	}
	

	public static String getGamemodeString(Gamemode gamemode) {
		Gamemode gm = gamemode;
		Map<Gamemode, String> map = new HashMap<>();

		map.put(Gamemode.Standard, "standard.png");
		map.put(Gamemode.Catch, "catch.png");
		map.put(Gamemode.Mania, "mania.png");
		map.put(Gamemode.Taiko, "taiko.png");

		if (map.containsKey(gm)) {
			return map.get(gamemode);
		}
		return "standard";
	}

}

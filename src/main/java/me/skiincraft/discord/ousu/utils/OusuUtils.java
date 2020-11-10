package me.skiincraft.discord.ousu.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import me.skiincraft.api.ousu.entity.beatmap.Beatmap;
import me.skiincraft.api.ousu.entity.objects.Approval;
import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.api.ousu.entity.score.RecentScore;
import me.skiincraft.api.ousu.entity.score.Score;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;


public class OusuUtils {

	public enum DCText {
		BOLD, SPOILER, ITALIC, BOLD_ITALC
	}

	public static boolean isNumeric(String strNum) {
		Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
		if (strNum == null) {
			return false;
		}
		return pattern.matcher(strNum).matches();
	}

	public static String dcText(String text, DCText type){
		switch (type){
			case BOLD:
				return "**" + text + "**";
			case SPOILER:
				return "||" + text + "||";
			case ITALIC:
				return "*" + text + "*";
			case BOLD_ITALC:
				return "**" + text + "***";
		}
		return text;
	}
	
	public static boolean isImage(String url) {
		URLConnection connection;
		try {
			connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			BufferedImage img = ImageIO.read(connection.getInputStream());
			return img != null;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean containsSpecialCharacters(String str) {
		Pattern pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	
	public static String[] splitString(String string, String delimiter) {
		List<StringBuffer> buffer = new ArrayList<>();
		int lenght = StringUtils.quantityLetters(delimiter, string) + 1;
		for (int i = 0; i < lenght; i++) {
			buffer.add(new StringBuffer());
		}
		
		int i = 0;
		for (char c: string.toCharArray()) {
			if (c == delimiter.charAt(0)) {
				i++;
				continue;
			}
			buffer.get(i).append(c);
		}
		String[] str = new String[buffer.size()];
		i = 0;
		for (StringBuffer b:buffer) {
			str[i] = b.toString();
			i++;
		}
		
		return str;
	}
	
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
			return GenericsEmotes.getEmoteAsMentionEquals("ss_plus");
		}
		if (rank.equalsIgnoreCase("SS")) {
			return GenericsEmotes.getEmoteAsMentionEquals("ss");
		}
		if (rank.equalsIgnoreCase("X")) {
			return GenericsEmotes.getEmoteAsMentionEquals("ss");
		}
		if (rank.equalsIgnoreCase("S+")) {
			return GenericsEmotes.getEmoteAsMentionEquals("s_plus");
		}
		if (rank.equalsIgnoreCase("S")) {
			return GenericsEmotes.getEmoteAsMentionEquals("s_");
		}
		if (rank.equalsIgnoreCase("A")) {
			return GenericsEmotes.getEmoteAsMentionEquals("a_");
		}
		if (rank.equalsIgnoreCase("B")) {
			return GenericsEmotes.getEmoteAsMentionEquals("b_");
		}
		if (rank.equalsIgnoreCase("C")) {
			return GenericsEmotes.getEmoteAsMentionEquals("c_");
		}
		if (rank.equalsIgnoreCase("F")) {
			return GenericsEmotes.getEmoteAsMentionEquals("f_");
		}
		return GenericsEmotes.getEmoteAsMention("osulogo");
	}
	
	public static String getRankEmote(Score score) {
		String rank = score.getRank();
		if (rank.equalsIgnoreCase("SS+")) {
			return GenericsEmotes.getEmoteAsMentionEquals("ss_plus");
		}
		if (rank.equalsIgnoreCase("SS")) {
			return GenericsEmotes.getEmoteAsMentionEquals("ss");
		}
		if (rank.equalsIgnoreCase("X")) {
			return GenericsEmotes.getEmoteAsMentionEquals("ss");
		}
		if (rank.equalsIgnoreCase("S+")) {
			return GenericsEmotes.getEmoteAsMentionEquals("s_plus");
		}
		if (rank.equalsIgnoreCase("S")) {
			return GenericsEmotes.getEmoteAsMentionEquals("s_");
		}
		if (rank.equalsIgnoreCase("A")) {
			return GenericsEmotes.getEmoteAsMentionEquals("a_");
		}
		if (rank.equalsIgnoreCase("B")) {
			return GenericsEmotes.getEmoteAsMentionEquals("b_");
		}
		if (rank.equalsIgnoreCase("C")) {
			return GenericsEmotes.getEmoteAsMentionEquals("c_");
		}
		if (rank.equalsIgnoreCase("F")) {
			return GenericsEmotes.getEmoteAsMentionEquals("f_");
		}
		return GenericsEmotes.getEmoteAsMention("osulogo");
	}
	
	public static String getRankEmote(String score) {
		if (score.equalsIgnoreCase("SS+")) {
			return GenericsEmotes.getEmoteAsMentionEquals("ss_plus");
		}
		if (score.equalsIgnoreCase("SS")) {
			return GenericsEmotes.getEmoteAsMentionEquals("ss");
		}
		if (score.equalsIgnoreCase("SSH")) {
			return GenericsEmotes.getEmoteAsMentionEquals("ss");
		}
		if (score.equalsIgnoreCase("X")) {
			return GenericsEmotes.getEmoteAsMentionEquals("ss");
		}
		if (score.equalsIgnoreCase("S+")) {
			return GenericsEmotes.getEmoteAsMentionEquals("s_plus");
		}
		if (score.equalsIgnoreCase("SH")) {
			return GenericsEmotes.getEmoteAsMentionEquals("s_");
		}
		if (score.equalsIgnoreCase("S")) {
			return GenericsEmotes.getEmoteAsMentionEquals("s_");
		}
		if (score.equalsIgnoreCase("A")) {
			return GenericsEmotes.getEmoteAsMentionEquals("a_");
		}
		if (score.equalsIgnoreCase("B")) {
			return GenericsEmotes.getEmoteAsMentionEquals("b_");
		}
		if (score.equalsIgnoreCase("C")) {
			return GenericsEmotes.getEmoteAsMentionEquals("c_");
		}
		if (score.equalsIgnoreCase("F")) {
			return GenericsEmotes.getEmoteAsMentionEquals("f_");
		}
		return GenericsEmotes.getEmoteAsMention("osulogo");
	}
	

	public static String getGamemodeString(Gamemode gamemode) {
        Map<Gamemode, String> map = new HashMap<>();

		map.put(Gamemode.Standard, "standard.png");
		map.put(Gamemode.Catch, "catch.png");
		map.put(Gamemode.Mania, "mania.png");
		map.put(Gamemode.Taiko, "taiko.png");


		if (map.containsKey(gamemode)) {
			return map.get(gamemode);
		}
		return "standard";
	}

}

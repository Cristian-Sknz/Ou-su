package me.skiincraft.discord.ousu.utils;

import java.util.HashMap;
import java.util.Map;

import me.skiincraft.api.ousu.modifiers.Approvated;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.api.ousu.scores.Score;
import me.skiincraft.discord.ousu.customemoji.OusuEmojis;

public class OusuUtils {
	
	public static String getApproval(Approvated approval) {
		Map<Approvated, String> map = new HashMap<>();

		map.put(Approvated.Ranked, "Ranked");
		map.put(Approvated.Qualified, "Qualify");
		map.put(Approvated.Pending, "Pending");
		map.put(Approvated.Approved, "Approvated");
		map.put(Approvated.Loved, "Loved");
		map.put(Approvated.Graveyard, "Graveyard");
		map.put(Approvated.WIP, "WiP");

		if (map.containsKey(approval)) {
			return map.get(approval);
		}

		return "Não classificado";
	}
	
	public static String getRankEmote(Score score) {
		String rank = score.getRank();
		if (rank.equalsIgnoreCase("SS+")) {
			return OusuEmojis.getEmoteAsMentionEquals("ss_plus");
		}
		if (rank.equalsIgnoreCase("SS")) {
			return OusuEmojis.getEmoteAsMentionEquals("ss");
		}
		if (rank.equalsIgnoreCase("X")) {
			return OusuEmojis.getEmoteAsMentionEquals("ss");
		}
		if (rank.equalsIgnoreCase("S+")) {
			return OusuEmojis.getEmoteAsMentionEquals("s_plus");
		}
		if (rank.equalsIgnoreCase("S")) {
			return OusuEmojis.getEmoteAsMentionEquals("s_");
		}
		if (rank.equalsIgnoreCase("A")) {
			return OusuEmojis.getEmoteAsMentionEquals("a_");
		}
		if (rank.equalsIgnoreCase("B")) {
			return OusuEmojis.getEmoteAsMentionEquals("b_");
		}
		if (rank.equalsIgnoreCase("C")) {
			return OusuEmojis.getEmoteAsMentionEquals("c_");
		}
		if (rank.equalsIgnoreCase("F")) {
			return OusuEmojis.getEmoteAsMentionEquals("f_");
		}
		return OusuEmojis.getEmoteAsMention("osulogo");
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

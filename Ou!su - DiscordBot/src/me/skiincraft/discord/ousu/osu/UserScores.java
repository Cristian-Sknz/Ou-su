package me.skiincraft.discord.ousu.osu;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import com.oopsjpeg.osu4j.GameMod;
import com.oopsjpeg.osu4j.OsuBeatmap;
import com.oopsjpeg.osu4j.OsuScore;
import com.oopsjpeg.osu4j.exception.OsuAPIException;

public class UserScores {

	private UserOsu userOsu;
	private String username;

	private OsuBeatmap beatmap;
	private String beatMapCreator;

	private String date;
	private GameMod[] beatMapMods;

	private int maxCombo;
	private int mapPP;
	private String beatMapRank;

	private int totalHits;

	private int hit100;
	private int hit300;
	private int hit50;

	private int miss;

	private int gekis;
	private int katus;

	private int score;

	private int ordem;
	private String dificult;

	public int getOrdem() {
		return ordem;
	}

	@SuppressWarnings("deprecation")
	public UserScores(UserOsu userOsu, ScoreType type, int value) {
		OsuScore ss;

		List<OsuScore> scoretop = userOsu.getTopscore();
		username = userOsu.getUsername();
		if (type == ScoreType.LastScore) {
			if (userOsu.getLastscore().size() == 0) {
				return;
			}
			ss = userOsu.getLastscore().get(value);
		} else {
			if (scoretop.size() == 0) {
				return;
			}
			ss = scoretop.get(value);
		}

		try {
			beatmap = ss.getBeatmap().get();
			beatMapCreator = beatmap.getCreatorName();
		} catch (OsuAPIException e) {
			e.printStackTrace();
		}
		this.userOsu = userOsu;

		miss = ss.getMisses();
		ZonedDateTime a = ss.getDate();
		int ano = a.getYear();
		int mes = a.getMonthValue();
		int dia = a.getDayOfMonth();
		int hora = a.getHour();
		int minuto = a.getMinute();

		date = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(ano, mes, dia, hora, minuto));
		beatMapMods = ss.getEnabledMods();

		mapPP = (int) ss.getPp();
		maxCombo = ss.getMaxCombo();
		beatMapRank = ss.getRank();
		score = ss.getScore();

		totalHits = ss.getTotalHits();
		hit100 = ss.getHit100();
		hit300 = ss.getHit300();
		hit50 = ss.getHit50();

		gekis = ss.getGekis();
		katus = ss.getKatus();

		dificult = beatmap.getVersion();

	}

	public UserOsu getUserOsu() {
		return userOsu;
	}

	public String getAvatarURL() {
		return userOsu.getAvatarURL();
	}

	public String getUsername() {
		return username;
	}

	public OsuBeatmap getBeatmap() {
		return beatmap;
	}

	public String getDate() {
		return date;
	}

	public GameMod[] getBeatMapMods() {
		return beatMapMods;
	}

	public int getMaxCombo() {
		return maxCombo;
	}

	public int getMapPP() {
		return mapPP;
	}

	public String getBeatMapRank() {
		return beatMapRank;
	}

	public int getTotalHits() {
		return totalHits;
	}

	public int getHit100() {
		return hit100;
	}

	public int getHit300() {
		return hit300;
	}

	public int getHit50() {
		return hit50;
	}

	public int getGekis() {
		return gekis;
	}

	public int getKatus() {
		return katus;
	}

	public int getMiss() {
		return miss;
	}

	public String getBeatMapCreator() {
		return beatMapCreator;
	}

	public int getScore() {
		return score;
	}

	public String getDificult() {
		return dificult;
	}
}

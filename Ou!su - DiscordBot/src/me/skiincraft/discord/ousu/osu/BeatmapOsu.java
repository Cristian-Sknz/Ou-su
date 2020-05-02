package me.skiincraft.discord.ousu.osu;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

import com.oopsjpeg.osu4j.GameMode;
import com.oopsjpeg.osu4j.Genre;
import com.oopsjpeg.osu4j.Language;
import com.oopsjpeg.osu4j.OsuBeatmap;
import com.oopsjpeg.osu4j.OsuBeatmapSet;
import com.oopsjpeg.osu4j.OsuUser;
import com.oopsjpeg.osu4j.abstractbackend.LazilyLoaded;
import com.oopsjpeg.osu4j.backend.EndpointBeatmaps;
import com.oopsjpeg.osu4j.exception.OsuAPIException;

import me.skiincraft.discord.ousu.utils.IntegerUtils;

public class BeatmapOsu extends OsuAPI {

	private String aprovação;
	private String artista;
	private float bpm;
	private LazilyLoaded<OsuUser> criador;
	private Genre genero;
	private int combo;
	private GameMode gamemode;
	private String titulo;
	private String dificuldade;

	private LazilyLoaded<OsuBeatmapSet> beatmapset;
	private OsuBeatmap beatmap;
	private String criadorname;
	private URL beatMapURL;
	private String data;
	private String sucessfull;
	private Language language;

	public BeatmapOsu(int BeatMapID) throws MalformedURLException, OsuAPIException {

		beatmap = getOsu().beatmaps.getAsQuery(new EndpointBeatmaps.ArgumentsBuilder().setBeatmapID(BeatMapID).build())
				.resolve().get(0);

		this.aprovação = beatmap.getApproved().getName();
		this.data = new SimpleDateFormat("dd/MM/yyyy").format(convertZonedDate(beatmap.getApprovedDate()));

		this.artista = beatmap.getArtist();
		this.bpm = beatmap.getBPM();
		this.criador = beatmap.getCreator();
		this.genero = beatmap.getGenre();
		this.combo = beatmap.getMaxCombo();
		this.gamemode = beatmap.getMode();
		this.titulo = beatmap.getTitle();
		this.dificuldade = beatmap.getVersion();

		this.language = beatmap.getLanguage();

		this.sucessfull = IntegerUtils.getPorcentagem(beatmap.getPlayCount(), beatmap.getPassCount());

		this.beatmapset = beatmap.getBeatmapSet();
		this.criadorname = beatmap.getCreatorName();
		this.beatMapURL = beatmap.getURL();
	}

	public String getSucessfull() {
		return sucessfull;
	}

	private Date convertZonedDate(ZonedDateTime zoned) {
		Date date = Date.from(zoned.toInstant());
		return date;
	}

	public URL getBeatMapURL() {
		return beatMapURL;
	}

	public String getBeatmapCover() {
		return "https://assets.ppy.sh/beatmaps/" + beatmap.getBeatmapSetID() + "/covers/cover.jpg";
	}

	public String getBeatmapThumbnail() {
		return "https://b.ppy.sh/thumb/" + beatmap.getBeatmapSetID() + "l.jpg";
	}

	public InputStream getBeatmapPreview() {
		URLConnection conn;
		try {
			conn = new URL("http://b.ppy.sh/preview/" + beatmap.getBeatmapSetID() + ".mp3").openConnection();
			InputStream is = conn.getInputStream();
			return is;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public String getMapStars() {
		DecimalFormat decimal = new DecimalFormat("#.0");
		String result = decimal.format(beatmap.getDifficulty());

		String dif = beatmap.getDifficulty() + "";
		int one = Integer.valueOf(dif.charAt(0) + "");
		int two = Integer.valueOf(dif.charAt(2) + "");

		String fullstars = "";
		for (int i = 0; i < one; i++) {
			fullstars += "★";
		}
		if (two >= 5) {
			return "**" + fullstars + "✩** (" + result + ")";
		} else {
			return "**" + fullstars + "** (" + result + ")";
		}
	}

	public String getAprovação() {
		return aprovação;
	}

	public String getArtista() {
		return artista;
	}

	public float getBpm() {
		return bpm;
	}

	public LazilyLoaded<OsuUser> getCriador() {
		return criador;
	}

	public Genre getGenero() {
		return genero;
	}

	public int getCombo() {
		return combo;
	}

	public GameMode getGamemode() {
		return gamemode;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getCriadorName() {
		return criadorname;
	}

	public String getDificuldade() {
		return dificuldade;
	}

	public LazilyLoaded<OsuBeatmapSet> getBeatmapset() {
		return beatmapset;
	}

	public OsuBeatmap getBeatmap() {
		return beatmap;
	}

	public String getData() {
		return data;
	}

	public String getLanguageEmoji() {
		if (language == Language.ANY) {
			return ":flag_black:";
		}
		if (language == Language.CHINESE) {
			return ":flag_cn:";
		}
		if (language == Language.ENGLISH) {
			return ":flag_us:";
		}
		if (language == Language.FRENCH) {
			return ":flag_fr:";
		}
		if (language == Language.INSTRUMENTAL) {
			return ":musical_keyboard:";
		}
		if (language == Language.ITALIAN) {
			return ":flag_it:";
		}
		if (language == Language.JAPANESE) {
			return ":flag_jp:";
		}
		if (language == Language.KOREAN) {
			return ":flag_kr:";
		}
		if (language == Language.OTHER) {
			return ":flag_white:";
		}
		if (language == Language.SPANISH) {
			return ":flag_es:";
		}
		if (language == Language.SWEDISH) {
			return ":flag_se:";
		}
		return ":flag_black:";
	}

}

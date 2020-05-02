package me.skiincraft.discord.ousu.osu;

import java.net.MalformedURLException;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;
import com.oopsjpeg.osu4j.GameMode;
import com.oopsjpeg.osu4j.OsuScore;
import com.oopsjpeg.osu4j.OsuUser;
import com.oopsjpeg.osu4j.backend.EndpointUserRecents;
import com.oopsjpeg.osu4j.backend.EndpointUsers;
import com.oopsjpeg.osu4j.exception.OsuAPIException;

public class UserOsu extends OsuAPI {

	private OsuUser user;
	private int userid;

	public int getUserid() {
		return userid;
	}

	private String username;

	private String userUrl;

	private GameMode gamemode;

	private int rank;
	private int nacionalRank;

	private CountryCode pais;

	private long totalscore;

	private int pp;

	private List<OsuScore> topScore;
	private List<OsuScore> lastScore;

	public UserOsu(String username, GameMode mode) throws MalformedURLException, OsuAPIException {
		this.username = username;
		this.gamemode = mode;

		loadUser(username, mode);

		this.userUrl = user.getURL().toString();
		this.userid = user.getID();

		this.rank = user.getRank();
		this.nacionalRank = user.getCountryRank();
		this.totalscore = user.getTotalScore();
		this.pais = user.getCountry();

		this.pp = user.getPP();

		this.topScore = user.getTopScores(5).get();
		this.username = user.getUsername();

		try {
			this.lastScore = getOsu().userRecents
					.getAsQuery(
							new EndpointUserRecents.ArgumentsBuilder(user.getID()).setMode(mode).setLimit(3).build())
					.asLazilyLoaded().get();
		} catch (NullPointerException e) {
			this.lastScore = null;
		}

	}

	public UserOsu(int userID, GameMode mode) throws MalformedURLException, OsuAPIException {
		this.gamemode = mode;

		this.user = getOsu().users.query(new EndpointUsers.ArgumentsBuilder(userID).setMode(mode).build());

		this.userUrl = user.getURL().toString();

		this.rank = user.getRank();
		this.nacionalRank = user.getCountryRank();
		this.totalscore = user.getTotalScore();
		this.pais = user.getCountry();

		this.pp = user.getPP();

		this.topScore = user.getTopScores(5).get();
		this.username = user.getUsername();
		try {
			this.lastScore = getOsu().userRecents
					.getAsQuery(new EndpointUserRecents.ArgumentsBuilder(userID).setMode(mode).setLimit(3).build())
					.asLazilyLoaded().get();
		} catch (NullPointerException e) {
			this.lastScore = null;
		}

	}

	public synchronized void loadUser(String username, GameMode mode) throws OsuAPIException {
		this.user = getOsu().users.query(new EndpointUsers.ArgumentsBuilder(username).setMode(mode).build());
	}

	public OsuUser getUser() {
		return user;
	}

	public GameMode getGameMode() {
		return gamemode;
	}

	public String getUsername() {
		return username;
	}

	public String getUserUrl() {
		return userUrl;
	}

	public int getRank() {
		return rank;
	}

	public long getTotalscore() {
		return totalscore;
	}

	public int getPp() {
		return pp;
	}

	public List<OsuScore> getTopscore() {
		return topScore;
	}

	public List<OsuScore> getLastscore() {
		return lastScore;
	}

	public int getNacionalRank() {
		return nacionalRank;
	}

	public String getAvatarURL() {
		return "https://a.ppy.sh/" + user.getID();
	}

	public CountryCode getPais() {
		return pais;
	}
}

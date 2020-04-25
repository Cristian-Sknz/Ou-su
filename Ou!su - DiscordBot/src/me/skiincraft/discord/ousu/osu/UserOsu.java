package me.skiincraft.discord.ousu.osu;

import java.net.MalformedURLException;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;
import com.oopsjpeg.osu4j.GameMode;
import com.oopsjpeg.osu4j.OsuScore;

import com.oopsjpeg.osu4j.backend.EndpointUsers;
import com.oopsjpeg.osu4j.exception.OsuAPIException;
import com.oopsjpeg.osu4j.OsuUser;

public class UserOsu extends OsuAPI{
	
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
		//this.lastScore = getOsu().userRecents.query(new EndpointUserRecents.ArgumentsBuilder(username).setMode(gamemode).setLimit(5).build());
		} catch(NullPointerException e) {
			//
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
		//this.lastScore = user.withRecentScores(5).get();
		
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
		/*
		InputStream input = null;
		try {
			input = new URL("https://old.ppy.sh/u/" + getUsername()).openStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		Document document = new Tidy().parseDOM(input, null);
		NodeList imgs = document.getElementsByTagName("img");
		
		List<String> srcs = new ArrayList<String>();
		for (int i = 0; i < imgs.getLength(); i++) {
		    srcs.add(imgs.item(i).getAttributes().getNamedItem("src").getNodeValue());
		}
		
		
		for (String src: srcs) {
			if (src.contains("a.ppy.sh/" + getUser().getID())) {
				return "http:" + src;
			}
		}
		*/
		return "https://a.ppy.sh/" + user.getID();
	}

	public CountryCode getPais() {
		return pais;
	}
}

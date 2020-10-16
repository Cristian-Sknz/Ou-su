package me.skiincraft.discord.ousu.messages;

public class Ranking {
	
	private final String username;
	private final String userid;
	private final String url;
	private final String[] country;

	private final String pp;

	public Ranking(String username, String userid, String url, String[] country, String pp) {
		this.username = username;
		this.userid = userid;
		this.url = url;
		this.country = country;
		this.pp = pp;
	}

	public String getUsername() {
		return username;
	}

	public String getPP() {
		return pp;
	}

	public String getUserid() {
		return userid;
	}

	public String getUrl() {
		return url;
	}

	public String[] getCountry() {
		return country;
	}

}

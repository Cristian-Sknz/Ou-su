package me.skiincraft.discord.ousu.search;

import java.util.Arrays;

public class RankingUsers {

	private String username;
	private String userid;
	private String url;
	private String[] country;

	private String pp;

	public RankingUsers(String username, String userid, String url, String[] country, String pp) {
		super();
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

	@Override
	public String toString() {
		return "RankingUsers [username=" + username + ", userid=" + userid + ", url=" + url + ", country="
				+ Arrays.toString(country) + ", pp=" + pp + "]";
	}

}

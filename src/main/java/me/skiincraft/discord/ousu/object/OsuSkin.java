package me.skiincraft.discord.ousu.object;

import java.util.List;

import me.skiincraft.api.ousu.entity.objects.Gamemode;

public class OsuSkin {

	private final String skinname;
	private final String skinurl;
	private final String skinimage;

	private final String downloadurl;

	private final String creator;
	private final List<Gamemode> gamemodes;
	private final Statistics statistics;

	public OsuSkin(String skinname, String skinurl, String skinimage, String downloadurl, String creator,
			List<Gamemode> gamemodes, Statistics statistics) {
		super();
		this.skinname = skinname;
		this.skinurl = skinurl;
		this.skinimage = skinimage;
		this.downloadurl = downloadurl;
		this.creator = creator;
		this.gamemodes = gamemodes;
		this.statistics = statistics;
	}

	public String getSkinname() {
		return skinname;
	}

	public String getSkinurl() {
		return skinurl;
	}

	public String getSkinimage() {
		return skinimage;
	}

	public String getDownloadurl() {
		return downloadurl;
	}

	public String getCreator() {
		return creator;
	}

	public List<Gamemode> getGamemodes() {
		return gamemodes;
	}

	public Statistics getStatistics() {
		return statistics;
	}

	@Override
	public String toString() {
		return "OsuSkin [skinname=" + skinname + ", skinurl=" + skinurl + ", skinimage=" + skinimage + ", downloadurl="
				+ downloadurl + ", creator=" + creator + ", gamemodes=" + gamemodes + ", statistics=" + statistics
				+ "]";
	}

	public static class Statistics {
		private final String downloads;
		private final String viewes;
		private final String comments;

		public Statistics(String downloads, String viewes, String comments) {
			super();
			this.downloads = downloads;
			this.viewes = viewes;
			this.comments = comments;
		}

		public String getDownloads() {
			return downloads;
		}

		public String getViewes() {
			return viewes;
		}

		public String getComments() {
			return comments;
		}

	}

}

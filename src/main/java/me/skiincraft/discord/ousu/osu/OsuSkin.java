package me.skiincraft.discord.ousu.osu;

import java.util.List;

import me.skiincraft.api.ousu.entity.objects.Gamemode;

public class OsuSkin {

	private final String skinName;
	private final String skinURL;
	private final String skinImage;

	private final String downloadURL;

	private final String creator;
	private final List<Gamemode> gamemodes;
	private final Statistics statistics;

	public OsuSkin(String skinName, String skinURL, String skinImage, String downloadurl, String creator,
				   List<Gamemode> gamemodes, Statistics statistics) {
		super();
		this.skinName = skinName;
		this.skinURL = skinURL;
		this.skinImage = skinImage;
		this.downloadURL = downloadurl;
		this.creator = creator;
		this.gamemodes = gamemodes;
		this.statistics = statistics;
	}

	public String getSkinName() {
		return skinName;
	}

	public String getSkinURL() {
		return skinURL;
	}

	public String getSkinImage() {
		return skinImage;
	}

	public String getDownloadURL() {
		return downloadURL;
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
		return "OsuSkin [skinname=" + skinName + ", skinurl=" + skinURL + ", skinimage=" + skinImage + ", downloadurl="
				+ downloadURL + ", creator=" + creator + ", gamemodes=" + gamemodes + ", statistics=" + statistics
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

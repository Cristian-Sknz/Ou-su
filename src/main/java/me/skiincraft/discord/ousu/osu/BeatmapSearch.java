package me.skiincraft.discord.ousu.osu;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import me.skiincraft.api.ousu.entity.objects.Approval;
import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.api.ousu.entity.objects.Genre;

public class BeatmapSearch {

	private final String title;
	private final String author;
	private final String creator;

	private final long creatorId;
	private final long beatmapId;
	private final long beatmapSetId;

	private final String mapLength;
	private final String[] difficult;
	private final Gamemode[] gamemodes;

	private final Genre genre;
	private final float bpm;
	private final String[] tags;
	private final Approval approvated;

	private final boolean video;

	public BeatmapSearch(String title, String author, long creatorId, String creator, long beatmapSetId, long beatmapId, boolean video,
						 String mapLength, String[] difficult, Gamemode[] gamemodes, Genre genre, float bpm, String[] tags,
						 Approval approvated) {
		this.beatmapId = beatmapId;
		this.beatmapSetId = beatmapSetId;
		this.title = title;
		this.author = author;
		this.creatorId = creatorId;
		this.creator = creator;
		this.video = video;
		this.mapLength = mapLength;
		this.difficult = difficult;
		this.genre = genre;
		this.bpm = bpm;
		this.tags = tags;
		this.approvated = approvated;
		this.gamemodes = gamemodes;
	}
	
	public long getBeatmapSetId() {
		return beatmapSetId;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public String getCreator() {
		return creator;
	}

	public String getMapLength() {
		return mapLength;
	}

	public Genre getGenre() {
		return genre;
	}

	public float getBpm() {
		return bpm;
	}
	
	public long getBeatmapId() {
		return beatmapId;
	}

	public String[] getTags() {
		return tags;
	}

	public Approval getApprovated() {
		return approvated;
	}

	public String[] getDifficult() {
		return difficult;
	}

	public InputStream getBeatmapPreview() throws IOException {
		URLConnection conn = new URL("http://b.ppy.sh/preview/" + beatmapSetId + ".mp3").openConnection();
		return conn.getInputStream();
	}

	public String getBeatmapCoverUrl() {
		return "https://assets.ppy.sh/beatmaps/" + beatmapSetId + "/covers/cover.jpg";
	}

	public String getBeatmapThumbnailUrl() {
		return "https://b.ppy.sh/thumb/" + beatmapSetId + "l.jpg";
	}

	public String getURL() {
		return "https://osu.ppy.sh/beatmapsets/" + beatmapSetId;
	}

	public Gamemode[] getGamemodes() {
		return gamemodes;
	}

	public boolean hasVideo() {
		return video;
	}

}

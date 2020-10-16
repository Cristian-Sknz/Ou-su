package me.skiincraft.discord.ousu.htmlpage;

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
	private final int creatorid;
	private final String creator;
	
	private final long beatmapid;
	private final long beatmapsetid;

	private final String maplength;
	private final String[] difficult;
	private final Gamemode[] gamemodes;

	private final Genre genre;
	private final float bpm;
	private final String[] tags;
	private final Approval approvated;

	private final boolean video;

	public BeatmapSearch(String title, String author, int creatorid, String creator, long beatmapsetid, long beatmapid, boolean video,
			String maplength, String[] difficult, Gamemode[] gamemodes, Genre genre, float bpm, String[] tags,
			Approval approvated) {
		this.beatmapid = beatmapid;
		this.beatmapsetid = beatmapsetid;
		this.title = title;
		this.author = author;
		this.creatorid = creatorid;
		this.creator = creator;
		this.video = video;
		this.maplength = maplength;
		this.difficult = difficult;
		this.genre = genre;
		this.bpm = bpm;
		this.tags = tags;
		this.approvated = approvated;
		this.gamemodes = gamemodes;
	}
	
	public long getBeatmapsetid() {
		return beatmapsetid;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public int getCreatorid() {
		return creatorid;
	}

	public String getCreator() {
		return creator;
	}

	public String getMaplength() {
		return maplength;
	}

	public Genre getGenre() {
		return genre;
	}

	public float getBpm() {
		return bpm;
	}
	
	public long getBeatmapid() {
		return beatmapid;
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
		URLConnection conn = new URL("http://b.ppy.sh/preview/" + beatmapsetid + ".mp3").openConnection();
		return conn.getInputStream();
	}

	public String getBeatmapCoverUrl() {
		return "https://assets.ppy.sh/beatmaps/" + beatmapsetid + "/covers/cover.jpg";
	}

	public String getBeatmapThumbnailUrl() {
		return "https://b.ppy.sh/thumb/" + beatmapsetid + "l.jpg";
	}

	public String getURL() {
		return "https://osu.ppy.sh/beatmapsets/" + beatmapsetid;
	}

	public Gamemode[] getGamemodes() {
		return gamemodes;
	}

	public boolean hasVideo() {
		return video;
	}

}

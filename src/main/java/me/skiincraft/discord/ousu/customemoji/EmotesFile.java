package me.skiincraft.discord.ousu.customemoji;

import java.util.Map;

public class EmotesFile {
	
	private Map<String, Map<String, String>> emotes;

	public Map<String, Map<String, String>> getEmotes() {
		return emotes;
	}

	public void setEmotes(Map<String, Map<String, String>> configuration) {
		this.emotes = configuration;
	}

}

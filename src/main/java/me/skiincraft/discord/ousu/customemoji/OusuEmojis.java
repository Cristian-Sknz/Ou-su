package me.skiincraft.discord.ousu.customemoji;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Emote;

public class OusuEmojis {
	
	public static List<Emote> emotes = new ArrayList<Emote>();
	
	public static List<Emote> getEmotes() {
		return emotes;
	}
	
	public static Emote getEmoteEquals(String nameequals) {
		for (Emote emote : getEmotes()) {
			if (emote.getName().equalsIgnoreCase(nameequals)) {
				return emote;
			}
		}
		return getEmotes().get(0);
	}
	
	public static String getEmoteAsMentionEquals(String nameequals) {
		for (Emote emote : getEmotes()) {
			if (emote.getName().equalsIgnoreCase(nameequals)) {
				return emote.getAsMention();
			}
		}
		return getEmotes().get(0).getAsMention();
	}
	
	public static Emote getEmote(String name) {
		for (Emote emote : getEmotes()) {
			if (emote.getName().toLowerCase().contains(name)) {
				return emote;
			}
		}
		return getEmotes().get(0);
	}

	public static String getEmoteAsMention(String name) {
		for (Emote emote : getEmotes()) {
			if (emote.getName().toLowerCase().contains(name)) {
				return emote.getAsMention();
			}
		}
		return getEmotes().get(0).getAsMention();
	}

}

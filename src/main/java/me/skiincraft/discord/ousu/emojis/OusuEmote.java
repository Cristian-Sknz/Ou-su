package me.skiincraft.discord.ousu.emojis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import me.skiincraft.discord.ousu.OusuBot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.dv8tion.jda.api.entities.Emote;

public class OusuEmote {
	
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
	
	public static void loadEmotes(String filename) {
		try {
			FileInputStream inputStream = new FileInputStream(OusuBot.getMain().getPlugin().getPluginPath() + "/" + filename + ".json");
			Reader reader = new InputStreamReader(inputStream);
			JsonObject ob = new JsonParser().parse(reader).getAsJsonArray().get(0).getAsJsonObject();
			JsonArray array = ob.get("Emotes").getAsJsonArray();
			
			for (JsonElement element : array) {
				JsonObject object = element.getAsJsonObject();
				emotes.add(new EmoteImpl(object));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}

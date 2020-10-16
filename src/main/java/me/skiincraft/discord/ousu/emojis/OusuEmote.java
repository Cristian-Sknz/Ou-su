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
	
	public static final List<Emote> emotes = new ArrayList<>();
	
	public static List<Emote> getEmotes() {
		return emotes;
	}
	
	public static Emote getEmoteEquals(String nameequals) {
		return emotes.stream()
				.filter(emote -> emote.getName().equalsIgnoreCase(nameequals))
				.findFirst().orElse(getEmotes().get(0));
	}
	
	public static String getEmoteAsMentionEquals(String nameequals) {
		return emotes.stream()
				.filter(emote -> emote.getName().equalsIgnoreCase(nameequals))
				.map(Emote::getAsMention)
				.findFirst().orElse(getEmotes().get(0).getAsMention());
	}
	
	public static Emote getEmote(String name) {
		return emotes.stream()
				.filter(emote -> emote.getName().contains(name))
				.findFirst().orElse(getEmotes().get(0));
	}

	public static String getEmoteAsMention(String name) {
		return emotes.stream()
				.filter(emote -> emote.getName().contains(name))
				.map(Emote::getAsMention)
				.findFirst().orElse(getEmotes().get(0).getAsMention());
	}
	
	public static void loadEmotes(String filename) {
		try {
			FileInputStream inputStream = new FileInputStream(OusuBot.getInstance().getPlugin().getPluginPath() + "/" + filename + ".json");
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

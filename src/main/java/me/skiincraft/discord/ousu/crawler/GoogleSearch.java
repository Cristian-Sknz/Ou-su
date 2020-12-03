package me.skiincraft.discord.ousu.crawler;

import java.util.ArrayList;
import java.util.List;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.skiincraft.discord.ousu.exceptions.SearchException;
import me.skiincraft.discord.ousu.object.Search;

public class GoogleSearch {

	private final List<Search> result = new ArrayList<>();

	public GoogleSearch(String beatmapname) {
		String get = "https://www.googleapis.com/customsearch/v1";
		String cx = "005029898272968949824:o3kccdcszcc";
		String token = "AIzaSyC5v7-xpRE7wg6F8wyzXcSvxUoKdtaKDIg";

		HttpRequest bc = HttpRequest.get(get, true, "key", token, "cx", cx, "q", beatmapname, "alt", "json");

		bc.accept("application/json").contentType();
		String body = bc.body();

		JsonObject ob1 = new JsonParser().parse(body).getAsJsonObject();

		if (!ob1.has("items")) {
			throw new SearchException("Não foi encontrado nada na pesquisa. Foi pesquisado '" + beatmapname + "'.");
		}

		JsonArray items = ob1.get("items").getAsJsonArray();
		if (items.size() == 0) {
			throw new SearchException("Não foi encontrado nada na pesquisa. Foi pesquisado '" + beatmapname + "'.");
		}
		
		for (JsonElement ele : items) {
			JsonObject obj = ele.getAsJsonObject();
			if (obj.get("link").getAsString().contains("osu.ppy.sh/beatmapsets/")) {
				result.add(new Search(obj.get("title").getAsString(), obj.get("link").getAsString()));
			}
		}
	}

	public List<Search> getResults() {
		return result;
	}

	public List<Integer> getBeatmapSetIDs() {
		List<Integer> i = new ArrayList<>();
		for (Search s : getResults()) {
			String linkv = s.getLink().replace("https://osu.ppy.sh/beatmapsets/", "")
					.replace("http://osu.ppy.sh/beatmapsets/", "");
			
			if (linkv.contains("/")) {
				int num;
				for (num = 0; num < 100; num++) {
					if (linkv.charAt(num) == '/') {
						break;
					}
				}
				linkv = linkv.substring(0, num);
				s.setLink(linkv);
			}
			try {
				i.add(Integer.valueOf(s.getLink().replaceAll("\\D+", "")));
			} catch (NumberFormatException ignored) {
			}
		}
		return i;
	}

}

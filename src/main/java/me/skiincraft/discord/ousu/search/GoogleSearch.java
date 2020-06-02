package me.skiincraft.discord.ousu.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import me.skiincraft.discord.ousu.exception.SearchNotFoundException;
import me.skiincraft.discord.ousu.utils.Token;

public class GoogleSearch {

	private List<Search> result;
	private static JsonReceiver receiver;

	public GoogleSearch(String beatmapname) {
		String get = "https://www.googleapis.com/customsearch/v1";
		String cx = "005029898272968949824:o3kccdcszcc";
		String token = Token.googletoken;

		String search = beatmapname;
		HttpRequest bc = HttpRequest.get(get, true, "key", token, "cx", cx, "q", search, "alt", "json");

		bc.accept("application/json").contentType();
		String body = bc.body();

		Gson g = new Gson();
		JsonReceiver us = g.fromJson(body, JsonReceiver.class);

		try {
			receiver = us;
			result = new ArrayList<Search>();
			
			if (receiver.getItems() == null) {
				throw new SearchNotFoundException(
						"Não foi encontrado nada na pesquisa. Foi pesquisado '" + beatmapname + "'.");
			}
			List<Map<String, Object>> items = receiver.getItems();

			for (Map<String, Object> it : items) {
				String title = (String) it.get("title");
				String link = (String) it.get("link");

				if (link.contains("osu.ppy.sh/beatmapsets/")) {
					result.add(new Search(title, link));
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	public List<Search> getResults() {
		return result;
	}

	@Deprecated
	public List<Integer> getBeatmapSetID() {
		List<Integer> i = new ArrayList<Integer>();
		for (Search s : getResults()) {
			i.add(Integer.valueOf(s.getLink().replaceAll("\\D+", "")));
		}
		return i;
	}

	public List<Integer> getBeatmapSetIDs() {
		List<Integer> i = new ArrayList<Integer>();
		for (Search s : getResults()) {
			String linkv = s.getLink().replace("https://osu.ppy.sh/beatmapsets/", "")
					.replace("http://osu.ppy.sh/beatmapsets/", "");
			
			if (linkv.contains("/")) {
				int num = 0;
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
			} catch (NumberFormatException e) {
				continue;
			}
		}
		return i;
	}

}

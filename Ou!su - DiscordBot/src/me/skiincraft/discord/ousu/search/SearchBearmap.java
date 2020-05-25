package me.skiincraft.discord.ousu.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import me.skiincraft.discord.ousu.exception.SearchNotFoundException;
import me.skiincraft.discord.ousu.utils.Token;

public class SearchBearmap {

	private List<Search> result;
	private static JsonReceiver receiver;

	public SearchBearmap(String beatmapname) {
		// Utilizei a API do google para pesquisar
		String get = "https://www.googleapis.com/customsearch/v1";
		String token = Token.googletoken;// Precisa do Token API do google
		String cx = "005029898272968949824:o3kccdcszcc";

		String search = beatmapname;
		HttpRequest bc = HttpRequest.get(get, true, "key", token, "cx", cx, "q", search, "alt", "json");
		// Fiz um request

		bc.accept("application/json").contentType();

		String body = bc.body();// Resultado do request

		Gson g = new Gson();
		JsonReceiver us = g.fromJson(body, JsonReceiver.class);
		// Extraio o que eu recebi em um construtor

		try {
			receiver = us;
			result = new ArrayList<Search>();
			// Caso não tenha achado nada, dispara uma exceção.
			if (receiver.getItems() == null) {
				throw new SearchNotFoundException(
						"Não foi encontrado nada na pesquisa. Foi pesquisado '" + beatmapname + "'.");
			}
			List<Map<String, Object>> items = receiver.getItems();

			for (Map<String, Object> it : items) {
				String title = (String) it.get("title");
				String link = (String) it.get("link");

				// Aqui ele filtra somente os beatmaps do site do osu.
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

	public List<Integer> getBeatmapSetIDs() {
		List<Integer> i = new ArrayList<Integer>();
		// Aqui ele transforma o url em numeros (pegando somente o beatmap ID)
		// Esse metodo pode falhar em pegar o id correto, foi feito outro abaixo.
		for (Search s : getResults()) {
			i.add(Integer.valueOf(s.getLink().replaceAll("\\D+", "")));
		}
		return i;
	}

	public List<Integer> getBeatmapSetIDs2() {
		// Aqui ele transforma o url em numeros (pegando somente o beatmap ID)
		// é uma versão melhor da primeira opção.
		List<Integer> i = new ArrayList<Integer>();
		for (Search s : getResults()) {
			// Tiro o url inicial
			String linkv = s.getLink().replace("https://osu.ppy.sh/beatmapsets/", "")
					.replace("http://osu.ppy.sh/beatmapsets/", "");

			// Se tiver um '/' ele vai filtrar até achar o primeiro
			if (linkv.contains("/")) {
				int num = 0;
				for (num = 0; num < 100; num++) {

					if (linkv.charAt(num) == '/') {
						break;
					}
					// Quando ele achar, vai pegar o id corretamente
					// ex: '3182313/discussão...' para '3182313'
				}
				linkv = linkv.substring(0, num);
				s.setLink(linkv);
			}

			// Aqui como garantia coloquei para substituir as letras por numeros.
			try {
				i.add(Integer.valueOf(s.getLink().replaceAll("\\D+", "")));
			} catch (NumberFormatException e) {
				continue;
			}
		}
		return i;
	}

}

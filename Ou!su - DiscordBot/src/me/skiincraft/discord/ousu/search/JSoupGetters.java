package me.skiincraft.discord.ousu.search;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import me.skiincraft.api.ousu.modifiers.Approvated;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.api.ousu.modifiers.Genre;
import me.skiincraft.discord.ousu.osuskins.OsuSkin;
import me.skiincraft.discord.ousu.osuskins.OsuSkin.Statistics;

public class JSoupGetters {

	public static String[] splitText(String s, int i) {
		String[] split1 = s.split("#");
		String[] split2 = split1[i].split(" ");

		return split2;
	}

	public static String encodeValue(String value) {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.toString()).replace("+", "%20");
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getCause());
		}
	}

	public static List<RankingUsers> inputtypes(String user) throws IOException {
		String html = "https://old.ppy.sh/u/" + user;

		Document doc = Jsoup.connect(html).get();
		List<RankingUsers> ranking = new ArrayList<RankingUsers>();
		Element ele = doc.getElementsByClass("playstyle-container").get(0);
		Map<String, Boolean> elementos = new HashMap<>();
		elementos.put("Mouse", ele.toString().contains("mouse using"));
		elementos.put("Teclado", ele.toString().contains("keyboard using"));
		elementos.put("Tablet", ele.toString().contains("tablet using"));
		elementos.put("Touch", ele.toString().contains("touch using"));

		System.out.println("Mouse: " + elementos.get("Mouse"));
		System.out.println("Teclado: " + elementos.get("Teclado"));
		System.out.println("Tablet: " + elementos.get("Tablet"));
		System.out.println("Touch: " + elementos.get("Touch"));

		System.out.println(doc.getElementsByClass("prof-beatmap"));

		return ranking;
	}

	public static BeatmapSearch beatmapinfos(int beatmapsetid) throws IOException {
		String html = "https://old.ppy.sh/s/" + beatmapsetid;

		Document doc = Jsoup.connect(html).get();

		boolean video = doc.getElementsByClass("beatmap_download_link").size() == 2;

		Element tablist = doc.getElementById("tablist");
		Elements tab;
		try {
			tab = tablist.select("ul > li > a");
		} catch (NullPointerException e) {
			return null;
		}

		List<Gamemode> gamemode = new ArrayList<Gamemode>();
		for (Element beatmaptab : tab) {
			Elements active = beatmaptab.getElementsByClass("beatmaptab active");
			if (!(active.size() == 0)) {
				if (active.get(0).attr("href").contains("m=0")) {
					if (!gamemode.contains(Gamemode.Standard)) {
						gamemode.add(Gamemode.Standard);
					}
				}
				if (active.get(0).attr("href").contains("m=1")) {
					if (!gamemode.contains(Gamemode.Taiko)) {
						gamemode.add(Gamemode.Taiko);
					}
				}
				if (active.get(0).attr("href").contains("m=2")) {
					if (!gamemode.contains(Gamemode.Catch)) {
						gamemode.add(Gamemode.Catch);
					}
				}
				if (active.get(0).attr("href").contains("m=3")) {
					if (!gamemode.contains(Gamemode.Mania)) {
						gamemode.add(Gamemode.Mania);
					}
				}
			}

			if (beatmaptab.getElementsByClass("beatmapTab ").size() == 0) {
				break;
			}

			for (Element b : beatmaptab.getElementsByClass("beatmapTab ")) {
				if (b.attr("href").contains("m=0")) {
					if (!gamemode.contains(Gamemode.Standard)) {
						gamemode.add(Gamemode.Standard);
					}
				}
				if (b.attr("href").contains("m=1")) {
					if (!gamemode.contains(Gamemode.Taiko)) {
						gamemode.add(Gamemode.Taiko);
					}
				}
				if (b.attr("href").contains("m=2")) {
					if (!gamemode.contains(Gamemode.Catch)) {
						gamemode.add(Gamemode.Catch);
					}
				}
				if (b.attr("href").contains("m=3")) {
					if (!gamemode.contains(Gamemode.Mania)) {
						gamemode.add(Gamemode.Mania);
					}
				}
				if (gamemode.size() == 4) {
					break;
				}
			}
		}
		Gamemode[] gamemodes = new Gamemode[gamemode.size()];
		gamemode.toArray(gamemodes);

		Elements sl = tablist.select("ul > li > a > span");

		List<String> diff = new ArrayList<String>();

		for (Element dif : sl) {
			diff.add(dif.text());
		}

		String[] difficult = new String[diff.size()];
		diff.toArray(difficult);

		// songinfo
		Element songinfo = doc.getElementById("songinfo");
		Elements info = songinfo.select("tbody > tr");

		Elements tb1 = info.get(1).select("td"); // Tabela 1
		String title = tb1.get(1).text(); // title

		Elements tb0 = info.get(0).select("td"); // Tabela 0
		String artist = tb0.get(1).text(); // Artist;

		Elements tb2 = info.get(2).select("td"); // Tabela 2

		String creator = tb2.get(1).text(); // CreatorName
		int creatorid = Integer.valueOf(tb2.get(1).select("a").attr("href").replace("/u/", "")); // CreatorID
		String maplenth = tb2.get(5).text().split(" ")[0]; // Lenth

		Elements tb3 = info.get(3).select("td"); // Tabela 3
		Genre[] gr = Genre.values();

		Genre genre = Genre.UNSPECIFIED;
		for (Genre g : gr) {
			if (g.name().equalsIgnoreCase(tb3.get(3).select("a").get(0).text())) {
				genre = g;
			}
		}

		float bpm = Float.valueOf(tb3.get(5).text());

		Elements tb4 = info.get(4).select("td"); // Tabela 4
		String[] tags = tb4.get(1).text().split(" ");

		Elements tb5 = info.get(6).select("td"); // Tabela 6
		Approvated[] apv = Approvated.values();
		Approvated approvated = Approvated.UNSPECIFIED;

		for (Approvated ap : apv) {
			if (ap.name().equalsIgnoreCase(tb5.get(0).text().split(":")[1].replace(" ", ""))) {
				approvated = ap;
			}
		}

		BeatmapSearch s = new BeatmapSearch(title, artist, creatorid, creator, beatmapsetid, video, maplenth, difficult,
				gamemodes, genre, bpm, tags, approvated);

		return s;
	}

	public static List<OsuSkin> pageskins() throws IOException {
		String html = "https://osuskins.net/";

		Document doc = Jsoup.connect(html).get();
		List<OsuSkin> skinslist = new ArrayList<OsuSkin>();
		Elements ele = doc.getElementsByClass("skins");

		Elements skins = ele.get(0).getElementsByClass("skin-container");
		int i = 0;
		for (Element skin : skins) {
			if (i == 10) {
				break;
			}
			String skinname = skin.select("h2").get(0).text();
			String skinlink = "https://osuskins.net" + skin.select("a").attr("href");
			String skinimage = skinlink.replace("/skin/", "/screenshots/") + ".jpg";
			String download = skinlink.replace("/skin/", "/download/");
			String downloads = skin.select("li").get(0).text();
			String views = skin.select("li").get(1).text();
			String comments = "0";
			if (skin.select("li").size() == 3) {
				comments = skin.select("li").get(2).text();
			}

			Document doc2 = Jsoup.connect(skinlink).get();
			String creator = doc2.getElementsByClass("skin-page-detail").get(2).text();
			if (creator.contains("Players")) {
				creator = "idk";
			} else {
				creator = creator.replace("Creators ", "");
			}
			List<Gamemode> gamemodes = new ArrayList<Gamemode>();
			String[] m = doc2.getElementsByClass("skin-page-detail").get(1).text().replace("Modes ", "").split(" ");

			for (String modes : m) {
				gamemodes.add(Gamemode.valueOf(modes));
			}

			OsuSkin skincomplete = new OsuSkin(skinname, skinlink, skinimage, download, creator, gamemodes,
					new Statistics(downloads, views, comments));

			skinslist.add(skincomplete);
			i++;
		}
		return skinslist;
	}

}

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

import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.discord.ousu.osuskins.OsuSkin;
import me.skiincraft.discord.ousu.osuskins.OsuSkin.Statistics;

public class OsuSearchGetter {

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

	public static List<OsuSkin> pageskins() throws IOException {
		String html = "https://osuskins.net/";

		Document doc = Jsoup.connect(html).get();
		List<OsuSkin> skinslist = new ArrayList<OsuSkin>();
		Elements ele = doc.getElementsByClass("skins");

		Elements skins = ele.get(0).getElementsByClass("skin-container");
		int i = 0;
		for (Element skin : skins) {
			if (i == 10)break;
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
			String[] m = doc2.getElementsByClass("skin-page-detail")
					.get(1)
					.text()
					.replace("Modes ", "")
					.split(" ");
			
			for (String modes : m) {
				gamemodes.add(Gamemode.valueOf(modes));
			}
			
			OsuSkin skincomplete = new OsuSkin(skinname, skinlink, skinimage, download,
					creator, gamemodes, new Statistics(downloads, views, comments));
			
			skinslist.add(skincomplete);
			i++;
		}
		
		return skinslist;
	}

}

package me.skiincraft.discord.ousu.htmlpage;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import me.skiincraft.discord.ousu.object.Gender;
import me.skiincraft.discord.ousu.object.Personagem;

public class CharacterGetter {
	
	public static List<Personagem> searchCharacter(String character) {
		List<Personagem> characters = new ArrayList<>();
		try {
			Document document = Jsoup.connect("https://www.anime-planet.com/characters/all?name=" + URLEncoder.encode(character, StandardCharsets.UTF_8.name()) + "&sort=likes&order=desc").get();
			boolean isProfile = !document.location().contains("/all");
			if (isProfile) {
				Element img = document.getElementsByClass("pure-g entrySynopsis").get(0).getElementsByTag("img").get(0);
				String name = upperFirst(document.selectFirst("#siteContainer > h1").text());
				String link = document.location();
				String image = "https://www.anime-planet.com" + img.attr("src");
				String gender = document.selectFirst("#siteContainer > section.pure-g.entryBar > div:nth-child(1)")
						.text()
						.replace("Gender: ", "")
						.toUpperCase();
				
				String haircolor = document.selectFirst("#siteContainer > section.pure-g.entryBar > div:nth-child(2)")
						.text()
						.replace("Hair Color: ", "");
				
				
				characters.add(new Personagem(name, Gender.valueOf(gender), link, haircolor, image));
				
				return characters;
			}
			boolean notfound = document.getElementsByClass("pure-table stickyHeader striped").isEmpty();
			if (notfound) {
				return characters;
			}
			Element classe = document.getElementsByClass("pure-table stickyHeader striped").get(0);
			for (Element ele : classe.getElementsByTag("tr")) {
				Element avatar = ele.getElementsByClass("tableAvatar").get(0).selectFirst("img");
				Element info = ele.getElementsByClass("tableCharInfo").get(0);
				Element a = ele.getElementsByClass("tableCharInfo").get(0).getElementsByTag("a").get(0);
				
				Elements tags = info.getElementsByClass("tags").select("li");
	
				String image = "https://www.anime-planet.com" + avatar.attr("src");
				String link = "https://www.anime-planet.com" + info.attr("href");
				String name = upperFirst(a.text());
				Gender gender = Gender.UNKNOWN;
				String haircolor = ""; 
				if (tags.size() != 0) {
					gender = Gender.getGender(tags.get(0).text());
					haircolor = (gender == Gender.UNKNOWN) ? tags.get(0).text() : tags.get(1).text();
				}
				characters.add(new Personagem(name, gender,link, haircolor, image));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return characters;
	}
	
	public static Personagem refreshPersonagem(String url) {
		try {
			Document document = Jsoup.connect(url).get();
			Element img = document.getElementsByClass("pure-g entrySynopsis").get(0).getElementsByTag("img").get(0);
			String name = upperFirst(document.selectFirst("#siteContainer > h1").text());
			String link = document.location();
			String image = "https://www.anime-planet.com" + img.attr("src");
			String gender = document.selectFirst("#siteContainer > section.pure-g.entryBar > div:nth-child(1)").text()
					.replace("Gender: ", "").toUpperCase();

			String haircolor = document.selectFirst("#siteContainer > section.pure-g.entryBar > div:nth-child(2)")
					.text().replace("Hair Color: ", "").toUpperCase();

			return new Personagem(name, Gender.getGender(gender), link, haircolor, image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String upperFirst(String string) {
		String[] splited = string.split(" ");
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < splited.length; i++) {
			builder.append(String.valueOf(splited[i].charAt(0)).toUpperCase());
			builder.append(splited[i].toLowerCase().substring(1));
			if (i != splited.length - 1) {
				builder.append(" ");
			}
		}
		return builder.toString();
	}
}


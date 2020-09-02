package me.skiincraft.discord.ousu.htmlpage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.messages.Ranking;

public class HtmlRanking {
	
	public static List<Ranking> get(String country) throws IOException{
		String s = (country == null) ? "" : "country=" + country;
		String html = "https://osu.ppy.sh/rankings/osu/performance?" + s;
		Document doc = Jsoup.connect(html).get();
		List<Ranking> ranking = new ArrayList<Ranking>();
		Elements eles = doc.select("tbody > tr > td");

		int i = 1;

		for (Element e1 : eles) {
			Elements s1 = e1.getElementsByClass("ranking-page-table__user-link");
			for (Element e2 : s1) {
				Elements s2 = e2.getElementsByClass("flag-country");
				Elements s3 = e2.getElementsByClass("ranking-page-table__user-link-text js-usercard");

				String playername = s3.get(0).html();
				String userid = s3.get(0).attr("data-user-id");
				String url = s3.get(0).attr("href");

				for (Element e3 : s2) {

					String countryname = e3.attr("title");
					String countrycode = e3.attr("style").split("'")[1]
							.replace("/images/flags/", "")
							.replace(".png","");

					Ranking rank = new Ranking(playername, userid, url, new String[] { countryname, countrycode },
							splitText(eles.text(), i)[4 + StringUtils.quantityLetters(" ", playername)]);

					ranking.add(rank);
					i++;
				}
			}
		}
		return ranking;
	}
	
	private static String[] splitText(String s, int i) {
		String[] split1 = s.split("#");
		String[] split2 = split1[i].split(" ");

		return split2;
	}

}

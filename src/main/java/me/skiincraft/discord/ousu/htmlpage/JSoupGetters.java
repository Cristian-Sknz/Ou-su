package me.skiincraft.discord.ousu.htmlpage;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ocpsoft.prettytime.PrettyTime;

import me.skiincraft.api.ousu.entity.objects.Approval;
import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.api.ousu.entity.objects.Genre;
import me.skiincraft.api.ousu.entity.user.User;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.ousu.object.OsuSkin;
import me.skiincraft.discord.ousu.object.OsuSkin.Statistics;
import me.skiincraft.discord.ousu.osu.UserStatistics;


public class JSoupGetters {

	public static String[] splitText(String s, int i) {
		String[] split1 = s.split("#");
		return split1[i].split(" ");
	}

	public enum InputTypes {
		Mouse, Keyboard, Table, Touchpad
	}
	
	public static UserStatistics inputType(User user, LanguageManager lang) throws IOException {
		String html = "https://old.ppy.sh/u/" + user.getUserId();

		Document doc = Jsoup.connect(html).get();
		Element ele = doc.getElementsByClass("playstyle-container").get(0);
		Element ele2 = doc.getElementsByClass("profile-details").get(0);
		
		String firstlogin = ele2.getElementsByClass("timeago").get(0).text().replace(" UTC", "");
		String lastActive = ele2.getElementsByClass("timeago").get(1).text().replace(" UTC", "");
		
		SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			PrettyTime pretty = new PrettyTime(lang.getLanguage().getLocale());
			firstlogin = pretty.format(simpledate.parse(firstlogin));
			lastActive = pretty.format(simpledate.parse(lastActive));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//OsuUserDB sql = new OsuUserDB(user);
		String lastpp = "";
		String[] lpp = "232&232".split("&");//sql.get("lastpp").split("&");
		int valor1 = Integer.parseInt(lpp[0]);
		int valor2 = Integer.parseInt(lpp[1]);
		
		if (valor2 != user.getPP()) {
			//sql.set("lastpp", valor2 + "&" + ((int)user.getPP()));
			
			lastpp += user.getPP()+ " (+" + Integer.toString(valor2-(int)user.getPP()).replace("-", "") + ")";
		} else {
			lastpp = (Integer.parseInt(lpp[0]) == Integer.parseInt(lpp[1])) ? lpp[0]: valor2+ " (+" + Integer.toString(valor1-valor2).replace("-", "") + ")";
		}
		
		String lastscore = "";
		String[] lscore = "3132&3232".split("&");//sql.get("lastscore").split("&");
		long val1 = Long.parseLong(lscore[0]);
		long val2 = Long.parseLong(lscore[1]);
		
		if (val2 != user.getTotalScore()) {
			//sql.set("lastscore", val1 + "&" + user.getTotalScore());
			
			lastscore += user.getTotalScore()+ " (+" + Long.toString((val2-user.getTotalScore())).replace("-", "") + ")";
		} else {
			lastscore = (val1 == val2) ? lscore[0]: val2+ " (+" + Long.toString((val1-val2)).replace("-", "") + ")";
		}
		
		Map<InputTypes, Boolean> input = new HashMap<>();
		
		input.put(InputTypes.Mouse, ele.toString().contains("mouse using"));
		input.put(InputTypes.Keyboard, ele.toString().contains("keyboard using"));
		input.put(InputTypes.Table, ele.toString().contains("tablet using"));
		input.put(InputTypes.Touchpad, ele.toString().contains("touch using"));
		
		return new UserStatistics(firstlogin, lastActive, input, lastpp, lastscore, user);
	}
	
	public static BeatmapSearch beatmapInfoById(long beatmapid) throws IOException {
		Document doc = Jsoup.connect("https://old.ppy.sh/b/" + beatmapid).get();
		Elements e = doc.select("#songinfo > tbody > tr > td");
		Element difficult = doc.select("#tablist > ul> li > a").stream()
				.filter(el -> el.className().equalsIgnoreCase("beatmapTab active"))
				.findFirst().orElse(null);
		
		return new BeatmapSearch(e.get(7).text(),
				e.get(1).text(),
				Integer.parseInt(e.get(13).selectFirst("a").attr("href").replaceAll("\\D+", "")), e.get(13).text(),
				Long.parseLong(doc.selectFirst("body > div.bodytopbg > div > div.mainbody2 > div.content > div.content-with-bg > div.paddingboth > div > img").attr("src").replaceAll("\\D+", "")), beatmapid,
				false, e.get(17).text().split(" ")[0], new String[] { difficult.text() },
				getPageGamemodes(new Elements(difficult)), getGenre(e.get(21).text().split(" ")[0].toUpperCase()),
				Float.parseFloat(e.get(23).text()), e.get(25).text().split(" "), null);
	}
	
	private static Genre getGenre(String genrestring) {
		return Arrays.stream(Genre.values()).filter(g -> g.name().equalsIgnoreCase(genrestring)).findAny().orElse(Genre.ANY);
	}

	public static BeatmapSearch beatmapInfo(long beatmapsetid) throws IOException {
		String html = "https://old.ppy.sh/s/" + beatmapsetid;
		Document doc = Jsoup.connect(html).get();

		boolean video = doc.getElementsByClass("beatmap_download_link").size() == 2;

		Element tablist = doc.getElementById("tablist");
		Elements tab = null;

		try {
			tab = tablist.select("ul > li > a");
		} catch (NullPointerException e) {
			return null;
		}
		

		Gamemode[] gamemodes = getPageGamemodes(tab);
		List<String> diff = new ArrayList<>();
		Elements sl = tablist.select("ul > li > a > span");
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
		int creatorid = Integer.parseInt(tb2.get(1).select("a").attr("href").replace("/u/", "")); // CreatorID
		String maplenth = tb2.get(5).text().split(" ")[0]; // Lenth

		Elements tb3 = info.get(3).select("td"); // Tabela 3
		Genre[] gr = Genre.values();

		Genre genre = Genre.UNSPECIFIED;
		for (Genre g : gr) {
			if (g.name().equalsIgnoreCase(tb3.get(3).select("a").get(0).text())) {
				genre = g;
			}
		}

		float bpm = Float.parseFloat(tb3.get(5).text());

		Elements tb4 = info.get(4).select("td"); // Tabela 4
		String[] tags = tb4.get(1).text().split(" ");

		Elements tb5 = info.get(6).select("td"); // Tabela 6
		Approval[] apv = Approval.values();
		Approval approvated = Approval.UNSPECIFIED;

		for (Approval ap : apv) {
			if (ap.name().equalsIgnoreCase(tb5.get(0).text().split(":")[1].replace(" ", ""))) {
				approvated = ap;
			}
		}

		return new BeatmapSearch(title, artist, creatorid, creator, beatmapsetid, 0, video, maplenth, difficult,
				gamemodes, genre, bpm, tags, approvated);
	}
	
	private static Gamemode[] getPageGamemodes(Elements tab) {
		List<Gamemode> gamemode = new ArrayList<>();
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
		return gamemodes;
	}

	public static List<OsuSkin> pageskins() throws IOException {
		String html = "https://osuskins.net/";

		Document doc = Jsoup.connect(html).get();
		List<OsuSkin> skinslist = new ArrayList<>();
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
			List<Gamemode> gamemodes = new ArrayList<>();
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

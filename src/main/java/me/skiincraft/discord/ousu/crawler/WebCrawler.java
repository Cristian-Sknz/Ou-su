package me.skiincraft.discord.ousu.crawler;

import me.skiincraft.api.ousu.entity.objects.Approval;
import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.api.ousu.entity.objects.Genre;
import me.skiincraft.discord.core.configuration.Language;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.messages.Ranking;
import me.skiincraft.discord.ousu.osu.OsuSkin;
import me.skiincraft.discord.ousu.osu.BeatmapSearch;
import me.skiincraft.discord.ousu.osu.UserStatistics;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ocpsoft.prettytime.PrettyTime;

import javax.annotation.Nullable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class WebCrawler {

    private static final String OLD_WEBSITE = "https://old.ppy.sh";
    private static final String OSU_SKINS = "https://osuskins.net";
    private static final String NEW_WEBSITE = "https://osu.ppy.sh";

    public static List<Ranking> getRanking(String country) throws IOException {
        Document doc = Jsoup.connect(NEW_WEBSITE + "/rankings/osu/performance?" + ((country == null) ? "" : "country=" + country)).get();
        Elements eles = doc.select("tbody > tr > td");
        AtomicInteger integer = new AtomicInteger(1);

        List<Ranking> ranking = new ArrayList<>();
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
                            .replace(".png", "");

                    Ranking rank = new Ranking(playername, userid, url, new String[]{countryname, countrycode},
                            splitText(eles.text(), integer.getAndIncrement())[4 + StringUtils.quantityLetters(" ", playername)]);
                    ranking.add(rank);
                }
            }
        }
        return ranking;
    }
        private static String[] splitText(String s, int i) {
            String[] split1 = s.split("#");
            return split1[i].split(" ");
        }

    public static List<OsuSkin> getHomePageSkins() throws IOException {
        Document doc = Jsoup.connect(OSU_SKINS).get();
        List<OsuSkin> skinslist = new ArrayList<>();
        Elements skins = doc.getElementsByClass("skins").get(0).getElementsByClass("skin-container");

        return skins.stream().limit(10).map(skin -> {
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

            Document doc2;
            try {
                doc2 = Jsoup.connect(skinlink).get();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            String creator = doc2.getElementsByClass("skin-page-detail").get(2).text();
            creator = (creator.contains("Players")) ? "?" : creator.replace("Creators ", "");

            List<Gamemode> gamemodes = new ArrayList<>();
            String[] m = doc2.getElementsByClass("skin-page-detail").get(1).text().replace("Modes ", "").split(" ");
            Arrays.stream(m).filter(modes -> Gamemode.getGamemode(modes) != null);

            return new OsuSkin(skinname, skinlink, skinimage, download, creator, gamemodes,
                    new OsuSkin.Statistics(downloads, views, comments));
        }).collect(Collectors.toList());
    }

    public static BeatmapSearch getBeatmapInfo(long beatmapId) {
        try {
            Document doc = Jsoup.connect("https://old.ppy.sh" + "/b/" + beatmapId).get();
            Elements e = doc.select("#songinfo > tbody > tr > td");
            Element difficult = doc.select("#tablist > ul> li > a").stream()
                    .filter(el -> el.className().equalsIgnoreCase("beatmapTab active"))
                    .findFirst().orElse(null);

            long beatmapsetId = Long.parseLong(e.select("a").stream().filter(a -> a.attr("href").toLowerCase().contains("beatmapsets"))
                    .map(a -> a.attr("href"))
                    .findFirst().orElse("0").replaceAll("\\D+", ""));

            return new BeatmapSearch(e.get(7).text(),
                    e.get(1).text(),
                    Integer.parseInt(e.get(13).selectFirst("a").attr("href").replaceAll("\\D+", "")), e.get(13).text(),
                    beatmapsetId, beatmapId,
                    false, e.get(17).text().split(" ")[0], new String[]{Objects.requireNonNull(difficult, "difficult line is null").text()},
                    getPageGamemodes(new Elements(difficult)), getGenre(e.get(21).text().split(" ")[0].toUpperCase()),
                    Float.parseFloat(e.get(23).text()), e.get(25).text().split(" "), null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BeatmapSearch getBeatmapInfoBySetID(long beatmapSetId) throws IOException {
        Document doc = Jsoup.connect(OLD_WEBSITE + "/s/" + beatmapSetId).get();
        boolean video = doc.getElementsByClass("beatmap_download_link").size() == 2;
        Element tablist = doc.getElementById("tablist");
        Elements tab;
        try {
            tab = tablist.select("ul > li > a");
        } catch (NullPointerException e) {
            return null;
        }

        Gamemode[] gamemodes = getPageGamemodes(tab);
        Elements sl = tablist.select("ul > li > a > span");
        String[] difficult = sl.stream().map(Element::text).toArray(String[]::new);

        // songinfo
        Elements songinfo = doc.getElementById("songinfo").select("tbody > tr");

        Elements table0 = songinfo.get(0).select("td"); // Tabela 0
        Elements table1 = songinfo.get(1).select("td"); // Tabela 1
        Elements table2 = songinfo.get(2).select("td"); // Tabela 2
        Elements table3 = songinfo.get(3).select("td"); // Tabela 3
        Elements table4 = songinfo.get(4).select("td"); // Tabela 4
        Elements table5 = songinfo.get(6).select("td"); // Tabela 6

        String title = table1.get(1).text(); // title
        String artist = table0.get(1).text(); // Artist;
        String creator = table2.get(1).text(); // CreatorName
        String[] tags = table4.get(1).text().split(" ");
        float bpm = Float.parseFloat(table3.get(5).text());
        long creatorid = Long.parseLong(table2.get(1).select("a").attr("href").replace("/u/", ""));
        String maplenght = table2.get(5).text().split(" ")[0];

        Genre genre = Arrays.stream(Genre.values())
                .filter(g -> g.name().equalsIgnoreCase(table3.get(3).select("a").get(0).text()))
                .findFirst().orElse(Genre.UNSPECIFIED);

        Approval approval = Arrays.stream(Approval.values())
                .filter(app -> app.name().equalsIgnoreCase(table5.get(0).text().split(":")[1].replace(" ", "")))
                .findFirst().orElse(Approval.UNSPECIFIED);

        return new BeatmapSearch(title, artist, creatorid, creator, beatmapSetId, 0, video, maplenght, difficult,
                gamemodes, genre, bpm, tags, approval);
    }


    private static Gamemode[] getPageGamemodes(Elements tab) {
        List<Gamemode> gamemode = new ArrayList<>();
        tab.forEach(element -> {
            Elements active = element.getElementsByClass("beatmaptab active");
            if (!active.isEmpty()) {
                gamemode.addAll(Arrays.stream(Gamemode.values())
                        .filter(gm -> !gamemode.contains(gm))
                        .filter(gm -> active.get(0).attr("href").contains("m=" + gm.getId())).collect(Collectors.toList()));
            }
            Elements beatmapTab = element.getElementsByClass("beatmapTab ");
            beatmapTab.forEach(b -> gamemode.addAll(Arrays.stream(Gamemode.values())
                    .filter(gm -> !gamemode.contains(gm))
                    .filter(gm -> b.attr("href").contains("m=" + gm.getId())).collect(Collectors.toList())));
        });

        return gamemode.toArray(new Gamemode[0]);
    }

    private static Genre getGenre(String genreString) {
        return Arrays.stream(Genre.values()).filter(g -> g.name().equalsIgnoreCase(genreString)).findAny().orElse(Genre.ANY);
    }


    public static UserStatistics getOtherStatistics(@Nullable Document document, @Nullable Language language, long userId){
        try {
            Document doc = (document == null) ? Jsoup.connect(OLD_WEBSITE + "/u/" + userId).get() : document;
            Element profile_details = doc.getElementsByClass("profile-details").get(0);

            Elements timeago = profile_details.getElementsByClass("timeago");

            String firstlogin = timeago.get(0).text().replace(" UTC", "");
            String online = (timeago.size() == 1) ? "?" : timeago.get(1).text().replace(" UTC", "");

            SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            PrettyTime pretty = new PrettyTime((language != null) ? language.getLocale() : new Locale("en", "US"));

            firstlogin = pretty.format(simpledate.parse(firstlogin));
            online = (online.equals("?")) ? "?" : pretty.format(simpledate.parse(online));

            return new UserStatistics(firstlogin, online, getInputType(doc, userId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<InputType> getInputType(@Nullable Document userPage, long userId){
        try {
            Document doc = (userPage == null) ? Jsoup.connect(OLD_WEBSITE + "/u/" + userId).get() : userPage;
            Element ele = doc.getElementsByClass("playstyle-container").get(0);

            return Arrays.stream(InputType.values())
                    .filter(c -> ele.toString().toLowerCase().contains(c.name().toLowerCase() + " using"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

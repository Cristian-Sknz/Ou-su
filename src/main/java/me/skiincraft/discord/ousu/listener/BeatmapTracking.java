package me.skiincraft.discord.ousu.listener;

import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.crawler.WebCrawler;
import me.skiincraft.discord.ousu.embed.SearchEmbed;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import me.skiincraft.discord.ousu.osu.BeatmapSearch;
import me.skiincraft.discord.ousu.utils.ImageUtils;
import me.skiincraft.discord.ousu.utils.OusuUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BeatmapTracking extends ListenerAdapter {

    public String substringBeatmap(String message){
        if (message.contains("/beatmapsets/")) {
            return message.substring(message.indexOf("beatmapsets/"));
        }
        return message.substring(message.indexOf("beatmaps/"));
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        if (!event.getChannel().canTalk()){
            return;
        }
        if (message.getAuthor().isBot()) {
            return;
        }
        if (message.isWebhookMessage()) {
            return;
        }

        String messageraw = message.getContentRaw().split(" ")[0];
        if (messageraw.contains("osu.ppy.sh/beatmapsets/") || messageraw.contains("https://osu.ppy.sh/beatmaps/")) {
            String geturl = substringBeatmap(messageraw);
            geturl = geturl.substring(geturl.indexOf("/")+1);
            String[] split = geturl.split("/");

            boolean isBeatmapset = messageraw.toLowerCase().contains("beatmapsets");
            if (split.length >= 3){
                if (OusuUtils.isNumeric(split[2])){
                    return;
                }
            }

            if (split.length >= 2) {
                try {
                    BeatmapSearch search = WebCrawler.getBeatmapInfoBySetID(Long.parseLong(split[0].replaceAll("\\D+", "")));
                    if (search == null){
                        search = WebCrawler.getBeatmapInfo(Long.parseLong(split[1].replaceAll("\\D+", "")));
                        if (search == null) {
                            return;
                        }
                    }
                    EmbedBuilder embed = SearchEmbed.searchEmbed(search, new LanguageManager(event.getGuild()));
                    event.getChannel().sendMessage(embed.build()).queue();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            try {
                if (isBeatmapset) {
                    BeatmapSearch search = WebCrawler.getBeatmapInfoBySetID(Long.parseLong(split[0].replaceAll("\\D+", "")));
                    if (search == null) {
                        return;
                    }
                    EmbedBuilder embed = SearchEmbed.searchEmbed(search, new LanguageManager(event.getGuild()));
                    event.getChannel().sendMessage(embed.build()).queue();

                } else {
                    BeatmapSearch search = WebCrawler.getBeatmapInfo(Long.parseLong(split[0].replaceAll("\\D+", "")));
                    if (search == null) {
                        return;
                    }
                    EmbedBuilder embed = SearchEmbed.searchEmbed(search, new LanguageManager(event.getGuild()));
                    event.getChannel().sendMessage(embed.build()).queue();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (messageraw.contains("osuskins.net/skin/")){
            int letters = StringUtils.quantityLetters("/", messageraw.substring(messageraw.indexOf("skin/")));
            if (letters > 2) return;

            String code = messageraw.substring(messageraw.indexOf("skin/")).split("/")[1];
            new Thread(() -> {
                try {
                    Document document = Jsoup.connect("https://osuskins.net/skin/" + code).get();
                    String title = document.getElementsByClass("skin-page-title").get(0).selectFirst("h1").text();
                    List<Gamemode> gamemodes = new ArrayList<>();
                    String[] m = document.getElementsByClass("skin-page-detail").get(1).text().replace("Modes ", "").split(" ");

                    for (String modes : m) {
                        gamemodes.add(Gamemode.valueOf(modes));
                    }
                    MessageEmbed embed = embedSkin(title, "https://osuskins.net/screenshots/" + code + ".jpg", gamemodes, "https://osuskins.net/skin/" + code).build();
                    event.getChannel().sendMessage(embed).queue();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public EmbedBuilder embedSkin(String title, String image, List<Gamemode> gamemodes, String url){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Ou!su Tracking");
        embed.setTitle(title, url);
        embed.setImage(image);
        embed.setFooter("Skins by osuskins.net","https://osuskins.net/favicon-32x32.png");
        StringBuilder gamemode = new StringBuilder();
        for (Gamemode mode : gamemodes) {
            gamemode.append(Objects.requireNonNull(GenericsEmotes.getEmote(mode.name().toLowerCase())).getAsMention()).append(" ").append(mode.name()).append("\n");
        }
        embed.addField("Gamemodes", gamemode.toString(), true);

        try {
            embed.setColor(ImageUtils.getPredominatColor(ImageIO.read(new URL(url))));
        } catch (Exception e){
            embed.setColor(Color.YELLOW);
        }
        return embed;
    }
}


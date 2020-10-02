package me.skiincraft.discord.ousu.listener;

import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.discord.core.utils.ImageUtils;
import me.skiincraft.discord.core.utils.IntegerUtils;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.embed.SearchEmbed;
import me.skiincraft.discord.ousu.emojis.OusuEmote;
import me.skiincraft.discord.ousu.htmlpage.BeatmapSearch;
import me.skiincraft.discord.ousu.htmlpage.JSoupGetters;
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

public class BeatmapTracking extends ListenerAdapter {

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
        if (messageraw.contains("osu.ppy.sh/beatmapsets/")){
            String geturl = messageraw.substring(messageraw.indexOf("beatmapsets/"));
            geturl = geturl.substring(geturl.indexOf("/")+1);

            String[] split = geturl.split("/");
            if (split.length >= 3){
                if (IntegerUtils.isNumeric(split[2])){
                    return;
                }
            }
            new Thread(() -> {
                long beatmapset = Long.parseLong(split[0].replaceAll("\\D+", ""));
                if (split.length == 2) try {
                    long beatmap = Long.parseLong(split[1].replaceAll("\\D+", ""));
                    BeatmapSearch search = JSoupGetters.beatmapInfoById(beatmap);
                    event.getChannel().sendMessage(SearchEmbed.searchEmbed(search, event.getGuild()).build()).queue();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                try {
                    BeatmapSearch search = JSoupGetters.beatmapInfo(beatmapset);
                    if (search == null) return;
                    event.getChannel().sendMessage(SearchEmbed.searchEmbed(search, event.getGuild()).build()).queue();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, "tracking").start();
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
            gamemode.append(OusuEmote.getEmote(mode.name().toLowerCase()).getAsMention()).append(" ").append(mode.name()).append("\n");
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


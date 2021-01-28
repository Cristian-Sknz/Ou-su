package me.skiincraft.ousubot.listeners;

import me.skiincraft.api.osu.entity.beatmap.Beatmap;
import me.skiincraft.api.osu.entity.beatmap.BeatmapSet;
import me.skiincraft.api.osu.requests.Endpoint;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.EventMap;
import me.skiincraft.discord.core.common.EventListener;
import me.skiincraft.discord.core.language.Language;
import me.skiincraft.ousubot.api.OusuAPI;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousubot.view.models.BeatmapSimple;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

@EventMap
public class BeatmapTracking implements EventListener {

    @Inject
    private OusuAPI api;
    private MessageModel messageModel;

    @SubscribeEvent
    public void temporaryTracker(@NotNull GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        if (this.messageModel == null)
            this.messageModel = new MessageModel("embeds/search" , Language.getGuildLanguage(event.getGuild()));
        if (!event.getChannel().canTalk() || message.getAuthor().isBot() || message.isWebhookMessage()){
            return;
        }
        String messageraw = message.getContentRaw().split(" ")[0].toLowerCase();
        if (!messageraw.startsWith("http") || !messageraw.startsWith("https")){
            return;
        }

        if (messageraw.contains("osu.ppy.sh/beatmapsets/")){
            long beatmapSetId = getBeatmapSetIdFromURL(messageraw);
            if (beatmapSetId == 0){
                return;
            }
            ifExistsBeatmapSetSendMessage(beatmapSetId, event.getChannel());
            return;
        }
        if (messageraw.contains("osu.ppy.sh/beatmaps/")){
            long beatmapSetId = getBeatmapIdFromURL(messageraw);
            if (beatmapSetId == 0){
                return;
            }
            ifExistsBeatmapSendMessage(beatmapSetId, event.getChannel());
        }
    }

    public void ifExistsBeatmapSetSendMessage(long beatmapSetId, TextChannel channel){
        try {
            Endpoint endpoint = api.getAvailableTokens().getEndpoint();
            BeatmapSet beatmapSet = endpoint.getBeatmapSet(beatmapSetId).get();
            messageModel.setLanguage(Language.getGuildLanguage(channel.getGuild()));
            EmbedBuilder embed = getModelEmbedBuilder(messageModel, beatmapSet);
            channel.sendMessage(embed.build()).queue();
        } catch (Exception ignored){}
    }

    public void ifExistsBeatmapSendMessage(long beatmap, TextChannel channel){
        try {
            Endpoint endpoint = api.getAvailableTokens().getEndpoint();
            Beatmap beatmapSet = endpoint.getBeatmap(beatmap).get();
            messageModel.setLanguage(Language.getGuildLanguage(channel.getGuild()));
            EmbedBuilder embed = getModelEmbedBuilder(messageModel, beatmapSet);
            channel.sendMessage(embed.build()).queue();
        } catch (Exception ignored){}
    }

    public EmbedBuilder getModelEmbedBuilder(MessageModel model, BeatmapSet beatmapSet){
        model.addProperty("beatmapAdapter", new BeatmapSimple(beatmapSet, model.getEmotes()));
        return model.getEmbedBuilder();
    }

    public EmbedBuilder getModelEmbedBuilder(MessageModel model, Beatmap beatmapSet){
        model.addProperty("beatmapAdapter", new BeatmapSimple(beatmapSet, model.getEmotes()));
        return model.getEmbedBuilder();
    }

    private boolean isNumeric(String args){
        return args.matches("-?\\d+(\\.\\d+)?");
    }

    public long getBeatmapIdFromURL(String url){
        String[] replaces = new String[]{"https://", "http://", "osu.ppy.sh/beatmaps/"};
        String beatmapId = url;
        for (String str : replaces){
            beatmapId = beatmapId.replace(str, "");
        }
        if (beatmapId.length() < 3){
            return 0;
        }

        if (beatmapId.contains("/")){
            String[] beatmapsIds = beatmapId.split("/");
            return Long.parseLong(beatmapsIds[0].replaceAll("\\D+", ""));
        }
        return Long.parseLong(url.replaceAll("\\D+", ""));
    }

    public long getBeatmapSetIdFromURL(String url){
        String[] replaces = new String[]{"https://", "http://", "osu.ppy.sh/beatmapsets/"};
        String beatmapSetId = url;
        for (String str : replaces){
            beatmapSetId = beatmapSetId.replace(str, "");
        }
        if (beatmapSetId.length() < 3){
            return 0;
        }

        if (beatmapSetId.contains("/")){
            String[] beatmapsIds = beatmapSetId.split("/");
            return Long.parseLong(beatmapsIds[0].replaceAll("\\D+", ""));
        }
        return Long.parseLong(url.replaceAll("\\D+", ""));
    }
}

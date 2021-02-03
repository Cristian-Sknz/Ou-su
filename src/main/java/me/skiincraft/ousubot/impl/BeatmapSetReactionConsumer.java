package me.skiincraft.ousubot.impl;

import me.skiincraft.api.osu.entity.beatmap.Beatmap;
import me.skiincraft.ousucore.utils.TriConsumer;
import me.skiincraft.ousubot.OusuBot;
import me.skiincraft.ousubot.models.ChannelTracking;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;
import java.util.List;

public class BeatmapSetReactionConsumer implements TriConsumer<Message, Member, EmbedBuilder> {

    private final List<EmbedBuilder> embeds;
    private final List<Beatmap> beatmaps;

    public BeatmapSetReactionConsumer(List<EmbedBuilder> embeds, List<Beatmap> beatmaps) {
        this.embeds = embeds;
        this.beatmaps = beatmaps;
    }

    public BeatmapSetReactionConsumer(EmbedBuilder[] embeds, List<Beatmap> beatmaps) {
        this(Arrays.asList(embeds), beatmaps);
    }

    @Override
    public void accept(Message t, Member member, EmbedBuilder embedBuilder) {
        if (embedBuilder == null)
            return;
        OusuBot.getTrackingRepository().save(new ChannelTracking(t.getTextChannel(), beatmaps.get(embeds.indexOf(embedBuilder)).getBeatmapId()));
    }
}

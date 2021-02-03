package me.skiincraft.ousubot.impl;

import me.skiincraft.api.osu.entity.score.Score;
import me.skiincraft.ousucore.utils.TriConsumer;
import me.skiincraft.ousubot.OusuBot;
import me.skiincraft.ousubot.models.ChannelTracking;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;
import java.util.List;

public class ScoreReactionConsumer implements TriConsumer<Message, Member, EmbedBuilder> {

    private final List<EmbedBuilder> embeds;
    private final List<Score> score;

    public ScoreReactionConsumer(List<EmbedBuilder> embeds, List<Score> score) {
        this.embeds = embeds;
        this.score = score;
    }

    public ScoreReactionConsumer(EmbedBuilder[] embeds, List<Score> score) {
        this(Arrays.asList(embeds), score);
    }

    @Override
    public void accept(Message t, Member member, EmbedBuilder embedBuilder) {
        if (embedBuilder == null)
            return;
        OusuBot.getTrackingRepository().save(new ChannelTracking(t.getTextChannel(), score.get(embeds.indexOf(embedBuilder)).getBeatmapId()));
    }
}

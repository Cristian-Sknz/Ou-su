package me.skiincraft.ousubot.view.models;

import me.skiincraft.api.osu.entity.score.Score;
import me.skiincraft.api.osu.object.score.ScoreRank;
import me.skiincraft.api.osu.object.score.ScoreStatistics;
import me.skiincraft.ousubot.view.emotes.GenericsEmotes;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ScoreAdapter {

    private final String scoreEmote;
    private final String beatmapName;
    private final String beatmapSetUrl;

    private final String version;
    private final String beatmapUrl;
    private final String pp;
    private final long combo;
    private final String scoreUrl;
    private final long totalScore;
    private final String score;
    private final String userName;
    private final long userId;

    private final long beatmapSetId;
    private final String creator;
    private final long creatorId;


    public ScoreAdapter(Score score, GenericsEmotes emotes) {
        this.scoreEmote = getRankEmote(score.getScoreRank(), emotes);
        this.beatmapName = Objects.requireNonNull(score.getBeatmapSet()).getTitle();
        this.beatmapSetUrl = score.getBeatmapSet().getURL();
        this.version = Objects.requireNonNull(score.getBeatmap()).getVersion();
        this.beatmapUrl = score.getBeatmap().getURL();
        this.pp = (score.getPP() == 0) ? "?" : String.valueOf((int) score.getPP());
        this.combo = score.getMaxCombo();
        this.scoreUrl = score.getScoreUrl();
        this.totalScore = score.getScore();
        String[] scoreTypes = new String[]{emotes.getEmoteAsMentionEquals("300"),
                emotes.getEmoteAsMentionEquals("100"),
                emotes.getEmoteAsMentionEquals("50"),
                emotes.getEmoteAsMentionEquals("miss")};
        ScoreStatistics statistics = score.getStatistics();
        int[] scores = new int[]{statistics.get300(), statistics.get100(), statistics.get50(), statistics.getMiss()};
        AtomicInteger i = new AtomicInteger(0);
        this.score = Arrays.stream(scoreTypes).map(t -> t + ": " + scores[i.getAndIncrement()]).collect(Collectors.joining(" | "));
        this.userName = score.getUser().getUsername();
        this.userId = score.getUserId();
        this.beatmapSetId = score.getBeatmapSetId();
        this.creator = score.getBeatmapSet().getCreator();
        this.creatorId = score.getBeatmapSet().getUserId();
    }

    public String getRankEmote(ScoreRank rank, GenericsEmotes emote){
        switch (rank) {
            case SSh:
                return emote.getEmoteAsMentionEquals("SS_Plus");
            case SS:
                return emote.getEmoteAsMentionEquals("SS");
            case Sh:
                return emote.getEmoteAsMentionEquals("S_Plus");
            default:
                return emote.getEmoteAsMentionEquals(rank.getName() + "_");
        }
    }

    public String getScoreEmote() {
        return scoreEmote;
    }

    public String getBeatmapName() {
        return beatmapName;
    }

    public String getBeatmapSetUrl() {
        return beatmapSetUrl;
    }

    public String getVersion() {
        return version;
    }

    public String getBeatmapUrl() {
        return beatmapUrl;
    }

    public String getPp() {
        return pp;
    }

    public long getCombo() {
        return combo;
    }

    public String getScoreUrl() {
        return scoreUrl;
    }

    public long getTotalScore() {
        return totalScore;
    }

    public String getScore() {
        return score;
    }

    public String getUserName() {
        return userName;
    }

    public long getUserId() {
        return userId;
    }

    public long getBeatmapSetId() {
        return beatmapSetId;
    }

    public String getCreator() {
        return creator;
    }

    public long getCreatorId() {
        return creatorId;
    }
}

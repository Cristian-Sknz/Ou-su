package me.skiincraft.ousubot.view.models;

import me.skiincraft.api.osu.entity.beatmap.Beatmap;
import me.skiincraft.api.osu.entity.beatmap.BeatmapSet;
import me.skiincraft.api.osu.object.game.GameMode;
import me.skiincraft.ousubot.view.emotes.GenericsEmotes;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BeatmapSimple {

    private final long beatmapId;
    private final long beatmapSetId;

    private final String title;
    private final String creator;
    private final long creatorId;
    private final String genre;
    private final String modeEmote;
    private final String mode;
    private final String approval;
    private final String version;
    private final String successRate;
    private final int maxCombo;
    private final float bpm;
    private final String stars;
    private String artist;
    private String approvalDate;

    public BeatmapSimple(Beatmap beatmap, GenericsEmotes emotes) {
        this.beatmapId = beatmap.getBeatmapId();
        this.beatmapSetId = beatmap.getBeatmapSetId();
        this.title = beatmap.getBeatmapSet().getTitle();
        this.artist = beatmap.getBeatmapSet().getArtist();
        if (beatmap.getBeatmapSet().getArtistUnicode() != null && !beatmap.getBeatmapSet().getArtistUnicode().equalsIgnoreCase(artist)) {
            this.artist = "`" + beatmap.getBeatmapSet().getArtistUnicode() + "`  (" + beatmap.getBeatmapSet().getArtist() + ")";
        }
        this.creator = beatmap.getBeatmapSet().getCreator();
        this.creatorId = beatmap.getBeatmapSet().getUserId();
        this.genre = beatmap.getBeatmapSet().getGenre().getName();
        this.mode = modeName(beatmap.getGameMode());
        this.modeEmote = emotes.getEmoteAsMentionEquals(modeName(beatmap.getGameMode()));
        this.approval = beatmap.getBeatmapSet().getStatus().name();
        this.version = beatmap.getVersion();
        DecimalFormat df = new DecimalFormat("#.00");
        this.successRate = df.format((beatmap.getPassCount() * 100) / beatmap.getPlayCount());
        this.maxCombo = beatmap.getSliders() + beatmap.getCircles() + beatmap.getSpinners();
        this.approvalDate = "?";
        if (beatmap.getBeatmapSet().getRankedDate() != null) {
            approvalDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(beatmap.getBeatmapSet().getRankedDate());
        }
        this.bpm = beatmap.getBPM();
        this.stars = beatmap.getDifficultRatingStars();
    }

    public BeatmapSimple(BeatmapSet beatmapSet, GenericsEmotes emotes) {
        List<Beatmap> beatmaps = beatmapSet.getBeatmaps();
        this.beatmapId = beatmaps.get(0).getBeatmapId();
        this.beatmapSetId = beatmapSet.getBeatmapSetId();
        this.title = beatmapSet.getTitle();
        this.artist = beatmapSet.getArtist();
        this.creator = beatmapSet.getCreator();
        this.creatorId = beatmapSet.getUserId();
        this.genre = beatmapSet.getGenre().getName();
        this.modeEmote = emotes.getEmoteAsMentionEquals(modeName(beatmaps.get(0).getGameMode()));
        this.mode = modeName(beatmaps.get(0).getGameMode());
        this.approval = beatmapSet.getStatus().name();
        this.version = beatmaps.stream().limit(5)
                .map(beatmap -> emotes.getEmoteAsMentionEquals("rainbowcircle") + " " + beatmap.getVersion())
                .collect(Collectors.joining("\n")) + ((beatmaps.size() > 5) ? "*[...]*" : "");
        this.successRate = "?";
        this.maxCombo = beatmaps.get(0).getMaxCombo();
        this.approvalDate = Objects.requireNonNull(beatmapSet.getRankedDate()).format(DateTimeFormatter.ofPattern("dd/MM/yy-HH:mm"));
        this.bpm = beatmapSet.getBPM();
        this.stars = "?";
    }

    private String modeName(GameMode mode) {
        switch (mode) {
            case Osu:
                return "Standard";
            case Taiko:
                return "Taiko";
            case Mania:
                return "Mania";
            case Fruits:
                return "Catch";
        }
        return "Standard";
    }

    public long getBeatmapId() {
        return beatmapId;
    }

    public long getBeatmapSetId() {
        return beatmapSetId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getCreator() {
        return creator;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public String getGenre() {
        return genre;
    }

    public String getModeEmote() {
        return modeEmote;
    }

    public String getMode() {
        return mode;
    }

    public String getApproval() {
        return approval;
    }

    public String getVersion() {
        return version;
    }

    public String getSuccessRate() {
        return successRate;
    }

    public int getMaxCombo() {
        return maxCombo;
    }

    public String getApprovalDate() {
        return approvalDate;
    }

    public float getBpm() {
        return bpm;
    }

    public String getStars() {
        return stars;
    }
}

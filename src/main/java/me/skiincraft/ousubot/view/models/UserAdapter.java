package me.skiincraft.ousubot.view.models;

import me.skiincraft.api.osu.entity.user.SimpleUser;
import me.skiincraft.api.osu.entity.user.User;
import me.skiincraft.api.osu.object.user.PlayStyle;
import me.skiincraft.ousubot.view.emotes.GenericsEmotes;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserAdapter {

    private final String status;
    private final String pprank;
    private final String playedTime;
    private final String accuracy;

    private final long userId;
    private final String username;
    private final String countryCode;
    private final String countryRank;
    private final String pp;
    private final String totalScore;

    private final String userAccount;
    private final String inputs;
    private final String previousNames;

    public UserAdapter(User user, GenericsEmotes emotes) {
        this.status = (user.isOnline()) ? ":green_circle: Online" : ":black_circle: Offline";
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        this.pprank = nf.format(user.getStatistics().getPpRank());
        this.playedTime = nf.format(TimeUnit.SECONDS.toHours(user.getStatistics().getPlayTime()));
        this.accuracy = new DecimalFormat("#.00").format(user.getStatistics().getHitAccuracy());
        this.userId = user.getId();
        this.username = user.getUsername();
        this.countryCode = user.getCountryCode();
        this.countryRank = nf.format(user.getStatistics().getCountryRank());
        this.pp = nf.format((int) user.getStatistics().getPp());
        this.totalScore = nf.format(user.getStatistics().getTotalScore());

        this.userAccount = Stream.of(isSupporter(user, emotes), isActive(user, emotes), containsBadges(user, emotes))
                .filter(args -> args.length() != 0).collect(Collectors.joining("\n"));
        this.previousNames = previousNames(user);
        this.inputs = inputs(user);
    }

    public UserAdapter(SimpleUser user) {
        this.status = null;
        this.pprank = "0";
        this.playedTime = "0";
        this.accuracy = "0";
        this.userId = user.getId();
        this.username = user.getUsername();
        this.countryCode = user.getCountryCode();
        this.countryRank = "0";
        this.pp = "0";
        this.totalScore = "0";
        this.userAccount = null;
        this.previousNames = null;
        this.inputs = null;
    }

    private String isSupporter(User user, GenericsEmotes emotes) {
        return (user.isSupporter()) ? emotes.getEmoteAsMentionEquals("empty") + " Supporter " + emotes.getEmoteAsMentionEquals("osusupporter") : "";
    }

    private String isActive(User user, GenericsEmotes emotes) {
        return emotes.getEmoteAsMentionEquals("empty") + " Status:" + ((user.isActive()) ? "Active" : "Inative");
    }

    private String containsBadges(User user, GenericsEmotes emotes) {
        return (user.getBadges().size() != 0) ? emotes.getEmoteAsMentionEquals("empty") + " " + user.getBadges().size() + " Badges" : "";
    }

    private String previousNames(User user) {
        return "`" + ((user.getPreviousUsernames() != null && user.getPreviousUsernames().length != 0) ? String.join("\n", user.getPreviousUsernames()) : user.getUsername()) + "`";
    }

    private String inputs(User user) {
        if (user.getPlayStyles() == null || user.getPlayStyles().length == 0) {
            return "N/A";
        }
        return Arrays.stream(user.getPlayStyles()).map(pl -> playstyleEmote(pl) + " " + pl.name()).collect(Collectors.joining("\n"));
    }

    private String playstyleEmote(PlayStyle style) {
        switch (style) {
            case Keyboard:
                return ":keyboard:";
            case Tablet:
                return "<:tablet:717125447527301200>";
            case Mouse:
                return ":mouse_three_button:";
            case Touchpad:
                return "<:touchpad:717126686151278704>";
        }
        return "";
    }

    public String getStatus() {
        return status;
    }

    public String getPprank() {
        return pprank;
    }

    public String getPlayedTime() {
        return playedTime;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryRank() {
        return countryRank;
    }

    public String getPp() {
        return pp;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public String getInputs() {
        return inputs;
    }

    public String getPreviousNames() {
        return previousNames;
    }
}

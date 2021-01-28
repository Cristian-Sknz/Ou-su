package me.skiincraft.ousubot.view.models;

import me.skiincraft.api.osu.entity.user.User;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

    public UserAdapter(User user) {
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
}

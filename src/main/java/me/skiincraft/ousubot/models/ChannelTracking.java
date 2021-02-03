package me.skiincraft.ousubot.models;

import me.skiincraft.ousubot.OusuBot;
import me.skiincraft.sql.annotation.Id;
import me.skiincraft.sql.annotation.Table;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Base64;
import java.util.Optional;

@Table("tb_channels")
public class ChannelTracking {

    @Id
    private String identification;
    private long channelId;
    private long guildId;
    private long beatmapId;

    public ChannelTracking() {
    }

    public ChannelTracking(TextChannel channel) {
        this.identification = Base64.getEncoder().encodeToString((channel.getIdLong() + "" + channel.getGuild().getIdLong()).getBytes());
        this.channelId = channel.getIdLong();
        this.guildId = channel.getGuild().getIdLong();
        this.beatmapId = 0;
    }

    public ChannelTracking(long channelId, long guildId) {
        this.identification = Base64.getEncoder().encodeToString((channelId + "" + guildId).getBytes());
        this.channelId = channelId;
        this.guildId = guildId;
        this.beatmapId = 0;
    }

    public ChannelTracking(TextChannel channel, long beatmapId) {
        this.identification = Base64.getEncoder().encodeToString((channel.getIdLong() + "" + channel.getGuild().getIdLong()).getBytes());
        this.channelId = channel.getIdLong();
        this.guildId = channel.getGuild().getIdLong();
        this.beatmapId = beatmapId;
    }

    public static String getIdentification(TextChannel channel) {
        return Base64.getEncoder().encodeToString((channel.getIdLong() + "" + channel.getGuild().getIdLong()).getBytes());
    }

    public static Optional<ChannelTracking> getFromRepository(TextChannel channel) {
        return OusuBot.getTrackingRepository().getById(ChannelTracking.getIdentification(channel));
    }

    public String getIdentification() {
        return identification;
    }

    public ChannelTracking setIdentification(String identification) {
        this.identification = identification;
        return this;
    }

    public long getChannelId() {
        return channelId;
    }

    public ChannelTracking setChannelId(long channelId) {
        this.channelId = channelId;
        return this;
    }

    public long getGuildId() {
        return guildId;
    }

    public ChannelTracking setGuildId(long guildId) {
        this.guildId = guildId;
        return this;
    }

    public long getBeatmapId() {
        return beatmapId;
    }

    public ChannelTracking setBeatmapId(long beatmapId) {
        this.beatmapId = beatmapId;
        return this;
    }
}

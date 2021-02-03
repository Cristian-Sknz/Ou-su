package me.skiincraft.ousubot.api;

import com.google.gson.JsonParser;
import me.skiincraft.ousucore.OusuCore;
import me.skiincraft.ousubot.OusuBot;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.discordbots.api.client.DiscordBotListAPI;
import org.discordbots.api.client.impl.DiscordBotListAPIImpl;

import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class DiscordBotAPI {

    private final DiscordBotListAPI api;

    public DiscordBotAPI() {
        this.api = new DiscordBotListAPIImpl(getToken(), OusuCore.getShardManager().getShards().get(0).getSelfUser().getId());
    }

    public void updateStats() {
        api.setStats(OusuCore.getShardManager().getShards()
                .stream()
                .map(jda -> Integer.parseInt("" + jda.getGuildCache().size()))
                .collect(Collectors.toList()));
    }

    public DiscordBotListAPI getAPI() {
        return api;
    }

    public ShardManager getShardManager() {
        return OusuCore.getShardManager();
    }

    public String getToken() {
        return new JsonParser().parse(new InputStreamReader(OusuBot.class.getResourceAsStream("/DBLToken.json")))
                .getAsJsonObject()
                .get("key")
                .getAsString();
    }
}

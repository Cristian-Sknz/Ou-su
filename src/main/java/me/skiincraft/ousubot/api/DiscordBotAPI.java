package me.skiincraft.ousubot.api;

import me.skiincraft.beans.annotation.Component;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.discord.core.OusuCore;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.discordbots.api.client.DiscordBotListAPI;
import org.discordbots.api.client.impl.DiscordBotListAPIImpl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class DiscordBotAPI {

    @Inject
    private ShardManager shardManager;
    private DiscordBotListAPI api;

    public DiscordBotAPI(){
        this.api = new DiscordBotListAPIImpl(getToken(), shardManager.getShards().get(0).getSelfUser().getId());
    }

    public void updateStats(){
        api.setStats(shardManager.getShards()
                .stream()
                .map(jda -> Integer.parseInt("" + jda.getGuildCache().size()))
                .collect(Collectors.toList()));
    }

    public DiscordBotListAPI getAPI() {
        return api;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public String getToken() {
        try {
            return new String(Files.readAllBytes(Paths.get(OusuCore.getAssetsPath().toFile().getAbsolutePath() + "/dbl-token.txt")));
        } catch (Exception e){
            throw new RuntimeException("Não foi possível conectar a API do DiscordBotList");
        }
    }
}

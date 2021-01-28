package me.skiincraft.ousubot.listeners;

import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.EventMap;
import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.common.EventListener;
import me.skiincraft.ousubot.api.DiscordBotAPI;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@EventMap
public class DBListener implements EventListener {

	@Inject
	private ShardManager shardManager;
	@Inject
	private DiscordBotAPI api;
	
	@SubscribeEvent
	public void onJoinServer(GuildJoinEvent event) {
		System.out.printf("Foi adicionado uma nova Guild(%s)", event.getGuild().getName());
		api.updateStats();
	}
	
	public static String getToken() throws IOException {
		return new String(Files.readAllBytes(Paths.get(OusuCore.getAssetsPath().toFile().getAbsolutePath() + "/dbl-token.txt")));
	}
}

package me.skiincraft.discord.ousu.listener;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.stream.Collectors;

import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import org.discordbots.api.client.DiscordBotListAPI;
import org.discordbots.api.client.impl.DiscordBotListAPIImpl;

import me.skiincraft.discord.core.event.EventTarget;
import me.skiincraft.discord.core.event.Listener;
import me.skiincraft.discord.core.events.bot.BotJoinEvent;
import me.skiincraft.discord.core.events.bot.BotReceivedMessage;
import me.skiincraft.discord.ousu.OusuBot;

import net.dv8tion.jda.api.entities.User;

public class ReceivedListener implements Listener {
	
	@EventTarget
	public void onJoinServer(BotJoinEvent event) {
		System.out.println(event.getGuild().getName() + " foi adicionado.");
		try {
			DiscordBotListAPI dbl = new DiscordBotListAPIImpl(getToken(), event.getSelfUser().getId());
			dbl.setStats(OusuBot.getInstance().getShardManager().getShards()
					.stream()
					.map(j -> Integer.valueOf(j.getGuildCache().size()+""))
					.collect(Collectors.toList()));
		} catch (Exception e) {
			System.out.println("Não foi possivel atualizar o DiscordBotList");
		}
	}
	
	public static String getToken() throws IOException {
		Scanner scam = new Scanner(new InputStreamReader(new File(OusuCore.getAssetsPath().toFile().getAbsolutePath() + "/dbl-token.txt").toURI().toURL().openStream()));
		String str = scam.nextLine();
		scam.close();
		return str;
	}

}

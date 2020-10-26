package me.skiincraft.discord.ousu.listener;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.discordbots.api.client.DiscordBotListAPI;
import org.discordbots.api.client.impl.DiscordBotListAPIImpl;

import me.skiincraft.discord.core.event.EventTarget;
import me.skiincraft.discord.core.event.Listener;
import me.skiincraft.discord.core.events.bot.BotJoinEvent;
import me.skiincraft.discord.core.events.bot.BotReceivedMessage;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.CreatedReactionEvent;

import net.dv8tion.jda.api.entities.User;

public class ReactionListeners implements Listener {
	
	@EventTarget
	public void reactionReset(CreatedReactionEvent event) {
		Thread thread = new Thread(() ->{
			try {
				Thread.sleep(TimeUnit.MINUTES.toMillis(2));
				event.delete();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}
	
	@EventTarget
	public void onReceivedMessage(BotReceivedMessage e) {
		User autor = e.getMessage().getAuthor();
		System.out.println(">" + autor.getName() + "#" +autor.getDiscriminator() +" Digitou o comando: " + e.getMessage().getContentRaw());
	}
	
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
		Scanner scam = new Scanner(new InputStreamReader(new File(OusuBot.getInstance().getPlugin().getAssetsPath() + "/dbl-token.txt").toURI().toURL().openStream()));
		String str = scam.nextLine();
		scam.close();
		return str;
	}

}

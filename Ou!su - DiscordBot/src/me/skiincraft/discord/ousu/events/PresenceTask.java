package me.skiincraft.discord.ousu.events;

import java.util.TimerTask;

import me.skiincraft.discord.ousu.OusuBot;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;

public class PresenceTask extends TimerTask {

	public static int ordem = 0;
	Presence presence = OusuBot.getJda().getPresence();
	
	@Override
	public void run() {
		System.out.println("Trocando de Rich Presence");
		if (ordem == 0) {
			presence.setPresence(OnlineStatus.ONLINE, Activity.watching(presence.getJDA().getUsers().size() + " Usuarios!"));
			ordem++;
			return;
		}
		
		if (ordem == 1) {
			presence.setPresence(OnlineStatus.ONLINE, Activity.listening("☕ | Ou!Help for help."));
			ordem++;
			return;
		}
		
		if (ordem == 2) {
			presence.setPresence(OnlineStatus.ONLINE, Activity.watching(presence.getJDA().getGuilds().size() + " Servidores."));
			ordem++;
			return;
		}
		
		ordem = 0;
		this.run();
	}

}

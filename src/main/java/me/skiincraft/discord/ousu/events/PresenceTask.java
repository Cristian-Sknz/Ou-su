package me.skiincraft.discord.ousu.events;

import java.util.List;
import java.util.TimerTask;

import me.skiincraft.discord.ousu.OusuBot;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.Presence;

public class PresenceTask extends TimerTask {

	public static int ordem = 0;
	Presence presence = OusuBot.getJda().getPresence();

	@Override
	public void run() {
		if (ordem == 0) {
			presence.setPresence(OnlineStatus.ONLINE, Activity.listening("☕ | Ou!Help for help."));
			ordem++;
			return;
		}

		if (ordem == 1) {
			List<Guild> guild = OusuBot.getJda().getGuilds();
			int numero = 0;
			for (Guild guilda : guild) {
				numero += guilda.getMemberCount();
			}

			presence.setPresence(OnlineStatus.ONLINE, Activity.watching(numero + " Usuarios Online!"));
			ordem++;
			return;
		}

		if (ordem == 2) {
			presence.setPresence(OnlineStatus.ONLINE,
					Activity.watching(presence.getJDA().getGuilds().size() + " Servidores."));
			ordem++;
			return;
		}
		if (ordem == 3) {
			presence.setPresence(OnlineStatus.ONLINE, Activity.watching("🆕 ou!search to search for beatmaps"));
			ordem++;
			return;
		}

		ordem = 0;

		if (ordem == 0) {
			presence.setPresence(OnlineStatus.ONLINE, Activity.listening("☕ | Ou!Help for help."));
			ordem++;
			return;
		}
	}

}

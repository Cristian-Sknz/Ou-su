package me.skiincraft.discord.ousu.events;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.ShardManager;

public class PresenceMessages {

	public List<Activity> getMessages(ShardManager shardm) {
		List<Activity> l = new ArrayList<Activity>();
		int users = shardm.getUsers().size();
		int servers = shardm.getGuilds().size();
		
		l.add(Activity.listening("☕ | Ou!Help for help."));
		l.add(Activity.watching(users + " Usuarios Online!"));
		l.add(Activity.watching(servers + " Servidores"));
		l.add(Activity.watching("🆕 ou!search to search for beatmaps"));
		return l;
	}
}

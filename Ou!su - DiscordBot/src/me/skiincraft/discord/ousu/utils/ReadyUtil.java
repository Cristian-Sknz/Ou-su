package me.skiincraft.discord.ousu.utils;

import java.util.List;

import me.skiincraft.discord.ousu.mysql.SQLAccess;
import net.dv8tion.jda.api.entities.Guild;

public class ReadyUtil {

	public static void updateServerUsers(List<Guild> guilds) {
		for (Guild guild : guilds) {
			SQLAccess sql = new SQLAccess(guild);
			if (sql.getInt("membros") == guild.getMembers().size()) {
				return;
			}
			sql.set("membros", guild.getMembers().size());
		}
	}

	public static void updateServerNames(List<Guild> guilds) {
		for (Guild guild : guilds) {
			SQLAccess sql = new SQLAccess(guild);
			if (sql.getInt("nome") == guild.getMembers().size()) {
				return;
			}
			sql.set("nome", guild.getName());
		}
	}

}

package me.skiincraft.discord.ousu.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.skiincraft.discord.ousu.sqlite.GuildsDB;
import net.dv8tion.jda.api.entities.Guild;

public class ReadyUtil {

	public static void updateServerUsers(List<Guild> guilds) {
		for (Guild guild : guilds) {
			GuildsDB sql = new GuildsDB(guild);
			if (sql.getInt("membros") == guild.getMembers().size()) {
				return;
			}
			sql.set("membros", guild.getMembers().size());
		}
	}

	public static void updateServersData(List<Guild> guilds) {
		for (Guild guild : guilds) {
			GuildsDB sql = new GuildsDB(guild);
			Date data = new Date(TimeUnit.SECONDS.toMillis(guild.getSelfMember().getTimeJoined().toEpochSecond()));
			String simple = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(data);
			if (sql.get("adicionado em") == simple) {
				return;
			}
			sql.set("adicionado em", simple);
		}
	}

}

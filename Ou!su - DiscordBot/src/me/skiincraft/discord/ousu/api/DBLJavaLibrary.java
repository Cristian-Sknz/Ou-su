package me.skiincraft.discord.ousu.api;

import org.discordbots.api.client.DiscordBotListAPI;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.utils.Token;
import net.dv8tion.jda.api.JDA.ShardInfo;

public class DBLJavaLibrary {

	public void connect() {
		if (Token.dbltoken == null) {
			return;
		}

		ShardInfo shardinfo = OusuBot.getJda().getShardInfo();

		DiscordBotListAPI api = new DiscordBotListAPI.Builder().token(Token.dbltoken[0]).botId(Token.dbltoken[1])
				.build();

		api.setStats(shardinfo.getShardId(), shardinfo.getShardTotal(), OusuBot.getJda().getGuilds().size());
	}

}

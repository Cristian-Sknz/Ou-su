package me.skiincraft.discord.ousu.api;

import org.discordbots.api.client.DiscordBotListAPI;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.utils.Token;

public class DBLJavaLibrary {

	public void connect() {
		//ShardInfo shardinfo = OusuBot.getJda().getShardInfo();
		boolean b = true;
		if (b) {
			return;
		}
		DiscordBotListAPI api = new DiscordBotListAPI.Builder()
				.token(Token.dbltoken[0])
				.botId(OusuBot.getJda().getSelfUser().getId()).build();
		
		//api.setStats(shardinfo.getShardId(), shardinfo.getShardTotal(), OusuBot.getJda().getGuilds().size());
		api.setStats(OusuBot.getJda().getGuilds().size());
	}

}

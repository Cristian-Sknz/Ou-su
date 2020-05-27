package me.skiincraft.discord.ousu.api;

import org.discordbots.api.client.DiscordBotListAPI;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.utils.Token;
import net.dv8tion.jda.api.JDA.ShardInfo;

public class DBLJavaLibrary {

	public void connect() {

		ShardInfo shardinfo = OusuBot.getJda().getShardInfo();

		DiscordBotListAPI api = new DiscordBotListAPI.Builder().token(Token.dbltoken).botId("701825726449582192")
				.build();

		api.setStats(shardinfo.getShardId(), shardinfo.getShardTotal(), OusuBot.getJda().getGuilds().size());
	}

}

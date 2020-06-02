package me.skiincraft.discord.ousu.api;

import org.discordbots.api.client.DiscordBotListAPI;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.configuration.ConfigSetup;
import me.skiincraft.discord.ousu.configuration.ConfigSetup.ConfigOptions;
import net.dv8tion.jda.api.JDA.ShardInfo;

public class DBLJavaLibrary {

	private ConfigSetup config = new ConfigSetup();
	
	public void connect() {
		if (new ConfigSetup().getConfig(ConfigOptions.DBL) == "none") {
			return;
		}

		ShardInfo shardinfo = OusuBot.getJda().getShardInfo();

		DiscordBotListAPI api = new DiscordBotListAPI.Builder()
				.token(config.getConfig(ConfigOptions.DBL) + config.getConfig(ConfigOptions.DBL1) + config.getConfig(ConfigOptions.DBL2))
				.botId(config.getConfig(ConfigOptions.BotID)).build();
		
		api.setStats(shardinfo.getShardId(), shardinfo.getShardTotal(), OusuBot.getJda().getGuilds().size());
	}

}

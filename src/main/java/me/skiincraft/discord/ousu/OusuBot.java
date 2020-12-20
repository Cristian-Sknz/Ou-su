package me.skiincraft.discord.ousu;

import me.skiincraft.api.ousu.OusuAPI;
import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.common.PresenceUpdater;
import me.skiincraft.discord.core.common.reactions.ReactionListeners;
import me.skiincraft.discord.core.common.reactions.Reactions;
import me.skiincraft.discord.core.configuration.Language;
import me.skiincraft.discord.core.plugin.OusuPlugin;
import me.skiincraft.discord.ousu.adapter.OsuAPIAdapter;
import me.skiincraft.discord.ousu.commands.*;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import me.skiincraft.discord.ousu.listener.BeatmapTracking;
import me.skiincraft.discord.ousu.listener.ReceivedListener;
import net.dv8tion.jda.api.entities.Activity;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OusuBot extends OusuPlugin {

	private static OusuBot instance;
	private static PresenceUpdater presenceUpdater;

	public static OusuBot getInstance() {
		return instance;
	}
	{
		instance = this;
	}

	public void onEnable() {
		if (!OsuAPIAdapter.existsTokens()){
			System.out.println("Não foi possivel encontrar Tokens para ativação da API, desligando.");
			OusuCore.shutdown();
			return;
		}
		OusuCore.addLanguage(new Language(new Locale("pt", "BR")));
		OusuCore.addLanguage(new Language(new Locale("en", "US")));
		OusuCore.registerCommand(new BeatmapCommand());
		OusuCore.registerCommand(new BeatmapSetCommand());
		OusuCore.registerCommand(new CardCommand());
		OusuCore.registerCommand(new HelpCommand());
		OusuCore.registerCommand(new InviteCommand());
		OusuCore.registerCommand(new LanguageCommand());
		OusuCore.registerCommand(new PingCommand());
		OusuCore.registerCommand(new PrefixCommand());
		OusuCore.registerCommand(new RankingCommand());
		OusuCore.registerCommand(new RecentuserCommand());
		OusuCore.registerCommand(new RestartCommand());
		OusuCore.registerCommand(new SearchCommand());
		OusuCore.registerCommand(new SayCommand());
		OusuCore.registerCommand(new SkinsCommand());
		OusuCore.registerCommand(new TopUserCommand());
		OusuCore.registerCommand(new UserCommand());
		OusuCore.registerCommand(new VoteCommand());
		OusuCore.registerCommand(new MalCommand());
		ReactionListeners listeners = new ReactionListeners();
		Reactions.of(listeners);
		OusuCore.registerListener(listeners);
		OusuCore.registerListener(new ReceivedListener());
		OusuCore.registerListener(new BeatmapTracking());

		presenceUpdater = new PresenceUpdater(Arrays.asList(Activity.listening("ou!help for help."),
				Activity.watching("{guildsize} Servidores online."),
				Activity.listening("ou!vote on Discord Bots")));
		GenericsEmotes.loadEmotes(OusuCore.getAssetsPath().toAbsolutePath() + "/emotes/");
		Executors.newSingleThreadScheduledExecutor()
				.scheduleAtFixedRate(OusuCore::shutdown, 1,1, TimeUnit.HOURS);
	}
	
	public static PresenceUpdater getPresenceUpdater() {
		return presenceUpdater;
	}

	public static OusuAPI getAPI() {
		return OsuAPIAdapter.getOusuAPI();
	}

}

package me.skiincraft.discord.ousu;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.security.auth.login.LoginException;

import org.ocpsoft.prettytime.PrettyTime;

import me.skiincraft.api.ousu.OusuAPI;
import me.skiincraft.api.ousu.exceptions.InvalidTokenException;
import me.skiincraft.discord.ousu.api.CooldownManager;
import me.skiincraft.discord.ousu.api.DBLJavaLibrary;
import me.skiincraft.discord.ousu.commands.BeatMapCommand;
import me.skiincraft.discord.ousu.commands.BeatMapSetCommand;
import me.skiincraft.discord.ousu.commands.CardCommand;
import me.skiincraft.discord.ousu.commands.EmbedCommand;
import me.skiincraft.discord.ousu.commands.HelpCommand;
import me.skiincraft.discord.ousu.commands.InviteCommand;
import me.skiincraft.discord.ousu.commands.LanguageCommand;
import me.skiincraft.discord.ousu.commands.PingCommand;
import me.skiincraft.discord.ousu.commands.PrefixCommand;
import me.skiincraft.discord.ousu.commands.RankingCommand;
import me.skiincraft.discord.ousu.commands.RecentUserCommand;
import me.skiincraft.discord.ousu.commands.SearchCommand;
import me.skiincraft.discord.ousu.commands.SkinsCommand;
import me.skiincraft.discord.ousu.commands.TopUserCommand;
import me.skiincraft.discord.ousu.commands.UserCommand;
import me.skiincraft.discord.ousu.commands.UserImageCommand;
import me.skiincraft.discord.ousu.commands.VersionCommand;
import me.skiincraft.discord.ousu.commands.VoteCommand;
import me.skiincraft.discord.ousu.configuration.ConfigSetup;
import me.skiincraft.discord.ousu.configuration.ConfigSetup.ConfigOptions;
import me.skiincraft.discord.ousu.events.OtherEvents;
import me.skiincraft.discord.ousu.events.PresenceMessages;
import me.skiincraft.discord.ousu.events.ReadyBotEvent;
import me.skiincraft.discord.ousu.events.ReceivedEvent;
import me.skiincraft.discord.ousu.logger.Logging;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.owneraccess.PresenseCommand;
import me.skiincraft.discord.ousu.owneraccess.ServersCommand;
import me.skiincraft.discord.ousu.reactions.BeatmapsetEvent;
import me.skiincraft.discord.ousu.reactions.RankingReactionEvent;
import me.skiincraft.discord.ousu.reactions.RecentuserEvent;
import me.skiincraft.discord.ousu.reactions.SearchReactionsEvent;
import me.skiincraft.discord.ousu.reactions.ServerReactionsEvent;
import me.skiincraft.discord.ousu.reactions.SkinsReactionEvent;
import me.skiincraft.discord.ousu.reactions.TopUserReactionEvent;
import me.skiincraft.discord.ousu.reactions.UserReactionEvent;
import me.skiincraft.discord.ousu.sqlite.SQLite;
import me.skiincraft.discord.ousu.utils.OusuUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class OusuBot {
	
	private static final long restart = TimeUnit.MINUTES.toMillis(60);
	private boolean DBSQL;
	private static OusuBot ousu;
	private static OusuAPI osu;
	private static Logging log;
	private static ShardManager shardmanager;
	private static SQLite connection;
	private static int presenceOrdem = 0;
	public static OusuBot getOusu() {
		return ousu;
	}

	public static ShardManager getShardmanager() {
		return shardmanager;
	}

	public static OusuAPI getOsu() {
		return osu;
	}

	public static SQLite getSQL() {
		return connection;
	}

	public void logger(String message) {
		log.debug(Level.INFO, message, true);
	}

	public void logger(String[] message) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < message.length; i++) {
			buffer.append(message[i] + "\n");
		}
		log.debug(Level.INFO, buffer.toString(), true);
	}

	public void logger(String message, Level level) {
		log.debug(level, message, true);
	}

	public static void main(String[] args) {
		ApplicationUtils.openconsole();
		ConfigSetup config = new ConfigSetup();
		config.makeConfig();
		if (!config.verificarTokens()) {
			System.out.println("| Arquivo de configuração não esta configurado corretamente.");
			System.out.println("| Todos os campos devem ser preenchidos.");
			return;
		}
		arguments = args;
		log = new Logging();
		new OusuBot(config.getConfig(ConfigOptions.Token), 3);
	}

	public static String[] arguments;
		
	public OusuBot(String token, int shards) {
		try {
		ousu = this;
		
		Locale.setDefault(new Locale("pt", "BR"));
		DefaultShardManagerBuilder shardbuilder = new DefaultShardManagerBuilder(token);
		shardbuilder.setShardsTotal(shards);
		events(shardbuilder);
		commands(shardbuilder);
		
		connection = new SQLite(this);
		
		shardbuilder.setDisabledCacheFlags(EnumSet.of(CacheFlag.VOICE_STATE));
		shardbuilder.setChunkingFilter(ChunkingFilter.NONE);
		
		connection.abrir();
		connection.setup();
		try {
			connection.getStatement().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		shardmanager = shardbuilder.build();
		logger("Esperando todas as shards...");
		for (JDA jda :shardmanager.getShards()) {
			jda.awaitReady();
		}
		
		logger("Todas as shards foram carregadas.");
		osuLoader();
		TimerTask timertask = new TimerTask() {
			
			@Override
			public void run() {
				presenceOrdem = (presenceOrdem >= new PresenceMessages().getMessages(shardmanager).size()) ? 0 : presenceOrdem;
				shardmanager.setPresence(OnlineStatus.ONLINE,
						new PresenceMessages().getMessages(shardmanager).get(presenceOrdem));
				presenceOrdem++;
			}
		};

		
		Timer t = new Timer("Presence-Timer");
		t.schedule(timertask, 1000, TimeUnit.SECONDS.toMillis(60));
		SelfUser self = shardmanager.getShardById(0).getSelfUser();
		
		ApplicationUtils.frame.setTitle(ApplicationUtils.frame.getTitle().replace("[Bot]", self.getName()));
		new DBLJavaLibrary().connect();
		
		AppRestartThread(arguments, restart);
		CooldownManager.start();
		
		} catch (LoginException e) {
			System.out.println("JDA: Ocorreu um erro ao logar no bot. Verifique se o Token está correto.");
		} catch (InvalidTokenException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void events(DefaultShardManagerBuilder build) {
		registerEvents(build, new ReceivedEvent(), new TopUserReactionEvent(), new ReadyBotEvent(), new BeatmapsetEvent(),
				new RecentuserEvent(), new ServerReactionsEvent(),
				new SearchReactionsEvent(), new OtherEvents(), new RankingReactionEvent(), new SkinsReactionEvent(),
				new UserReactionEvent());
	}

	public void commands(DefaultShardManagerBuilder build) {

		registerCommands(build, new HelpCommand(), new EmbedCommand(), new UserCommand(), new TopUserCommand(),
				new UserImageCommand(), new PrefixCommand(), new BeatMapCommand(), new VersionCommand(),
				new InviteCommand(), new RecentUserCommand(), new LanguageCommand(), new BeatMapSetCommand(),
				new SearchCommand(), new VoteCommand(), new RankingCommand(), new SkinsCommand(), new CardCommand(),
				new PingCommand());

		registerCommands(build, new PresenseCommand(), new ServersCommand());
	}

	private void registerEvents(DefaultShardManagerBuilder build, ListenerAdapter... events) {
		ListenerAdapter[] comm = events;
		for (int i = 0; i < comm.length; i++) {
			build.addEventListeners(comm[i]);
		}
	}

	private void registerCommands(DefaultShardManagerBuilder build, Commands... commands) {
		Commands[] comm = commands;
		for (int i = 0; i < comm.length; i++) {
			build.addEventListeners(comm[i]);
			HelpCommand.commands.add(comm[i]);
		}
	}

	public void osuLoader() throws InvalidTokenException {
		OusuBot.osu = new OusuAPI(new ConfigSetup().getConfig(ConfigOptions.OsuToken));
	}

	public boolean isDBSQL() {
		return DBSQL;
	}

	public void setDBSQL(boolean dBSQL) {
		DBSQL = dBSQL;
	}
	
	public void AppRestartThread(String[] args, long restart) {
		Runnable runnable = () -> {
			PrettyTime time = new PrettyTime(Locale.forLanguageTag("PT"));
			try {
				for (int i = 0; i < 1; i++) {
					String str = (i == 0) ? time.format(OusuUtils.getDateAfter(restart))
							: time.format(OusuUtils.getDateAfter(restart / 2));
					logger("Essa aplicação irá reiniciar " + str);
					Thread.sleep(restart / 2);
				}
				ApplicationUtils.restartApplication(args);
			} catch (URISyntaxException | IOException | InterruptedException e) {
				e.printStackTrace();
			}
		};
		
		
		Thread thread = new Thread(runnable, "Ousu-Restart-Application");
		thread.start();
	}
	
	public static User getUserById(String id) {
		return getShardmanager().getUserById(id);
	}
	
	public static User getUserById(long id) {
		return getShardmanager().getUserById(id);
	}
	
	public static Guild getGuildById(String id) {
		return getShardmanager().getGuildById(id);
	}
	
	public static Guild getGuildById(long id) {
		return getShardmanager().getGuildById(id);
	}

}

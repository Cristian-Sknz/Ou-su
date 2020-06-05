package me.skiincraft.discord.ousu;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Timer;
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
import me.skiincraft.discord.ousu.customemoji.OusuEmojis;
import me.skiincraft.discord.ousu.events.OtherEvents;
import me.skiincraft.discord.ousu.events.PresenceTask;
import me.skiincraft.discord.ousu.events.ReadyBotEvent;
import me.skiincraft.discord.ousu.events.ReceivedEvent;
import me.skiincraft.discord.ousu.logger.Logging;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLite;
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
import me.skiincraft.discord.ousu.utils.OusuUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class OusuBot {
	
	private JDABuilder build;

	private static final long restart = TimeUnit.MINUTES.toMillis(60);
	private boolean DBSQL;
	private static OusuBot ousu;
	private static OusuAPI osu;
	private static Logging log;
	private static JDA jda;
	private SQLite connection;
	
	public static OusuBot getOusu() {
		return ousu;
	}

	public static OusuAPI getOsu() {
		return osu;
	}

	public static JDA getJda() {
		return jda;
	}
	
	public void setJda(JDA jda) {
		OusuBot.jda = jda;
	}

	public SQLite getSQL() {
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
		new OusuBot().loader(args);
	}

	public static String[] arguments;
	
	private void loader(String[] args) {
		Locale.setDefault(Locale.forLanguageTag("PT"));
		ousu = this;
		log = new Logging();
		arguments = args;
		
		// Configfile
		ConfigSetup config = new ConfigSetup();
		config.makeConfig();
		if (config.verificarTokens() == false) {
			System.out.println("| Arquivo de configuração não esta configurado corretamente.");
			System.out.println("| Todos os campos devem ser preenchidos.");
			return;
		}
		
		build = new JDABuilder(config.getConfig(ConfigOptions.Token));
		commands();
		events();
		
		System.out.println("MYSQL: Conectando ao servidor MySQL.");
		
		connection = new SQLite(this);
		connection.abrir();
		connection.setup();
		
		setupOusu();
	}
	
	public void setupOusu() {
		try {
			build.setDisabledCacheFlags(EnumSet.of(CacheFlag.VOICE_STATE));
			build.setChunkingFilter(ChunkingFilter.NONE);
			jda = build.build();
			jda.awaitReady();
			logger("JDA: Conexão foi estabelecida com sucesso");
			osuLoader();

			// PresenceTask;
			Timer timer = new Timer();
			timer.schedule(new PresenceTask(), 1000, 2 * (60 * 1000));

			ApplicationUtils.frame.setTitle(ApplicationUtils.frame.getTitle().replace("[Bot]", jda.getSelfUser().getName()));
			new OusuEmojis().setEmotes(getJda().getGuildById("680436378240286720").getEmotes());
			
			AppRestartThread(arguments, restart);
			CooldownManager.start();
			
			new DBLJavaLibrary().connect();
		} catch (LoginException e) {
			System.out.println("JDA: Ocorreu um erro ao logar no bot. Verifique se o Token está correto.");
		} catch (InvalidTokenException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void events() {
		registerEvents(new ReceivedEvent(), new TopUserReactionEvent(), new ReadyBotEvent(), new BeatmapsetEvent(),
				new RecentuserEvent(), new ServerReactionsEvent(),
				new SearchReactionsEvent(), new OtherEvents(), new RankingReactionEvent(), new SkinsReactionEvent(),
				new UserReactionEvent());
	}

	public void commands() {

		registerCommands(new HelpCommand(), new EmbedCommand(), new UserCommand(), new TopUserCommand(),
				new UserImageCommand(), new PrefixCommand(), new BeatMapCommand(), new VersionCommand(),
				new InviteCommand(), new RecentUserCommand(), new LanguageCommand(), new BeatMapSetCommand(),
				new SearchCommand(), new VoteCommand(), new RankingCommand(), new SkinsCommand(), new CardCommand());

		registerCommands(new PresenseCommand(),/* new PlayersCommand(),*/ new ServersCommand());
		
		// Comando Players temporariamente desativado.
	}

	private void registerEvents(ListenerAdapter... events) {
		ListenerAdapter[] comm = events;
		for (int i = 0; i < comm.length; i++) {
			build.addEventListeners(comm[i]);
		}
	}

	private void registerCommands(Commands... commands) {
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
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					PrettyTime time = new PrettyTime(Locale.forLanguageTag("PT"));
					logger("\nEssa aplicação irá reiniciar " + time.format(OusuUtils.getDateAfter(restart)));
					Thread.sleep(restart / 2);
					logger("\nEssa aplicação irá reiniciar " + time.format(OusuUtils.getDateAfter(restart)));
					System.out.println();
					Thread.sleep(restart / 2);
					System.out.println("Reiniciando....");
					try {
						ApplicationUtils.restartApplication(args);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		thread.start();

	}

}

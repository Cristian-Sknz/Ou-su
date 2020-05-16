package me.skiincraft.discord.ousu;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import me.skiincraft.api.ousu.OusuAPI;
import me.skiincraft.api.ousu.exceptions.InvalidTokenException;
import me.skiincraft.discord.ousu.commands.BeatMapCommand;
import me.skiincraft.discord.ousu.commands.BeatMapSetCommand;
import me.skiincraft.discord.ousu.commands.EmbedCommand;
import me.skiincraft.discord.ousu.commands.HelpCommand;
import me.skiincraft.discord.ousu.commands.InviteCommand;
import me.skiincraft.discord.ousu.commands.LanguageCommand;
import me.skiincraft.discord.ousu.commands.MentionCommand;
import me.skiincraft.discord.ousu.commands.PlayersCommand;
import me.skiincraft.discord.ousu.commands.PrefixCommand;
import me.skiincraft.discord.ousu.commands.RecentUserCommand;
import me.skiincraft.discord.ousu.commands.SearchCommand;
import me.skiincraft.discord.ousu.commands.TopUserCommand;
import me.skiincraft.discord.ousu.commands.UserCommand;
import me.skiincraft.discord.ousu.commands.UserImageCommand;
import me.skiincraft.discord.ousu.commands.VersionCommand;
import me.skiincraft.discord.ousu.commands.reactions.BeatmapsetEvent;
import me.skiincraft.discord.ousu.commands.reactions.HistoryEvent;
import me.skiincraft.discord.ousu.commands.reactions.PlayerReactionEvent;
import me.skiincraft.discord.ousu.commands.reactions.RecentuserEvent;
import me.skiincraft.discord.ousu.commands.reactions.SearchReactionsEvent;
import me.skiincraft.discord.ousu.commands.reactions.ServerReactionsEvent;
import me.skiincraft.discord.ousu.events.PresenceTask;
import me.skiincraft.discord.ousu.events.ReadyBotEvent;
import me.skiincraft.discord.ousu.events.ReceivedEvent;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLite;
import me.skiincraft.discord.ousu.owneraccess.LogChannelCommand;
import me.skiincraft.discord.ousu.owneraccess.PresenseCommand;
import me.skiincraft.discord.ousu.owneraccess.ServersCommand;
import me.skiincraft.discord.ousu.utils.Token;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OusuBot {

	public static List<String> listPrivated = new ArrayList<String>();
	JDABuilder build = new JDABuilder(token);

	public static OusuBot ousu;
	private static OusuAPI osu;
	private static String token = Token.token; // Isso é uma String estatica com o token.
	private static JDA jda;
	private SQLite connection;
	private static SelfUser selfUser;

	private boolean DBSQL;

	public static OusuBot getOusu() {
		return ousu;
	}

	public static OusuAPI getOsu() {
		return osu;
	}

	public static JDA getJda() {
		return jda;
	}

	public static SelfUser getSelfUser() {
		return selfUser;
	}

	public void setJda(JDA jda) {
		OusuBot.jda = jda;
	}

	public SQLite getSQL() {
		return connection;
	}

	public void logger(String message) {
		System.out.println(message);
	}
	


	public static void main(String[] args) {
		ApplicationUtils.openconsole();
		new OusuBot().loader(args);
	}

	
	public static int tempo = 6;
	
	private void loader(String[] args) {
		ousu = this;

		build.setStatus(OnlineStatus.DO_NOT_DISTURB);

		commands();
		events();

		System.out.println("MYSQL: Conectando ao servidor MySQL.");
		this.connection = new SQLite(this);
		this.connection.abrir();
		this.connection.setup();

		if (this.isDBSQL()) {
			System.out.println("MYSQL: Conexão foi estabelecida com sucesso.");
		} else {
			System.out.println("MYSQL: Falhou ao conectar ao mysql.");
			System.exit(0);
		}
		try {
			jda = build.build();
			jda.awaitReady();
			System.out.println("JDA: Conexão foi estabelecida com sucesso");
			osuLoader();
			
			OusuBot.selfUser = jda.getSelfUser();
			
			Locale.setDefault(new Locale("pt", "BR"));
			
			// PresenceTask;
			Timer timer = new Timer();
			timer.schedule(new PresenceTask(), 1000, 2*(60*1000));	
			
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						int tempo = 120*(60*1000);
						System.out.println();
						System.out.println("Essa aplicação ira reiniciar em 2h");
						System.out.println();Thread.sleep(tempo/2);
						System.out.println();
						System.out.println("Essa aplicação ira reiniciar em 1m");
						System.out.println();Thread.sleep(tempo/2);
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
			ApplicationUtils.frame.setIconImage(ImageIO.read(new URL(jda.getSelfUser().getAvatarUrl())));
			ApplicationUtils.frame.setTitle(ApplicationUtils.frame.getTitle().replace("[Bot]", jda.getSelfUser().getName()));
			
		} catch (LoginException e) {
			System.out.println("JDA: Ocorreu um erro ao logar no bot. Verifique se o Token está correto.");
		} catch (InvalidTokenException e) {
			System.out.println(e.getMessage());
		} catch (IOException  | InterruptedException  e) {
			e.printStackTrace();
		}
		
	}

	public void events() {
		registerEvents(new ReceivedEvent(), new HistoryEvent(), new ReadyBotEvent(), new BeatmapsetEvent(), new RecentuserEvent()
				, new PlayerReactionEvent(), new MentionCommand(), new ServerReactionsEvent(), new SearchReactionsEvent());
	}

	public void commands() {

		registerCommands(new HelpCommand(), new EmbedCommand(), new UserCommand(), new TopUserCommand(),
				new UserImageCommand(), new PrefixCommand(), new BeatMapCommand(), new VersionCommand(),
				new InviteCommand(), new RecentUserCommand(), new LanguageCommand(), new BeatMapSetCommand(),
				new SearchCommand());
		
		registerCommands(new PresenseCommand(), new LogChannelCommand(), new PlayersCommand(), new ServersCommand());
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
		OusuBot.osu = new OusuAPI(Token.osutoken);
	}

	public boolean isDBSQL() {
		return DBSQL;
	}
	
	public void setDBSQL(boolean dBSQL) {
		DBSQL = dBSQL;
	}

}

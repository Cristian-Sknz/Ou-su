package me.skiincraft.discord.ousu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.security.auth.login.LoginException;

import me.skiincraft.api.ousu.OusuAPI;
import me.skiincraft.api.ousu.exceptions.InvalidTokenException;
import me.skiincraft.discord.ousu.commands.BeatMapCommand;
import me.skiincraft.discord.ousu.commands.BeatMapSetCommand;
import me.skiincraft.discord.ousu.commands.EmbedCommand;
import me.skiincraft.discord.ousu.commands.HelpCommand;
import me.skiincraft.discord.ousu.commands.InviteCommand;
import me.skiincraft.discord.ousu.commands.LanguageCommand;
import me.skiincraft.discord.ousu.commands.PrefixCommand;
import me.skiincraft.discord.ousu.commands.RecentUserCommand;
import me.skiincraft.discord.ousu.commands.TopUserCommand;
import me.skiincraft.discord.ousu.commands.UserCommand;
import me.skiincraft.discord.ousu.commands.UserImageCommand;
import me.skiincraft.discord.ousu.commands.VersionCommand;
import me.skiincraft.discord.ousu.commands.reactions.BeatmapsetEvent;
import me.skiincraft.discord.ousu.commands.reactions.HistoryEvent;
import me.skiincraft.discord.ousu.commands.reactions.RecentuserEvent;
import me.skiincraft.discord.ousu.events.ReadyBotEvent;
import me.skiincraft.discord.ousu.events.ReceivedEvent;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.MySQL;
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
	private MySQL connection;
	private static SelfUser selfUser;

	private boolean DBSQL = true;

	public static OusuBot getOusu() {
		return ousu;
	}

	public static OusuAPI getOsu() {
		return osu;
	}

	public JDA getJda() {
		return jda;
	}

	public static SelfUser getSelfUser() {
		return selfUser;
	}

	public void setJda(JDA jda) {
		OusuBot.jda = jda;
	}

	public MySQL getSQL() {
		return connection;
	}

	public void logger(String message) {
		System.out.println(message);
	}

	public static void main(String[] args) {
		new OusuBot().loader();
	}

	private void loader() {
		ousu = this;

		build.setStatus(OnlineStatus.DO_NOT_DISTURB);

		commands();
		events();

		System.out.println("MYSQL: Conectando ao servidor MySQL.");
		this.connection = new MySQL(this);
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
			System.out.println("JDA: Conexão foi estabelecida com sucesso");
			OusuBot.selfUser = jda.getSelfUser();
			osuLoader();
			Locale.setDefault(new Locale("pt", "BR"));
		} catch (LoginException e) {
			System.out.println("JDA: Ocorreu um erro ao logar no bot. Verifique se o Token está correto.");
		} catch (InvalidTokenException e) {
			System.out.println(e.getMessage());
		}
		
	}

	public void events() {
		registerEvents(new ReceivedEvent(), new HistoryEvent(), new ReadyBotEvent(), new BeatmapsetEvent(), new RecentuserEvent());
	}

	public void commands() {

		registerCommands(new HelpCommand(), new EmbedCommand(), new UserCommand(), new TopUserCommand(),
				new UserImageCommand(), new PrefixCommand(), new BeatMapCommand(), new VersionCommand(),
				new InviteCommand(), new RecentUserCommand(), new LanguageCommand(), new BeatMapSetCommand());
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

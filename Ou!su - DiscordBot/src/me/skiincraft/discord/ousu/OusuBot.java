package me.skiincraft.discord.ousu;

import java.util.ArrayList;
import java.util.List;
import javax.security.auth.login.LoginException;

import com.oopsjpeg.osu4j.GameMode;
import com.oopsjpeg.osu4j.backend.EndpointUsers;
import com.oopsjpeg.osu4j.backend.Osu;
import com.oopsjpeg.osu4j.exception.OsuAPIException;

import me.skiincraft.discord.ousu.commands.BeatMapCommand;
import me.skiincraft.discord.ousu.commands.EmbedCommand;
import me.skiincraft.discord.ousu.commands.HelpCommand;
import me.skiincraft.discord.ousu.commands.TopUserCommand;
import me.skiincraft.discord.ousu.commands.UserCommand;
import me.skiincraft.discord.ousu.commands.UserImageCommand;
import me.skiincraft.discord.ousu.commands.VersionCommand;
import me.skiincraft.discord.ousu.commands.reactions.HistoryEvent;
import me.skiincraft.discord.ousu.commands.PrefixCommand;
import me.skiincraft.discord.ousu.events.ReceivedEvent;
import me.skiincraft.discord.ousu.events.onReadyEvent;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.MySQL;
import me.skiincraft.discord.ousu.utils.Token;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class OusuBot {

	public static List<String> listPrivated = new ArrayList<String>();
	JDABuilder build = new JDABuilder(token);
	
	public static OusuBot ousu;
	private static String token = Token.token;
	private static JDA jda;
	private Osu osu;
	private MySQL connection;
	
	private boolean DBSQL = true;

	public static OusuBot getOusu() {
		return ousu;
	}
	public Osu getOsu() {
		return osu;
	}
	public JDA getJda() {
		return jda;
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
		
		this.connection = new MySQL(this);
		System.out.println("MYSQL: Conectando ao servidor MySQL.");
		this.connection.abrir();
		this.connection.setup();
		
		
		if (this.isDBSQL()) {
			System.out.println("MYSQL: Conexão foi estabelecida com sucesso.");
		} else {
			System.out.println("MYSQL: Falhou ao conectar ao mysql.");
			System.exit(0);
		}
			osuLoader();
		try {
			jda = build.build();
			System.out.println("JDA: Conexão foi estabelecida com sucesso");
		} catch (LoginException e) {
			System.out.println("JDA: Ocorreu um erro ao logar no bot. Verifique se o Token está correto.");
		}
		
		
	}
	
	public void events() {
		registerEvents(new ReceivedEvent(), new HistoryEvent(), new onReadyEvent());
	}

	public void commands() {
		
		registerCommands(
				new HelpCommand(), 
				new EmbedCommand(),
				new UserCommand(), 
				new TopUserCommand(), 
				new UserImageCommand(),
				new PrefixCommand(),
				new BeatMapCommand(),
				new VersionCommand());
	}
	
	private void registerEvents(ListenerAdapter...events) {
		ListenerAdapter[] comm = events;
		for (int i = 0; i < comm.length; i++) {
			build.addEventListeners(comm[i]);
		}
	}
	
	private void registerCommands(Commands...commands) {
		Commands[] comm = commands;
		for (int i = 0; i < comm.length; i++) {
			build.addEventListeners(comm[i]);
			HelpCommand.commands.add(comm[i]);
		}
	}
	
	public void osuLoader() {
		this.osu = Osu.getAPI(Token.osutoken);
		try {
			osu.users.query(new EndpointUsers.ArgumentsBuilder("skiincraft").setMode(GameMode.STANDARD).build());
			System.out.println("OSUAPI: Foi estabelecida conexão com sucesso");
		} catch (OsuAPIException e) {
			System.out.println("OSUAPI: Ocorreu um erro ao conectar ao OSUAPI");
			e.printStackTrace();
		}
	}
	
	public boolean isDBSQL() {
		return DBSQL;
	}
	public void setDBSQL(boolean dBSQL) {
		DBSQL = dBSQL;
	}

	//jda.getPresence().setActivity(Activity.playing("Estou em " + 2 + " servidores!"));
}

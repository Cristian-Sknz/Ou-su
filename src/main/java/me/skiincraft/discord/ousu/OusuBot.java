package me.skiincraft.discord.ousu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Locale;

import me.skiincraft.api.ousu.OusuAPI;
import me.skiincraft.api.ousu.exceptions.TokenException;
import me.skiincraft.discord.core.configuration.Language;
import me.skiincraft.discord.core.plugin.OusuPlugin;
import me.skiincraft.discord.core.utils.PresenceUpdater;
import me.skiincraft.discord.ousu.commands.BeatmapCommand;
import me.skiincraft.discord.ousu.commands.BeatmapSetCommand;
import me.skiincraft.discord.ousu.commands.CardCommand;
import me.skiincraft.discord.ousu.commands.HelpCommand;
import me.skiincraft.discord.ousu.commands.InviteCommand;
import me.skiincraft.discord.ousu.commands.LanguageCommand;
import me.skiincraft.discord.ousu.commands.PingCommand;
import me.skiincraft.discord.ousu.commands.PrefixCommand;
import me.skiincraft.discord.ousu.commands.RankingCommand;
import me.skiincraft.discord.ousu.commands.RecentuserCommand;
import me.skiincraft.discord.ousu.commands.RestartCommand;
import me.skiincraft.discord.ousu.commands.SayCommand;
import me.skiincraft.discord.ousu.commands.SearchCommand;
import me.skiincraft.discord.ousu.commands.SkinsCommand;
import me.skiincraft.discord.ousu.commands.TopUserCommand;
import me.skiincraft.discord.ousu.commands.UserCommand;
import me.skiincraft.discord.ousu.commands.VersionCommand;
import me.skiincraft.discord.ousu.commands.VoteCommand;
import me.skiincraft.discord.ousu.commands.fun.RollCommand;
import me.skiincraft.discord.ousu.commands.owner.CharacterCommand;
import me.skiincraft.discord.ousu.commands.owner.CharactersCommand;
import me.skiincraft.discord.ousu.commands.owner.PermissionCommand;
import me.skiincraft.discord.ousu.emojis.OusuEmote;
import me.skiincraft.discord.ousu.listener.BeatmapTracking;
import me.skiincraft.discord.ousu.listener.ReactionListeners;
import me.skiincraft.discord.ousu.listener.RollEvent;
import me.skiincraft.discord.ousu.object.LoadPersonagens;
import me.skiincraft.discord.ousu.reactions.CharReaction;
import me.skiincraft.discord.ousu.reactions.PageReactions;
import me.skiincraft.discord.ousu.reactions.UserReaction;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class OusuBot extends OusuPlugin {

	private static OusuAPI api;
	private static OusuBot main;
	private static PresenceUpdater presenceUpdater;

	public static OusuBot getMain() {
		return main;
	}
	
	private static long apiLast;
	public static OusuAPI getApi() {
		boolean nulls = Arrays.asList(getTokens()).contains(null) || Arrays.asList(getTokens()).size() == 1;
		if (System.currentTimeMillis() - apiLast >= 2000) {
			for (String string : getTokens()) {
				if (string == null)
					continue;
				if (nulls) {
					apiLast = System.currentTimeMillis();
					return api = new OusuAPI(string, false);
				}
				if (api == null) {
					return api = new OusuAPI(string, false); 
				}
				if (api.getToken().equals(string)) {
					continue;
				}
				apiLast = System.currentTimeMillis();
				return api = new OusuAPI(string, false);
			}
		}
		return api;
	}

	public void onEnable() {
		main = this;
		getPlugin().getCommandManager().registerCommand(new BeatmapCommand());
		getPlugin().getCommandManager().registerCommand(new BeatmapSetCommand());
		getPlugin().getCommandManager().registerCommand(new CardCommand());
		getPlugin().getCommandManager().registerCommand(new CharacterCommand());
		getPlugin().getCommandManager().registerCommand(new HelpCommand());
		getPlugin().getCommandManager().registerCommand(new InviteCommand());
		getPlugin().getCommandManager().registerCommand(new LanguageCommand());
		getPlugin().getCommandManager().registerCommand(new PingCommand());
		getPlugin().getCommandManager().registerCommand(new PrefixCommand());
		getPlugin().getCommandManager().registerCommand(new RankingCommand());
		getPlugin().getCommandManager().registerCommand(new RecentuserCommand());
		getPlugin().getCommandManager().registerCommand(new RestartCommand());
		getPlugin().getCommandManager().registerCommand(new SearchCommand());
		getPlugin().getCommandManager().registerCommand(new SayCommand());
		getPlugin().getCommandManager().registerCommand(new RollCommand());
		getPlugin().getCommandManager().registerCommand(new SkinsCommand());
		getPlugin().getCommandManager().registerCommand(new TopUserCommand());
		getPlugin().getCommandManager().registerCommand(new UserCommand());
		getPlugin().getCommandManager().registerCommand(new VersionCommand());
		getPlugin().getCommandManager().registerCommand(new VoteCommand());
		
		getPlugin().getCommandManager().registerCommand(new CharactersCommand());
		getPlugin().getCommandManager().registerCommand(new PermissionCommand());
		
		getPlugin().getEventManager().registerListener(new UserReaction());
		getPlugin().getEventManager().registerListener(new PageReactions());
		getPlugin().getEventManager().registerListener(new ReactionListeners());
		getPlugin().getEventManager().registerListener(new RollEvent());
		getPlugin().getEventManager().registerListener(new CharReaction());
		getPlugin().getEventManager().registerListener(new BeatmapTracking());

		
		
		File file = new File(getPlugin().getAssetsPath() + "/ousutoken.json");
		
		if (!file.exists()) {
			JsonObject object = new JsonObject();
			object.add("Api_1", JsonNull.INSTANCE);
			object.add("Api_2", JsonNull.INSTANCE);
			try {
				FileWriter filew = new FileWriter(file);
				filew.write(object.toString());
				filew.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		OusuEmote.loadEmotes("emotes");
		System.out.println("OusuBot carregado com sucesso.");
		
		getPlugin().addLanguage(new Language(new Locale("pt", "BR")));
		getPlugin().addLanguage(new Language(new Locale("en", "US")));
		
		getPlugin().getShardManager().setPresence(OnlineStatus.ONLINE, Activity.listening("ou!help | [ousucore]"));
		presenceUpdater = new PresenceUpdater(getPlugin(), Arrays.asList(
				Activity.listening("ou!help for help."),
				Activity.watching("{guildsize} Servidores online."),
				Activity.watching("{usersize} Usuarios disponiveis."),
				Activity.streaming("ou!vote on DiscordBots", "https://top.gg/bot/701825726449582192")));
		LoadPersonagens.load();
	}
	
	public static String[] getTokens() {
		try {
			InputStream input = new File(OusuBot.getMain().getPlugin().getAssetsPath() + "/ousutoken.json").toURI().toURL().openStream();
			JsonObject object = new JsonParser().parse(new InputStreamReader(input)).getAsJsonObject();
			boolean allIsNull = object.get("Api_1").isJsonNull() && object.get("Api_2").isJsonNull();
			if (allIsNull) {
				throw new TokenException("As keys Token da API do ousu estão nulas.", null);
			}
			
			String key1 = (object.get("Api_1").isJsonNull()) ? null :  object.get("Api_1").getAsString();
			String key2 = (object.get("Api_2").isJsonNull()) ? null :  object.get("Api_2").getAsString();
			
			return new String[] {key1, key2};
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static PresenceUpdater getPresenceUpdater() {
		return presenceUpdater;
	}

}

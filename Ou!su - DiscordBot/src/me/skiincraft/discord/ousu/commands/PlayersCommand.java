package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.events.TopUserReaction;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import me.skiincraft.discord.ousu.mysql.SQLPlayer;
import me.skiincraft.discord.ousu.richpresence.FakeRichPresence;
import me.skiincraft.discord.ousu.richpresence.PresenceGetter;
import me.skiincraft.discord.ousu.richpresence.Rich;
import me.skiincraft.discord.ousu.utils.DefaultEmbed;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class PlayersCommand extends Commands {

	public PlayersCommand() {
		super("ou!", "players", "players ", null);
	}

	@Override
	public String[] helpMessage(LanguageManager langm) {
		return langm.translatedArrayHelp("OSU_HELPMESSAGE_PLAYERS");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Osu;
	}

	@Override
	public void action(String[] args, String label, User user, TextChannel channel) {
		List<Rich> getter = new PresenceGetter(channel.getGuild(), "osu").getRichPresences();
		if (getter == null||getter.isEmpty()||getter.size() == 0) {
			Map<String, String> map = SQLPlayer.getOrderBy("osu_account", 150, true);
			if (map.isEmpty()||map.size() == 0) {
				sendEmbedMessage(embed(channel.getGuild())).queue();
				return;
			}
			
			List<String> keys = map.keySet().stream()
					.collect(Collectors.toList());
			
			List<String> values = map.values().stream()
					.collect(Collectors.toList());
			for (int i = 0; i < map.size();i++) {
				User u = OusuBot.getJda().getUserById(keys.get(i));
				if (getter == null) {
					getter = new ArrayList<Rich>();
				}
				if (channel.getGuild().isMember(u)) {
					getter.add(new Rich(u, new FakeRichPresence(values.get(i)).build()));
				}
			}
			if (getter.size() == 0) {
				sendEmbedMessage(embed(channel.getGuild())).queue();
				return;
			}
		}
		
		int size = getter.size();
		final List<Rich> finalrich = getter;
		sendEmbedMessage(richformat(getter, 0, channel.getGuild())).queue(new Consumer<Message>() {

			@Override
			public void accept(Message message) {
				message.addReaction("U+25C0").queue();
				message.addReaction("U+1F4AB").queue();
				message.addReaction("U+25B6").queue();
				Rich[] g = new Rich[size];
				finalrich.toArray(g);
				
				ReactionMessage.playersHistory.add(new TopUserReaction(user, message.getId(), g, 0));
			}
		});
	}
	
	public EmbedBuilder embed(Guild guild) {
		SQLAccess sac = new SQLAccess(guild);
		LanguageManager lang = new LanguageManager(Language.valueOf(sac.get("language")));
		
		EmbedBuilder embed = new EmbedBuilder();
		User user = OusuBot.getJda().getUserById("247096601242238991");
		SelfUser self = OusuBot.getJda().getSelfUser();
		embed.setAuthor(self.getName() + "#" + self.getDiscriminator(), null,
				self.getAvatarUrl());
		
		embed.setDescription(lang.translatedOsuMessages("NO_PLAYER_DETECTED"));

		embed.setColor(Color.MAGENTA);
		embed.setFooter(lang.translatedBot("FOOTER_DEFAULT"), user.getAvatarUrl());
		return embed;
	}
	
	public static EmbedBuilder richformat(List<Rich> rich, int value, Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();
		SQLAccess sac = new SQLAccess(guild);
		LanguageManager lang = new LanguageManager(Language.valueOf(sac.get("language")));
		
		User user = rich.get(value).getUser();
		RichPresence presence = rich.get(value).getRich();
		
		
		if (presence.getState() == null) {
			do {
				value++;
				if (value >= rich.size()) {
					String[] msg = lang.translatedArrayOsuMessages("UNAVAILABLE_RESOURCE");
					return new DefaultEmbed(msg[0], msg[1]).construirEmbed();
				}
				user = rich.get(value).getUser();
				presence = rich.get(value).getRich();
			} while (presence.getState() == null);
		}
		boolean offline = presence.getState().contains("OFFLINE");
		String order = "[" + (value + 1) + "/" + rich.size() + "] ";
		embed.setAuthor(order + user.getName() + "#" + user.getDiscriminator(), null,
				user.getAvatarUrl());
		
		embed.setThumbnail(presence.getLargeImage().getUrl());
		
		
		if (offline) {
			embed.setDescription(lang.translatedOsuMessages("OFFLINE_PLAYER"));
		} else {
			embed.setDescription(lang.translatedOsuMessages("ONLINE_PLAYER"));
		}
		for (Rich r : rich) {
			if (r.getRich().getLargeImage() == null) {
				continue;
			}
			if (r.getRich().getLargeImage().getText() == null) {
				continue;
			}
			String[] st = r.getRich().getLargeImage().getText().split(" ");
			String nickname = st[0];
			
			if (st.length == 0 || nickname.equalsIgnoreCase("guest")) {
				continue;
			} else {
				SQLPlayer sql = new SQLPlayer(r.getUser());
				sql.set("osu_account", nickname);
			}
		}
		String[] strs;
		if (presence.getLargeImage().getText() == null) {
			strs = new String[]{"guest"};	
		} else {
			strs = presence.getLargeImage().getText().split(" ");
		}
		
		if (strs.length == 0 || strs[0].equalsIgnoreCase("guest")) {
			embed.addField("Nickname:", "Unregistered user", true);
			embed.addField("Ranking: ", ":(", true);
		} else {
			embed.addField("Nickname:", strs[0], true);
			if (!offline) {
				if (strs.length >= 3) {
				embed.addField("Ranking: ", strs[2].replace(")", ""), true);
				} else {
					embed.addField("Ranking: ", ":(", true);
				}
			} else {
				embed.addField("Ranking: ", ":(", true);
			}
		}
		embed.addBlankField(true);
		if (presence.getState().contains("AFK")) {
			embed.addField("State: ", "AFK", true);
		} else if (offline) {
			embed.addField("State: ", "Offline", true);
		} else if (presence.getState().contains("Idle")) {
			embed.addField("State: ", "Idle", true);
		} else {
			int start = 0;
			int end = 0;
			String details = presence.getDetails().replace("-", "\n");
			
			for (int i = 3; i < 100; i++) {
				if (details.charAt(i) == '[') {
					start = i;
				}
				if (details.charAt(i) == ']') {
					end = i;
					break;
				}
			}
			//System.out.println("[ esta em " + start);
			//System.out.println("] esta em " + end);
			embed.addField("Beatmap: ", details.substring(0, start), true);
			
			embed.addField("Dificuldade", details.substring(start+1, end), true);
		}

		embed.setColor(Color.MAGENTA);
		embed.setFooter(lang.translatedBot("FOOTER_DEFAULT"), user.getAvatarUrl());
		return embed;
	}

}

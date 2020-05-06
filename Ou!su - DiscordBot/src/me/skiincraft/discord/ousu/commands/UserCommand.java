package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import me.skiincraft.api.ousu.exceptions.InvalidUserException;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.customemoji.OsuEmoji;
import me.skiincraft.discord.ousu.imagebuilders.OsuProfileNote;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import me.skiincraft.discord.ousu.utils.DefaultEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class UserCommand extends Commands {

	public UserCommand() {
		super("ou!", "user", "ou!user <nickname> <gamemode>", Arrays.asList("osuplayer"));
	}

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("OSU_HELPMESSAGE_USER");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Osu;
	}

	@Override
	public void action(String[] args, User user, TextChannel channel) {
		if (args.length == 1) {
			sendUsage().queue();
			return;
		}

		if (args.length == 2) {

			me.skiincraft.api.ousu.users.User osuUser;
			try {
				osuUser = OusuBot.getOsu().getUser(args[1], Gamemode.Standard);
			} catch (InvalidUserException e) {
				String[] str = getLang().translatedArrayOsuMessages("INEXISTENT_USER");
				StringBuffer buffer = new StringBuffer();
				for (String append : str) {
					if (append != str[0]) {
						buffer.append(append);
					}
				}

				sendEmbedMessage(new DefaultEmbed(str[0], buffer.toString())).queue();
				return;
			}
			InputStream drawer = OsuProfileNote.drawImage(osuUser);
			String aname = osuUser.getUserID() + "userOsu.png";
			channel.sendFile(drawer, aname)
					.embed(embed(osuUser, getEvent().getGuild()).setImage("attachment://" + aname).build()).queue();
			return;
		}

		if (args.length >= 3) {
			Gamemode gm = getGamemode(args[2]);
			if (gm == null) {
				gm = Gamemode.Standard;
			}

			me.skiincraft.api.ousu.users.User osuUser;
			try {
				osuUser = OusuBot.getOsu().getUser(args[1], gm);
			} catch (InvalidUserException e) {
				String[] str = getLang().translatedArrayOsuMessages("INEXISTENT_USER");
				StringBuffer buffer = new StringBuffer();
				for (String append : str) {
					if (append != str[0]) {
						buffer.append(append);
					}
				}

				sendEmbedMessage(new DefaultEmbed(str[0], buffer.toString())).queue();
				return;
			}

			InputStream drawer = OsuProfileNote.drawImage(osuUser);
			String aname = osuUser.getUserID() + "userOsu.png";
			channel.sendFile(drawer, aname)
					.embed(embed(osuUser, getEvent().getGuild()).setImage("attachment://" + aname).build()).queue();
			return;
		}
	}

	public EmbedBuilder embed(me.skiincraft.api.ousu.users.User osuUser, Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();
		SQLAccess sql = new SQLAccess(guild);
		LanguageManager lang = new LanguageManager(Language.valueOf(sql.get("language")));
		NumberFormat f = NumberFormat.getNumberInstance();
		String accuracy = new DecimalFormat("#.0").format(osuUser.getAccuracy());
		String PP = OsuEmoji.PP.getEmojiString();
		
		embed.setThumbnail(osuUser.getUserAvatar());

		embed.setAuthor(osuUser.getUserName(), osuUser.getURL(), osuUser.getUserAvatar());
		embed.setTitle(lang.translatedEmbeds("TITLE_USER_COMMAND_PLAYERSTATS"));
		embed.setDescription(
				lang.translatedEmbeds("MESSAGE_USER").replace("{USERNAME}", "[" + osuUser.getUserName() + "](" + osuUser.getURL() + ")"));
		embed.addField(lang.translatedEmbeds("RANKING"), "#" + f.format(osuUser.getRanking()), true);
		embed.addField(lang.translatedEmbeds("NATIONAL_RANKING"),
				osuUser.getCountryCode() + " #" + f.format(osuUser.getNacionalRanking()), true);
		embed.addField(lang.translatedEmbeds("PLAYED_TIME"), osuUser.getPlayedHours().toString(), true);
		embed.addField(lang.translatedEmbeds("PERFORMANCE"), lang.translatedEmbeds("ACCURACY") + "`" + (accuracy += "%")
				+ "`" + "\n" + PP + " " + f.format(osuUser.getPP()), true);
		embed.addField(lang.translatedEmbeds("TOTAL_SCORE"), f.format(osuUser.getTotalScore()) + "", true);

		embed.setFooter(lang.translatedBot("FOOTER_DEFAULT"),
				"https://osu.ppy.sh/images/flags/" + osuUser.getCountryCode() + ".png");
		embed.setColor(Color.gray);
		return embed;
	}

	public Gamemode getGamemode(String gamemode) {
		String gm = gamemode.toLowerCase();
		Map<String, Gamemode> map = new HashMap<>();

		map.put("standard", Gamemode.Standard);
		map.put("catch", Gamemode.Catch_the_Beat);
		map.put("mania", Gamemode.Mania);
		map.put("taiko", Gamemode.Taiko);

		if (map.containsKey(gm)) {
			return map.get(gamemode);
		}

		return null;
	}

}

package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

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
		super("ou!", "user", "ou!user <nickname> <gamemode>", Arrays.asList("player", "profile", "usuario"));
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
	public void action(String[] args, String label, User user, TextChannel channel) {
		if (args.length == 0) {
			sendUsage().queue();
			return;
		}

		if (args.length >= 1) {

			me.skiincraft.api.ousu.users.User osuUser;
			try {
				StringBuffer stringArgs = new StringBuffer();
				for (int i = 0; i < args.length; i++) {
					stringArgs.append(args[i] + " ");
				}
				
				int length = stringArgs.toString().length() - 1;
				
				String usermsg = stringArgs.toString().substring(0, length);
				String lastmsg = args[args.length-1];
				String name = usermsg.replace(" " + lastmsg, "");
				
				if (Gamemode.getGamemode(lastmsg) != null) {
					osuUser = OusuBot.getOsu().getUser(name, Gamemode.getGamemode(lastmsg));
				} else {
					osuUser = OusuBot.getOsu().getUser(usermsg);
				}
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
			InputStream drawer = OsuProfileNote.drawImage(osuUser, getLanguage());
			String aname = osuUser.getUserID() + "userOsu.png";
			channel.sendFile(drawer, aname)
					.embed(embed(osuUser, getEvent().getGuild()).setImage("attachment://" + aname).build()).queue();
			return;
		}
	}

	public static EmbedBuilder embed(me.skiincraft.api.ousu.users.User osuUser, Guild guild) {
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
}

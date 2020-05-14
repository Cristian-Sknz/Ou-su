package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.api.ousu.exceptions.InvalidUserException;
import me.skiincraft.api.ousu.exceptions.NoHistoryException;
import me.skiincraft.api.ousu.modifiers.Approvated;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.api.ousu.scores.Score;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.customemoji.OsuEmoji;
import me.skiincraft.discord.ousu.embedtypes.DefaultEmbed;
import me.skiincraft.discord.ousu.events.TopUserReaction;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import me.skiincraft.discord.ousu.mysql.SQLPlayer;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class TopUserCommand extends Commands {

	public TopUserCommand() {
		super("ou!", "top", "ou!top <nickname> <gamemode>", Arrays.asList("topmaps"));
	}

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("OSU_HELPMESSAGE_TOPUSER");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Osu;
	}

	public static String linkcover;

	@Override
	public void action(String[] args, String label, User user, TextChannel channel) {
		if (args.length == 0) {
			sendUsage().queue();
			return;
		}

		if (args.length >= 1) {

			List<Score> osuUser;
			try {

				StringBuffer stringArgs = new StringBuffer();
				for (int i = 0; i < args.length; i++) {
					stringArgs.append(args[i] + " ");
				}
				
				int length = stringArgs.toString().length() - 1;
				
				String usermsg = stringArgs.toString().substring(0, length);
				String lastmsg = args[args.length-1];
				String name = usermsg.replace(" " + lastmsg, "");
				
				if (getEvent().getMessage().getMentionedUsers().size() != 0) {
					String userid = getEvent().getMessage().getMentionedUsers().get(0)
							.getAsMention().replaceAll("\\D+","");
					
					SQLPlayer sql = new SQLPlayer(OusuBot.getJda().getUserById(userid));
					if (sql.existe()) {
						String nic = sql.get("osu_account");
						name = nic;
						usermsg = nic;
					}
				}
				
				if (Gamemode.getGamemode(lastmsg) != null) {
					osuUser = OusuBot.getOsu().getTopUser(name, Gamemode.getGamemode(lastmsg), 10);
				} else {
					osuUser = OusuBot.getOsu().getTopUser(usermsg, 10);
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
			} catch (NoHistoryException e) {
				String[] str = getLang().translatedArrayOsuMessages("NO_HAS_HISTORY");
				StringBuffer buffer = new StringBuffer();
				for (String append : str) {
					if (append != str[0]) {
						buffer.append(append);
					}
				}
				sendEmbedMessage(new DefaultEmbed(str[0], buffer.toString())).queue();
				return;
			}

			// Transform list to array
			System.out.println(osuUser.size());

			Score[] scorearray = new Score[osuUser.size()];
			osuUser.toArray(scorearray);

			sendEmbedMessage(embed(osuUser, 0, channel.getGuild())).queue(message -> {
				message.addReaction("U+25C0").queue();
				message.addReaction("U+25FC").queue();
				message.addReaction("U+25B6").queue();
				ReactionMessage.osuHistory.add(new TopUserReaction(user, message.getId(), scorearray, 0));
			});
			return;
		}
	}

	public static EmbedBuilder embed(List<Score> scorelist, int order, Guild guild) {
		// "Imports"
		EmbedBuilder embed = new EmbedBuilder();
		Score score = scorelist.get(order);
		SQLAccess sql = new SQLAccess(guild);
		LanguageManager lang = new LanguageManager(Language.valueOf(sql.get("language")));
		Beatmap beatmap = score.getBeatmap();
		me.skiincraft.api.ousu.users.User user = score.getUser();

		// Strings
		String inicial = getRankEmote(score);
		String ordem = "[" + (order + 1) + "/" + scorelist.size() + "]";
		String u = "[" + user.getUserName() + "](" + user.getURL() + ")";
		String title = "[" + beatmap.getTitle() + "](" + beatmap.getURL() + ") por `" + beatmap.getArtist() + "`";

		// String notes
		String h300 = OsuEmoji.Hit300.getEmojiString() + ": " + score.get300();
		String h100 = OsuEmoji.Hit100.getEmojiString() + ": " + score.get100();
		String h50 = OsuEmoji.Hit50.getEmojiString() + ": " + score.get50();
		String miss = OsuEmoji.Miss.getEmojiString() + ": " + score.getMiss();
		String pp = OsuEmoji.PP.getEmojiString() + ": ";
		String l = "\n";
		String field = h300 + l + h100 + l + h50 + l + miss + l;
		int id = score.getBeatmap().getBeatmapSetID();
		String url = "https://assets.ppy.sh/beatmaps/" + id + "/covers/cover.jpg?";

		// Embed
		embed.setAuthor(user.getUserName());
		embed.setTitle(inicial + " " + lang.translatedEmbeds("TITLE_USER_COMMAND_HISTORY") + " | " + ordem);
		embed.setDescription(lang.translatedEmbeds("MESSAGE_TOPUSER").replace("{USERNAME}", u));

		embed.addField("Beatmap:", title, true);
		embed.addField(lang.translatedEmbeds("MAP_STATS"),
				"`" + getApproval(beatmap.getApprovated()) + "`\n" + beatmap.getVersion(), true);

		embed.addField(lang.translatedEmbeds("SCORE"), field, true);
		embed.addField(lang.translatedEmbeds("TOTAL_SCORE"), score.getScore() + "", true);
		embed.addField(lang.translatedEmbeds("MAX_COMBO"), score.getMaxCombo() + "/" + score.getBeatmap().getMaxCombo(),
				true);

		embed.addField("PP", pp + new DecimalFormat("#").format(score.getScorePP()) + "", true);

		linkcover = url;
		embed.setThumbnail(user.getUserAvatar());
		embed.setImage(url);

		String author = beatmap.getCreator();
		embed.setFooter("[" + beatmap.getBeatmapID() + "] " + beatmap.getTitle() + " por " + beatmap.getArtist() + " | "
				+ lang.translatedEmbeds("MAP_CREATED_BY") + author);
		embed.setColor(Color.gray);
		return embed;
	}

	public static String getApproval(Approvated approval) {
		Map<Approvated, String> map = new HashMap<>();

		map.put(Approvated.Ranked, "Ranqueado");
		map.put(Approvated.Qualified, "Qualificado");
		map.put(Approvated.Pending, "Pendente");
		map.put(Approvated.Approved, "Aprovado");
		map.put(Approvated.Loved, "Loved");
		map.put(Approvated.Graveyard, "Cemiterio");
		map.put(Approvated.WIP, "WorkInProgress");

		if (map.containsKey(approval)) {
			return map.get(approval);
		}

		return "Não classificado";
	}

	public static String getRankEmote(Score score) {
		String rank = score.getRank();
		if (rank.equalsIgnoreCase("SSH")) {
			return OsuEmoji.SSPlus.getEmojiString();
		}
		if (rank.equalsIgnoreCase("SS")) {
			return OsuEmoji.SS.getEmojiString();
		}
		if (rank.equalsIgnoreCase("SH")) {
			return OsuEmoji.SPlus.getEmojiString();
		}
		if (rank.equalsIgnoreCase("S")) {
			return OsuEmoji.S.getEmojiString();
		}
		if (rank.equalsIgnoreCase("A")) {
			return OsuEmoji.A.getEmojiString();
		}
		if (rank.equalsIgnoreCase("B")) {
			return OsuEmoji.B.getEmojiString();
		}
		if (rank.equalsIgnoreCase("C")) {
			return OsuEmoji.C.getEmojiString();
		}
		if (rank.equalsIgnoreCase("F")) {
			return OsuEmoji.F.getEmojiString();
		}
		return OsuEmoji.OsuLogo.getEmojiString();
	}

}

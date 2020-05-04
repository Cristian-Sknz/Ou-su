package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
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
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.utils.DefaultEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class RecentUserCommand extends Commands {

	public RecentUserCommand() {
		super("ou!", "recent", "ou!recent <nickname> <gamemode>", Arrays.asList("recents"));
	}

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("OSU_HELPMESSAGE_RECENTUSER");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Osu;
	}

	public static String linkcover;

	@Override
	public void action(String[] args, User user, TextChannel channel) {
		if (args.length == 1) {
			sendUsage().queue();
			return;
		}

		if (args.length >= 2) {

			List<Score> osuUser;
			try {

				StringBuffer stringArgs = new StringBuffer();
				for (int i = 1; i < args.length; i++) {
					stringArgs.append(args[i] + " ");
				}
				int length = stringArgs.toString().length() - 1;
				osuUser = OusuBot.getOsu().getRecentUser(stringArgs.toString().substring(0, length), 5);
				@SuppressWarnings("unused")
				Score score = osuUser.get(0);

			} catch (InvalidUserException e) {
				sendEmbedMessage(new DefaultEmbed("Usuario inexistente", "Este usuario que você solicitou não existe."))
						.queue();
				return;
			} catch (NoHistoryException | NullPointerException e) {
				sendEmbedMessage(
						new DefaultEmbed("Não há historico", "Este usuario que você solicitou não tem historico"))
								.queue();
				return;
			}

			sendEmbedMessage(embed(osuUser, 0)).queue(message -> {
				message.addReaction("U+25C0").queue();
				message.addReaction("U+25FC").queue();
				message.addReaction("U+25B6").queue();
				// ReactionMessage.osuHistory.add(new ReactionUtils(user, message.getId(),
				// osuUser.getUsername(),0));
			});
			return;
		}
	}

	public static EmbedBuilder embed(List<Score> scorelist, int order) {
		EmbedBuilder embed = new EmbedBuilder();
		Score score = scorelist.get(order);
		String inicial = getRankEmote(score);

		embed.setColor(Color.gray);
		embed.setTitle(inicial + " " + score.getUsername() + " | Histórico do Jogador");

		StringBuilder str = new StringBuilder();

		me.skiincraft.api.ousu.users.User user = score.getUser();

		str.append("Você esta visualizando os beatmaps jogados nas ultimas 24h ");
		str.append("de [" + score.getUsername() + "]");
		str.append("(" + user.getURL() + ")");

		embed.setDescription(str.toString());

		Beatmap beatmap = score.getBeatmap();

		String title = "";
		StringBuilder beatmaptitle = new StringBuilder();

		beatmaptitle.append("[" + beatmap.getTitle() + "]");
		beatmaptitle.append("(" + beatmap.getURL() + ")");
		beatmaptitle.append("por `" + beatmap.getArtist() + "`");

		title = beatmaptitle.toString();

		embed.addField("Beatmap:", title, true);
		embed.addField("Status:", "`" + getApproval(beatmap.getApprovated()) + "`\n" + beatmap.getVersion(), true);

		String h300 = OsuEmoji.Hit300.getEmojiString() + ": " + score.get300();
		String h100 = OsuEmoji.Hit100.getEmojiString() + ": " + score.get100();
		String h50 = OsuEmoji.Hit50.getEmojiString() + ": " + score.get50();
		String miss = OsuEmoji.Miss.getEmojiString() + ": " + score.getMiss();
		//String pp = OsuEmoji.PP.getEmojiString() + ": ";
		String l = "\n";
		String field = h300 + l + h100 + l + h50 + l + miss + l;

		embed.addField("Pontuação", field, true);
		embed.addField("Pontuação total:", score.getScore() + "", true);
		embed.addField("Combo Maximo:", score.getMaxCombo() + "/" + score.getBeatmap().getMaxCombo(), true);

		//embed.addField("PP", pp + score.getScorePP() + "", true);

		int id = score.getBeatmap().getBeatmapSetID();
		String url = "https://assets.ppy.sh/beatmaps/" + id + "/covers/cover.jpg?";

		linkcover = url;
		embed.setThumbnail(user.getUserAvatar());
		embed.setImage(url);

		String author = beatmap.getCreator();
		embed.setFooter("[" + beatmap.getBeatmapID() + "] " + beatmap.getTitle() + " por " + beatmap.getArtist()
				+ " | Mapa criado por " + author);

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

	public static String getRankEmote(Score osuUser) {
		String rank = osuUser.getRank();
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

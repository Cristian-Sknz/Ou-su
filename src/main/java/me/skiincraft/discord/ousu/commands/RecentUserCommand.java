package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.api.ousu.exceptions.InvalidUserException;
import me.skiincraft.api.ousu.exceptions.NoHistoryException;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.api.ousu.modifiers.Mods;
import me.skiincraft.api.ousu.scores.Score;
import me.skiincraft.api.ousu.users.User;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.customemoji.OusuEmojis;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.events.DefaultReaction;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import me.skiincraft.discord.ousu.utils.Emoji;
import me.skiincraft.discord.ousu.utils.ImageUtils;
import me.skiincraft.discord.ousu.utils.OusuUtils;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

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
	public void action(String[] args, String label, TextChannel channel) {
		if (args.length == 0) {
			sendUsage().queue();
			return;
		}

		if (args.length >= 1) {
			try {
				String stringArgs = StringUtils.arrayToString2(0, args);

				String usermsg = stringArgs.substring(0, stringArgs.length() - 1);
				String lastmsg = args[args.length - 1];
				String name = usermsg.replace(" " + lastmsg, "");
				
				List<Score> osuUser = ((Gamemode.getGamemode(lastmsg) != null) 
						? OusuBot.getOsu().getRecentUser(name, Gamemode.getGamemode(lastmsg), 5)
						: OusuBot.getOsu().getRecentUser(usermsg, 5));

				@SuppressWarnings("unused")
				Score score = osuUser.get(0);
				
				sendEmbedMessage(TypeEmbed.LoadingEmbed()).queue(message -> {
					me.skiincraft.api.ousu.users.User us = osuUser.get(0).getUser();
					List<EmbedBuilder> emb = new ArrayList<EmbedBuilder>();

					int v = 1;
					for (Score s : osuUser) {
						emb.add(embed(s, new Integer[] { v, osuUser.size() }, us, channel.getGuild()));
						v++;
					}

					EmbedBuilder[] sc = new EmbedBuilder[emb.size()];
					emb.toArray(sc);
					message.editMessage(sc[0].build()).queue(message2 -> {
						message.addReaction("U+25C0").queue();
						message.addReaction("U+25B6").queue();
						ReactionMessage.recentHistory.add(new DefaultReaction(getUserId(), message.getId(), sc, 0));
					});
				});

			} catch (InvalidUserException e) {
				String[] str = getLang().translatedArrayOsuMessages("INEXISTENT_USER");
				sendEmbedMessage(TypeEmbed.WarningEmbed(str[0], StringUtils.commandMessage(str))).queue();
				return;
			} catch (NoHistoryException | NullPointerException e) {
				String[] str = getLang().translatedArrayOsuMessages("NO_HAS_HISTORY");
				sendEmbedMessage(TypeEmbed.SoftWarningEmbed(str[0], StringUtils.commandMessage(str))).queue();
				return;
			}
		}
	}

	public static EmbedBuilder embed(Score scorelist, Integer[] order,User user, Guild guild) {
		// "Imports"
		EmbedBuilder embed = new EmbedBuilder();
		Score score = scorelist;
		SQLAccess sql = new SQLAccess(guild);
		LanguageManager lang = new LanguageManager(Language.valueOf(sql.get("language")));
		Beatmap beatmap = score.getBeatmap();

		// Strings
		String inicial = OusuUtils.getRankEmote(score);
		String ordem = "[" + order[0].intValue() + "/" + order[1].intValue() + "]";
		String u = "[" + user.getUserName() + "](" + user.getURL() + ")";
		String title = "[" + beatmap.getTitle() + "](" + beatmap.getURL() + ") por `" + beatmap.getArtist() + "`";

		// String notes
		StringBuffer scores = new StringBuffer();
		
		scores.append(OusuEmojis.getEmoteAsMention("300") + ": " + score.get300() + "\n");
		scores.append(OusuEmojis.getEmoteAsMention("100") + ": " + score.get100() + "\n");
		scores.append(OusuEmojis.getEmoteAsMention("50") + ": " + score.get50() + "\n");
		scores.append(OusuEmojis.getEmoteAsMention("miss") + ": " + score.getMiss() + "\n");

		int id = beatmap.getBeatmapSetID();
		String url = "https://assets.ppy.sh/beatmaps/" + id + "/covers/cover.jpg?";

		String mods = "";
		for (Mods mod : score.getEnabledMods()) {
			for (Emote emoji : OusuEmojis.getEmotes()) {
				if (mod.name().toLowerCase().replace("_", "").contains(emoji.getName().toLowerCase())) {
					mods += emoji.getAsMention() + " ";
				}
			}
		}

		// Embed
		embed.setAuthor(user.getUserName());
		embed.setTitle(inicial + " " + lang.translatedEmbeds("TITLE_USER_COMMAND_HISTORY") + " | " + ordem);
		embed.setDescription(OusuEmojis.getEmoteAsMention("small_green_diamond") + " "
				+ lang.translatedEmbeds("MESSAGE_RECENTUSER").replace("{USERNAME}", u));

		embed.addField("Beatmap:", Emoji.HEADPHONES.getAsMention() + title, true);
		embed.addField(lang.translatedEmbeds("MAP_STATS"),
				"`" + OusuUtils.getApproval(beatmap.getApprovated()) + "`\n" + beatmap.getVersion() + "\n" + mods, true);

		embed.addField(lang.translatedEmbeds("SCORE"), scores.toString(), true);
		embed.addField(lang.translatedEmbeds("TOTAL_SCORE"), score.getScore() + "", true);
		embed.addField(lang.translatedEmbeds("MAX_COMBO"), score.getMaxCombo() + "/" + score.getBeatmap().getMaxCombo(),
				true);

		linkcover = url;
		embed.setThumbnail(user.getUserAvatar());
		embed.setImage((ImageUtils.existsImage(url))
				? url
				: "https://i.imgur.com/LfF0VBR.gif");

		String author = beatmap.getCreator();
		embed.setFooter("[" + beatmap.getBeatmapID() + "] " + beatmap.getTitle() + " por " + beatmap.getArtist() + " | "
				+ lang.translatedEmbeds("MAP_CREATED_BY") + author);
		try {
			embed.setColor(ImageUtils.getPredominatColor(ImageIO.read(new URL(beatmap.getBeatmapThumbnailUrl()))));
		} catch (NullPointerException | IOException e) {
			embed.setColor(Color.BLUE);
		}
		return embed;
	}

}

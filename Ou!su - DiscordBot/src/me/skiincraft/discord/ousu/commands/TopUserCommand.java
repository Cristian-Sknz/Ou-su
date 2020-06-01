package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
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
						? OusuBot.getOsu().getTopUser(name, Gamemode.getGamemode(lastmsg), 10) : OusuBot.getOsu().getTopUser(usermsg, 10));
		
				sendEmbedMessage(TypeEmbed.LoadingEmbed()).queue(loadmessage -> {
					List<EmbedBuilder> emb = new ArrayList<EmbedBuilder>();
					User us = osuUser.get(0).getUser();

					for (int i = 0; i < osuUser.size(); i++) {
						emb.add(embed(osuUser.get(i), new Integer[] { i + 1, osuUser.size() }, us, channel.getGuild()));
					}

					EmbedBuilder[] scorearray = new EmbedBuilder[emb.size()];
					emb.toArray(scorearray);

					loadmessage.editMessage(scorearray[0].build()).queue(sucessfullmessage -> {
						sucessfullmessage.addReaction("U+25C0").queue();
						sucessfullmessage.addReaction("U+25B6").queue();
						ReactionMessage.osuHistory
								.add(new DefaultReaction(getUserId(), loadmessage.getId(), scorearray, 0));
					});
				});
				
			} catch (InvalidUserException e) {
				String[] str = getLang().translatedArrayOsuMessages("INEXISTENT_USER");
				sendEmbedMessage(TypeEmbed.WarningEmbed(str[0], StringUtils.commandMessageEmoji(str, 
						OusuEmojis.getEmoteAsMention("small_red_diamond")))).queue();
				return;
			} catch (NoHistoryException e) {
				String[] str = getLang().translatedArrayOsuMessages("NO_HAS_HISTORY");
				sendEmbedMessage(TypeEmbed.WarningEmbed(str[0], StringUtils.commandMessageEmoji(str, 
						Emoji.SMALL_ORANGE_DIAMOND.getAsMention()))).queue();
				return;
			}
		}
	}

	public static EmbedBuilder embed(Score scorelist, Integer[] order, User user, Guild guild) {
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
		embed.setDescription(OusuEmojis.getEmote("small_green_diamond").getAsMention() + " "
				+ lang.translatedEmbeds("MESSAGE_TOPUSER").replace("{USERNAME}", u));

		embed.addField("Beatmap:", Emoji.HEADPHONES.getAsMention() + title, true);
		embed.addField(lang.translatedEmbeds("MAP_STATS"),
				"`" + OusuUtils.getApproval(beatmap.getApprovated()) + "`\n" + beatmap.getVersion() + "\n" + mods, true);

		embed.addField(lang.translatedEmbeds("SCORE"), scores.toString(), true);
		embed.addField(lang.translatedEmbeds("TOTAL_SCORE"), score.getScore() + "", true);
		embed.addField(lang.translatedEmbeds("MAX_COMBO"), score.getMaxCombo() + "/" + beatmap.getMaxCombo(), true);

		embed.addField("PP", OusuEmojis.getEmoteAsMentionEquals("pp") + " :" + new DecimalFormat("#").format(score.getScorePP()), true);

		linkcover = url;
		embed.setThumbnail(user.getUserAvatar());
		embed.setImage(url);

		String author = beatmap.getCreator();
		embed.setFooter("[" + beatmap.getBeatmapID() + "] " + beatmap.getTitle() + " por " + beatmap.getArtist() + " | "
				+ lang.translatedEmbeds("MAP_CREATED_BY") + author);

		try {
			embed.setColor(ImageUtils.getPredominatColor(ImageIO.read(new URL(beatmap.getBeatmapThumbnailUrl()))));
		} catch (NullPointerException | IOException e) {
			embed.setColor(new Color(252, 171, 151));
		}
		return embed;
	}
}

package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import me.skiincraft.api.ousu.Request;
import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.api.ousu.entity.objects.Mods;
import me.skiincraft.api.ousu.entity.score.Score;
import me.skiincraft.api.ousu.exceptions.ScoreException;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.reactions.ReactionObject;
import me.skiincraft.discord.core.utils.Emoji;
import me.skiincraft.discord.core.utils.ImageUtils;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.emojis.OusuEmote;
import me.skiincraft.discord.ousu.htmlpage.BeatmapSearch;
import me.skiincraft.discord.ousu.htmlpage.JSoupGetters;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import me.skiincraft.discord.ousu.osu.WebUser;
import me.skiincraft.discord.ousu.reactions.HistoryLists;
import me.skiincraft.discord.ousu.utils.OusuUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class TopUserCommand extends Comando {

	public TopUserCommand() {
		super("top", Collections.singletonList("topuser"), "top <user>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Osu;
	}

	public void execute(User buser, String[] args, TextChannel channel) {
		if (args.length == 0) {
			replyUsage();
			return;
		}

		List<String> l = new ArrayList<>(Arrays.asList(args));
		Gamemode gm = Gamemode.Standard;

		StringBuffer b = new StringBuffer();
		if (args.length >= 2) {
			if (isGamemode(args[args.length-1])) {
				l.remove(args.length-1);
			}
		}

		l.forEach(s -> b.append(s).append(" "));
		l.clear();
		try {
			Request<List<Score>> request = OusuBot.getApi().getTopUser(b.substring(0, b.length()-1), gm, 10);
			List<Score> scores = request.get();
			List<EmbedBuilder> embeds = new ArrayList<>();

			String name = WebUser.getName(b.substring(0, b.length()-1));
			long id = WebUser.getId(b.substring(0, b.length()-1));

			reply(TypeEmbed.LoadingEmbed().build(), message -> {
				int i= 1;
				for (Score score : scores) {
					try {
						BeatmapSearch bs = JSoupGetters.beatmapInfoById(score.getBeatmapId());
						embeds.add(embeds(score, bs, name, id, new int[] {i, scores.size()}));
						i++;
					} catch (IOException e) {
						continue;
					}

					if (embeds.size() == 1) {
						message.editMessage(embeds.get(0).build()).queue();
					}
				}
				message.addReaction("U+25C0").queue();
				message.addReaction("U+25B6").queue();

				HistoryLists.addToReaction(buser, message, new ReactionObject(embeds, 0));
			});
		} catch (ScoreException e) {
			String[] str = getLanguageManager().getStrings("Osu", "NO_HAS_HISTORY");
			MessageEmbed embed = TypeEmbed.SoftWarningEmbed(str[0], StringUtils.commandMessage(str)).build();

			System.out.println(StringUtils.commandMessage(str));
			reply(embed);
		}
	}
	
	
	public EmbedBuilder embeds(Score score, BeatmapSearch beatmap, String username, long userId, int[] ordem) {
		EmbedBuilder embed = new EmbedBuilder();
		LanguageManager lang = getLanguageManager();
		
		String title = "[" + beatmap.getTitle() + "](" + beatmap.getURL() + ") por `" + beatmap.getAuthor() + "`";
		String u = "[" + username + "](" + "https://osu.ppy.sh/users/" + userId + ")";


		StringBuilder mods = new StringBuilder();
		for (Mods mod : score.getEnabledMods()) {
			for (Emote emoji : OusuEmote.getEmotes()) {
				if (mod.name().toLowerCase().replace("_", "").contains(emoji.getName().toLowerCase())) {
					mods.append(emoji.getAsMention()).append(" ");
				}
			}
		}
		
		// Embed
		
		embed.setAuthor(username);
		embed.setTitle(OusuUtils.getRankEmote(score) + " " + lang.getString("Embeds","USER_COMMAND_HISTORY") + " | " + ordem[0] + "/" + ordem[1]);
		embed.setDescription(OusuEmote.getEmoteAsMention("small_green_diamond") + " "
				+ lang.getString("Embeds", "MESSAGE_TOPUSER").replace("{USERNAME}", u));
		
		embed.addField("Beatmap:", Emoji.HEADPHONES.getAsMention() + title, true);
		embed.addField(lang.getString("Embeds", "MAP_STATS"),
				"`" + OusuUtils.getApproval(beatmap.getApprovated()) + "`\n" + beatmap.getDifficult()[0] + "\n" + mods, true);

		String scores = OusuEmote.getEmoteAsMention("300") + ": " + score.get300() + "\n" +
				OusuEmote.getEmoteAsMention("100") + ": " + score.get100() + "\n" +
				OusuEmote.getEmoteAsMention("50") + ": " + score.get50() + "\n" +
				OusuEmote.getEmoteAsMention("miss") + ": " + score.getMiss() + "\n";
		embed.addField(lang.getString("Embeds", "SCORE"), scores, true);
		embed.addField(lang.getString("Embeds", "TOTAL_SCORE"), score.getScore() + "", true);
		embed.addField(lang.getString("Embeds", "MAX_COMBO"), score.getMaxCombo() + "", true);

		embed.addField("PP", OusuEmote.getEmoteAsMentionEquals("pp") + " :" + new DecimalFormat("#").format(score.getScorePP()), true);

		embed.setThumbnail("http://s.ppy.sh/a/" + userId + ".png");
		embed.setImage(beatmap.getBeatmapCoverUrl());
		String author = beatmap.getCreator();
		embed.setFooter("[" + beatmap.getBeatmapid() + "] " + beatmap.getTitle() + " por " + beatmap.getAuthor() + " | "
				+ lang.getString("Embeds", "MAP_CREATED_BY") + author);

		try {
			embed.setColor(ImageUtils.getPredominatColor(ImageIO.read(new URL(beatmap.getBeatmapThumbnailUrl()))));
		} catch (NullPointerException | IOException e) {
			embed.setColor(new Color(252, 171, 151));
		}
		
		return embed;
	}
	
	public boolean isGamemode(String arg) {
		return Gamemode.getGamemode(arg.toLowerCase()) != null;
	}

}

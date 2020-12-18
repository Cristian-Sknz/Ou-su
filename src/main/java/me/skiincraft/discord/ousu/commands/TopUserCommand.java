package me.skiincraft.discord.ousu.commands;

import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.api.ousu.entity.objects.Mods;
import me.skiincraft.api.ousu.entity.score.Score;
import me.skiincraft.api.ousu.exceptions.ScoreException;
import me.skiincraft.api.ousu.requests.Request;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.common.reactions.ReactionObject;
import me.skiincraft.discord.core.common.reactions.Reactions;
import me.skiincraft.discord.core.common.reactions.custom.ReactionPage;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.OusuCommand;
import me.skiincraft.discord.ousu.osu.BeatmapSearch;
import me.skiincraft.discord.ousu.crawler.WebCrawler;
import me.skiincraft.discord.ousu.emojis.GenericEmote;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import me.skiincraft.discord.ousu.osu.WebUser;
import me.skiincraft.discord.ousu.utils.ImageUtils;
import me.skiincraft.discord.ousu.utils.OusuUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

public class TopUserCommand extends OusuCommand {

	public TopUserCommand() {
		super("top", Collections.singletonList("topuser"), "top <username>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Statistics;
	}

	public void execute(Member member, String[] args, InteractChannel channel) {
		if (args.length == 0) {
			replyUsage(channel.getTextChannel());
			return;
		}

		List<String> l = new ArrayList<>(Arrays.asList(args));
		LanguageManager lang = getLanguageManager(channel.getTextChannel().getGuild());
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
			Request<List<Score>> request = OusuBot.getAPI().getTopUser(b.substring(0, b.length() - 1), gm, 10);
			List<Score> scores = request.get();
			List<EmbedBuilder> embeds = new ArrayList<>();

			String name = WebUser.getName(b.substring(0, b.length() - 1));
			long id = WebUser.getId(b.substring(0, b.length() - 1));

			channel.reply(TypeEmbed.LoadingEmbed().build(), message -> {
				int i = 1;
				for (Score score : scores) {
					BeatmapSearch bs = WebCrawler.getBeatmapInfo(score.getBeatmapId());
					if (bs == null) continue;
					embeds.add(embeds(lang, score, bs, name, id, new int[]{i, scores.size()}));
					i++;

					if (embeds.size() == 1) {
						message.editMessage(embeds.get(0).build()).queue();
					}
				}

				Objects.requireNonNull(Reactions.getInstance()).registerReaction(new ReactionObject(message, member.getIdLong(),
						new String[]{"U+25C0", "U+25B6"}), new ReactionPage(embeds, true));
			});
		} catch (ScoreException e) {
			String[] str = lang.getStrings("Osu", "NO_HAS_HISTORY");
			MessageEmbed embed = TypeEmbed.SoftWarningEmbed(str[0], StringUtils.commandMessage(str)).build();

			System.out.println(StringUtils.commandMessage(str));
			channel.reply(embed);
		} catch (Exception e) {
			channel.reply(TypeEmbed.errorMessage(e, channel.getTextChannel()).build());
		}
	}
	
	
	public EmbedBuilder embeds(LanguageManager lang, Score score, BeatmapSearch beatmap, String username, long userId, int[] ordem) {
		EmbedBuilder embed = new EmbedBuilder();
		
		String title = "[" + beatmap.getTitle() + "](" + beatmap.getURL() + ") por `" + beatmap.getAuthor() + "`";
		String u = "[" + username + "](" + "https://osu.ppy.sh/users/" + userId + ")";


		StringBuilder mods = new StringBuilder();
		for (Mods mod : score.getEnabledMods()) {
			for (GenericEmote emoji : GenericsEmotes.getEmotes()) {
				if (mod.name().toLowerCase().replace("_", "").contains(emoji.getName().toLowerCase())) {
					mods.append(emoji.getAsMention()).append(" ");
				}
			}
		}
		
		// Embed
		
		embed.setAuthor(username);
		embed.setTitle(OusuUtils.getRankEmote(score) + " " + lang.getString("Embeds","USER_COMMAND_HISTORY") + " | " + ordem[0] + "/" + ordem[1]);
		embed.setDescription(GenericsEmotes.getEmoteAsMention("small_green_diamond") + " "
				+ lang.getString("Embeds", "MESSAGE_TOPUSER").replace("{USERNAME}", u));
		
		embed.addField("Beatmap:", ":headphones: " + title, true);
		embed.addField(lang.getString("Embeds", "MAP_STATS"),
				"`" + OusuUtils.getApproval(beatmap.getApprovated()) + "`\n" + beatmap.getDifficult()[0] + "\n" + mods, true);

		String scores = GenericsEmotes.getEmoteAsMention("300") + ": " + score.get300() + "\n" +
				GenericsEmotes.getEmoteAsMention("100") + ": " + score.get100() + "\n" +
				GenericsEmotes.getEmoteAsMention("50") + ": " + score.get50() + "\n" +
				GenericsEmotes.getEmoteAsMention("miss") + ": " + score.getMiss() + "\n";
		embed.addField(lang.getString("Embeds", "SCORE"), scores, true);
		embed.addField(lang.getString("Embeds", "TOTAL_SCORE"), score.getScore() + "", true);
		embed.addField(lang.getString("Embeds", "MAX_COMBO"), score.getMaxCombo() + "", true);

		embed.addField("PP", GenericsEmotes.getEmoteAsMentionEquals("pp") + " :" + new DecimalFormat("#").format(score.getScorePP()), true);

		embed.setThumbnail("http://s.ppy.sh/a/" + userId + ".png");
		embed.setImage(beatmap.getBeatmapCoverUrl());
		String author = beatmap.getCreator();
		embed.setFooter("[" + beatmap.getBeatmapId() + "] " + beatmap.getTitle() + " por " + beatmap.getAuthor() + " | "
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

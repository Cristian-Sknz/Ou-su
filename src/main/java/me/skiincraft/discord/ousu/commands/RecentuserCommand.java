package me.skiincraft.discord.ousu.commands;

import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.api.ousu.entity.objects.Mods;
import me.skiincraft.api.ousu.entity.score.RecentScore;
import me.skiincraft.api.ousu.exceptions.ScoreException;
import me.skiincraft.api.ousu.requests.Request;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.common.reactions.ReactionObject;
import me.skiincraft.discord.core.common.reactions.Reactions;
import me.skiincraft.discord.core.common.reactions.custom.ReactionPage;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.crawler.WebCrawler;
import me.skiincraft.discord.ousu.emojis.GenericEmote;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import me.skiincraft.discord.ousu.osu.BeatmapSearch;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("SameParameterValue")
public class RecentuserCommand extends Comando {

	public RecentuserCommand() {
		super("recentuser", Arrays.asList("recent", "recente"), "recentuser <username>");
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
		Gamemode gm = Gamemode.Standard;

		StringBuffer b = new StringBuffer();
		if (args.length >= 2) {
			if (isGamemode(args[args.length-1])) {
				l.remove(args.length-1);
			}
		}

		l.forEach(s -> b.append(s).append(" "));
		l.clear();
		LanguageManager lang = getLanguageManager(channel.getTextChannel().getGuild());
		try {
			System.out.println(b.toString());
			Request<List<RecentScore>> request = OusuBot.getAPI().getRecentUser(b.substring(0, b.length()-1), gm, 10);
			List<RecentScore> score = request.get();
			channel.reply(TypeEmbed.LoadingEmbed().build(), message -> {
				List<EmbedBuilder> embeds = new ArrayList<>();
				for (RecentScore r: score) {
					BeatmapSearch be;
					be = WebCrawler.getBeatmapInfo(r.getBeatmapId());
					embeds.add(embed(lang, r, be, WebUser.getName(b.substring(0, b.length()-1))));
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
			channel.reply(embed);
		} catch (Exception e){
			channel.reply(TypeEmbed.errorMessage(e, channel.getTextChannel()).build());
		}
	}
	
	private String emote(String name, int append) {
		return GenericsEmotes.getEmoteAsMention(name) + " " + append + "\n";
	}
	
	private String emote(String name) {
		return GenericsEmotes.getEmoteAsMention(name) + " ";
	}
	
	public EmbedBuilder embed(LanguageManager lang, RecentScore score, BeatmapSearch beatmap, String username) {
		EmbedBuilder embed = new EmbedBuilder();
		String emote = OusuUtils.getRankEmote(score);

		StringBuilder mods = new StringBuilder();
		for (Mods mod : score.getEnabledMods()) {
			for (GenericEmote emoji : GenericsEmotes.getEmotes()) {
				if (mod.name().toLowerCase().replace("_", "").contains(emoji.getName().toLowerCase())) {
					mods.append(emoji.getAsMention()).append(" ");
				}
			}
		}
		
		embed.setAuthor(username);
		embed.setTitle(emote + " " + lang.getString("Embeds", "USER_COMMAND_HISTORY"));
		embed.setDescription(emote("small_green_diamond") + lang.getString("Embeds", "MESSAGE_RECENTUSER").replace("{USERNAME}", "[" + username + "]" + "(https://osu.ppy.sh/users/" + score.getUserId() + ")"));
		embed.addField("Beatmap:", ":headphones: "+ "[" + beatmap.getTitle() + "]" + "(" + beatmap.getURL() + ")", true);
		embed.addField(lang.getString("Embeds", "MAP_STATS"), "`" + OusuUtils.getApproval(beatmap.getApprovated()) + "`\n" + beatmap.getDifficult()[0] + "\n" + mods.toString(), true);
		String scores = emote("300", score.get300()) +
				emote("100", score.get100()) +
				emote("50", score.get50()) +
				emote("miss", score.getMiss());
		embed.addField(lang.getString("Embeds", "SCORE"), scores, true);
		
		embed.addField(lang.getString("Embeds", "TOTAL_SCORE"), score.getScore()+"", true);
		DateTimeFormatter datef = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		embed.addField(lang.getString("Embeds", "PLAYED_IN"), datef.format(score.getDate()), true);
		embed.addField(lang.getString("Embeds", "MAX_COMBO"), score.getMaxCombo() + "", true);
		
		embed.setImage(beatmap.getBeatmapCoverUrl());
		embed.setThumbnail(beatmap.getBeatmapThumbnailUrl());
		
		String author = beatmap.getCreator();
		embed.setFooter("[" + beatmap.getBeatmapId() + "] " + beatmap.getTitle() + " por " + beatmap.getAuthor() + " | "
				+ lang.getString("Embeds","MAP_CREATED_BY") + author);
		try {
			embed.setColor(ImageUtils.getPredominatColor(ImageIO.read(new URL(beatmap.getBeatmapThumbnailUrl()))));
		} catch (NullPointerException | IOException e) {
			embed.setColor(Color.BLUE);
		}
		
		return embed;
	}
	
	public boolean isGamemode(String arg) {
		return Gamemode.getGamemode(arg.toLowerCase()) != null;
	}

}

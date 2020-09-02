package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import me.skiincraft.api.ousu.Request;
import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.api.ousu.entity.objects.Mods;
import me.skiincraft.api.ousu.entity.score.RecentScore;
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

public class RecentuserCommand extends Comando {

	public RecentuserCommand() {
		super("recentuser", Arrays.asList("recent", "recente"), "recentuser <user>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Osu;
	}

	public void execute(User buser, String[] args, TextChannel channel) {
		if (args.length == 0) {
			replyUsage();
			return;
		}
		
		if (args.length >= 1) {
			List<String> l = new ArrayList<>(Arrays.asList(args));
			Gamemode gm = Gamemode.Standard;
			
			StringBuffer b = new StringBuffer();
			if (args.length >= 2) {
				if (isGamemode(args[args.length-1])) {
					l.remove(args.length-1);
				}
			}
			
			l.forEach(s -> b.append(s + " "));
			l.clear();
			try {
				System.out.println(b.toString());
				Request<List<RecentScore>> request = OusuBot.getApi().getRecentUser(b.substring(0, b.length()-1), gm, 10);
				List<RecentScore> score = request.get();
				reply(TypeEmbed.LoadingEmbed().build(), message -> {
					int i = 1;
					List<EmbedBuilder> embeds = new ArrayList<>();
					for (RecentScore r: score) {
						BeatmapSearch be;
						try {
							be = JSoupGetters.beatmapInfoById(r.getBeatmapId());
							embeds.add(embed(r, be, new int[] {i, score.size()}, WebUser.getName(b.substring(0, b.length()-1))));
							i++;
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (embeds.size() == 1) {
							message.editMessage(embeds.get(0).build()).queue();
						}
					}
					
					message.addReaction("U+25C0").queue();
					//message.addReaction("U+25FC").queue();
					message.addReaction("U+25B6").queue();
					
					EmbedBuilder[] emb = embeds.toArray(new EmbedBuilder[embeds.size()]);
					HistoryLists.addToReaction(buser, message, new ReactionObject(emb, 0));
				});
			} catch (ScoreException e) {
				String[] str = getLanguageManager().getStrings("Osu", "NO_HAS_HISTORY");
				MessageEmbed embed = TypeEmbed.SoftWarningEmbed(str[0], StringUtils.commandMessage(str)).build();
				
				System.out.println(StringUtils.commandMessage(str));
				reply(embed);
			}
		}
	}
	
	private String emote(String name, int append) {
		return OusuEmote.getEmoteAsMention(name) + " " + append + "\n";
	}
	
	private String emote(String name) {
		return OusuEmote.getEmoteAsMention(name) + " ";
	}
	
	public EmbedBuilder embed(RecentScore score, BeatmapSearch beatmap, int[] ordem, String username) {
		EmbedBuilder embed = new EmbedBuilder();
		String emote = OusuUtils.getRankEmote(score);
		String title = new StringBuffer()
				.append("[" + beatmap.getTitle() + "]")
				.append("(" + beatmap.getURL() + ")")
				.toString();
		
		String user = new StringBuffer()
				.append("[" + username + "]")
				.append("(https://osu.ppy.sh/users/" + score.getUserId() + ")")
				.toString();
		
		String scores = new StringBuffer()
				.append(emote("300", score.get300())) 
				.append(emote("100", score.get100()))
				.append(emote("50", score.get50()))
				.append(emote("miss", score.getMiss()))
				.toString();
		  
		StringBuffer mods = new StringBuffer();
		for (Mods mod : score.getEnabledMods()) {
			for (Emote emoji : OusuEmote.getEmotes()) {
				if (mod.name().toLowerCase().replace("_", "").contains(emoji.getName().toLowerCase())) {
					mods.append(emoji.getAsMention() + " ");
				}
			}
		}
		
		embed.setAuthor(username);
		embed.setTitle(emote + " " + getLanguageManager().getString("Embeds", "USER_COMMAND_HISTORY"));
		embed.setDescription(emote("small_green_diamond") + getLanguageManager().getString("Embeds", "MESSAGE_RECENTUSER").replace("{USERNAME}", user));
		LanguageManager lang = getLanguageManager();
		embed.addField("Beatmap:", Emoji.HEADPHONES.getAsMention() + title, true);
		embed.addField(lang.getString("Embeds", "MAP_STATS"), "`" + OusuUtils.getApproval(beatmap.getApprovated()) + "`\n" + beatmap.getDifficult()[0] + "\n" + mods.toString(), true);
		embed.addField(lang.getString("Embeds", "SCORE"), scores, true);
		
		embed.addField(lang.getString("Embeds", "TOTAL_SCORE"), score.getScore()+"", true);
		SimpleDateFormat datef = new SimpleDateFormat("dd/MM/yyyy");
		embed.addField(lang.getString("Embeds", "PLAYED_IN"), datef.format(score.getDate()), true);
		embed.addField(lang.getString("Embeds", "MAX_COMBO"), score.getMaxCombo() + "", true);
		
		embed.setImage(beatmap.getBeatmapCoverUrl());
		embed.setThumbnail(beatmap.getBeatmapThumbnailUrl());
		
		String author = beatmap.getCreator();
		embed.setFooter("[" + beatmap.getBeatmapid() + "] " + beatmap.getTitle() + " por " + beatmap.getAuthor() + " | "
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

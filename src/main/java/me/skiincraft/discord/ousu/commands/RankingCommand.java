package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.common.reactions.ReactionObject;
import me.skiincraft.discord.core.common.reactions.Reactions;
import me.skiincraft.discord.core.common.reactions.custom.ReactionPage;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.crawler.WebCrawler;
import me.skiincraft.discord.ousu.messages.Ranking;
import me.skiincraft.discord.ousu.messages.TypeEmbed;

import me.skiincraft.discord.ousu.utils.ImageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

public class RankingCommand extends Comando {

	public RankingCommand() {
		super("leaderboard", Collections.singletonList("ranking"), "leaderboard <country>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Gameplay;
	}

	public void execute(Member user, String[] args, InteractChannel channel) {
		if (args.length >= 2) {
			replyUsage(channel.getTextChannel());
			return;
		}
		try {
			channel.reply(TypeEmbed.LoadingEmbed().build(), message -> {
				String cc = (args.length == 0) ? null : (args[0].length() >= 2) ? args[0] : null;
				try {
					List<Ranking> rankinglist = WebCrawler.getRanking(cc);
					List<EmbedBuilder> embeds = embed(rankinglist, getLanguageManager(channel.getTextChannel().getGuild()));
					Objects.requireNonNull(Reactions.getInstance()).registerReaction(new ReactionObject(message, user.getIdLong(),
							new String[]{"U+25C0", "U+25B6"}), new ReactionPage(embeds, true));
					message.editMessage(embeds.get(0).build()).queue();
				} catch (IOException e) {
					channel.reply(TypeEmbed.errorMessage(e, channel.getTextChannel()).build());
				}
			});
		} catch (Exception e){
			channel.reply(TypeEmbed.errorMessage(e, channel.getTextChannel()).build());
		}
	}
	
	public List<EmbedBuilder> embed(List<Ranking> user, LanguageManager lang) {
		EmbedBuilder embed = new EmbedBuilder();
		
		// Verificando se é regional
		long countyEquals = user.stream().filter(u -> u.getCountry()[1].equalsIgnoreCase(user.get(0).getCountry()[1])).count();
		boolean isCountry = countyEquals == user.size();
		
		if (isCountry) {
			embed.setTitle(lang.getString("Embeds", "NATIONAL_RANKING"));
			embed.setDescription(lang.getString("Message", "NATIONAL_RANKING_MESSAGE")
					.replace("{country}", user.get(0).getCountry()[1]));
		}
		
		if (!isCountry) {
			embed.setTitle(lang.getString("Embeds", "WORLD_RANKING"));
			embed.setDescription(lang.getString("Message", "GLOBAL_RANKING_MESSAGE"));
		}
		//Pegando todos os valores
		embed.setThumbnail("https://i.imgur.com/sxIERAT.png");
		
		List<EmbedBuilder> embeds = new ArrayList<>();
		int cada = 0;
		for (int i = 1; i <= user.size()/10; i++) {
			EmbedBuilder temp = new EmbedBuilder(embed);
			List<Ranking> rank = new ArrayList<>();
			for (int n = cada; n < cada+10; n++) {
				rank.add(user.get(n));
			}
			temp.addField("Ranking", getRankingPlace(rank, cada + 1), true);
			temp.addField("Username", getUsernames(rank), true);
			temp.addField("PP", getPPs(rank), true);
			
			try {
				temp.setColor(ImageUtils.getPredominatColor(
						ImageIO.read(new URL("https://osu.ppy.sh/images/flags/" + rank.get(0).getCountry()[1] + ".png"))));
			} catch (IOException e) {
				temp.setColor(new Color(0, 180, 253));
			}
			
			cada+=10;
			embeds.add(temp);
		}
		
		return embeds;
	}
	
	private String getRankingPlace(List<Ranking> ranking, int start) {
		StringBuilder strings = new StringBuilder();
		int s = start;
		for (Ranking r: ranking) {
			strings.append("#").append(s).append(" :flag_").append(r.getCountry()[1].toLowerCase()).append(":\n");
			s++;
		}

		return strings.toString();
	}
	
	private String getUsernames(List<Ranking> ranking) {
		StringBuffer strings = new StringBuffer();
		
		Stream<Ranking> stream = ranking.stream();
		stream.forEach(v -> strings.append("[").append(v.getUsername()).append("](").append(v.getUrl()).append(")\n"));
		
		return strings.toString();
	}
	
	private String getPPs(List<Ranking> ranking) {
		StringBuffer strings = new StringBuffer();
		
		Stream<Ranking> stream = ranking.stream();
		stream.forEach(v -> strings.append(v.getPP()).append("\n"));
		
		return strings.toString();
	}

}

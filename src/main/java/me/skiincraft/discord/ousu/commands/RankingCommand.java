package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import me.skiincraft.discord.core.reactions.ReactionObject;
import me.skiincraft.discord.core.utils.ImageUtils;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.htmlpage.HtmlRanking;
import me.skiincraft.discord.ousu.messages.Ranking;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import me.skiincraft.discord.ousu.reactions.HistoryLists;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class RankingCommand extends Comando {

	public RankingCommand() {
		super("leaderboard", Collections.singletonList("ranking"), "leaderboard <country>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Osu;
	}

	public void execute(User user, String[] args, TextChannel channel) {
		if (args.length >= 2) {
			replyUsage();
			return;
		}
		reply(TypeEmbed.LoadingEmbed().build(), message -> {
				String cc = (args.length == 0)? null : (args[0].length() >= 2) ? args[0] : null;
				try {
					List<Ranking> rankinglist = HtmlRanking.get(cc);
					List<EmbedBuilder> embeds = embed(rankinglist);
					
					message.editMessage(embeds.get(0).build()).queue();
					
					message.addReaction("U+25C0").queue();
					message.addReaction("U+25B6").queue();
					
					HistoryLists.addToReaction(user, message, new ReactionObject(embeds, 0));
				} catch (IOException e) {
					e.printStackTrace();
					reply("Ocorreu um problema :/ \n`" + e.getMessage() + "`");
				}
		});
	}
	
	public List<EmbedBuilder> embed(List<Ranking> user) {
		EmbedBuilder embed = new EmbedBuilder();
		
		// Verificando se é regional
		long countyEquals = user.stream().filter(u -> u.getCountry()[1].equalsIgnoreCase(user.get(0).getCountry()[1])).count();
		boolean isCountry = countyEquals == user.size();
		
		if (isCountry) {
			embed.setTitle(getLanguageManager().getString("Embeds", "NATIONAL_RANKING"));
			embed.setDescription(getLanguageManager().getString("Message", "NATIONAL_RANKING_MESSAGE")
					.replace("{country}", user.get(0).getCountry()[1]));
		}
		
		if (!isCountry) {
			embed.setTitle(getLanguageManager().getString("Embeds", "WORLD_RANKING"));
			embed.setDescription(getLanguageManager().getString("Message", "GLOBAL_RANKING_MESSAGE"));
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

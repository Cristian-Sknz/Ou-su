package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.events.DefaultReaction;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.search.OsuRankingGetter;
import me.skiincraft.discord.ousu.search.RankingUsers;
import me.skiincraft.discord.ousu.utils.ImageUtils;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class RankingCommand extends Commands {

	public RankingCommand() {
		super("ou!", "ranking", "ou!ranking <countrycode>", null);
	}

	@Override
	public String[] helpMessage(LanguageManager langm) {
		return langm.translatedArrayHelp("OSU_HELPMESSAGE_RANKING");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Osu;
	}

	@Override
	public void action(String[] args, String label, TextChannel channel) {
		sendEmbedMessage(TypeEmbed.LoadingEmbed()).queue(new Consumer<Message>() {

			@Override
			public void accept(Message msg) {
				List<RankingUsers> ou;
				try {
					String cc = null;
					if (args.length != 0) {
						if (args[0].length() != 2) {
							sendUsage().queue();
							msg.delete().queue();
							return;
						}
						cc = args[0];
					}
					ou = OsuRankingGetter.rankingtop(cc);
					msg.editMessage(embed(ou, 0).build()).queue(new Consumer<Message>() {

						@Override
						public void accept(Message message) {
							message.addReaction("U+25C0").queue();
							message.addReaction("U+25B6").queue();
							EmbedBuilder[] embedb = new EmbedBuilder[] { embed(ou, 0), embed(ou, 10), embed(ou, 20),
									embed(ou, 30), embed(ou, 40) };

							ReactionMessage.rankingReaction.add(new DefaultReaction(getUserId(), message.getId(), embedb, 0));
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public EmbedBuilder embed(List<RankingUsers> user, int val) {
		EmbedBuilder embed = new EmbedBuilder();

		StringBuffer username = new StringBuffer();
		StringBuffer pps = new StringBuffer();
		StringBuffer flag = new StringBuffer();
		embed.setTitle(getLang().translatedEmbeds("WORLD_RANKING"));
		embed.setDescription(getLang().translatedMessages("GLOBAL_RANKING_MESSAGE"));
		int num = 0;
		int contador = 0;
		int isCountry = 0;

		// Verificando se o ranking é global ou regional
		for (RankingUsers ra : user) {
			if (ra.getCountry()[1].equals(user.get(0).getCountry()[1])) {
				isCountry++;
			}
		}

		if (isCountry >= 50) {
			embed.setTitle(getLang().translatedEmbeds("NATIONAL_RANKING"));
			embed.setDescription(getLang().translatedMessages("NATIONAL_RANKING_MESSAGE").replace("{country}",
					user.get(0).getCountry()[1]));
		}

		// Preparando Fields
		for (RankingUsers u : user) {
			if (num != val) {
				num++;
				continue;
			}
			if (contador == 10) {
				break;
			}
			String cc = u.getCountry()[1].toLowerCase().replace(" ", "");

			username.append("[" + u.getUsername() + "](" + u.getUrl() + ")\n");
			pps.append(u.getPP() + "\n");
			flag.append("#" + (val + contador + 1) + " :flag_" + cc + ":\n");

			contador++;
		}

		embed.addField("Ranking", flag.toString(), true);
		embed.addField("Username", username.toString(), true);
		embed.addField("PP", pps.toString(), true);

		embed.setThumbnail("https://i.imgur.com/sxIERAT.png");
		embed.setFooter("[" + (val + contador) + "/50]");
		try {
			embed.setColor(ImageUtils.getPredominatColor(
					ImageIO.read(new URL("https://osu.ppy.sh/images/flags/" + user.get(0).getCountry()[1] + ".png"))));
		} catch (IOException e) {
			embed.setColor(new Color(0, 180, 253));
			return embed;
		}

		return embed;
	}

}

package me.skiincraft.discord.ousu.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.skiincraft.api.ousu.exceptions.InvalidBeatmapException;
import me.skiincraft.discord.ousu.embeds.SearchEmbed;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.events.TopUserReaction;
import me.skiincraft.discord.ousu.exception.SearchNotFoundException;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.search.BeatmapSearch;
import me.skiincraft.discord.ousu.search.GoogleSearch;
import me.skiincraft.discord.ousu.search.JSoupGetters;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class SearchCommand extends Commands {

	public SearchCommand() {
		super("ou!", "search", "search <beatmap>", Arrays.asList("pesquisar"));
	}

	@Override
	public String[] helpMessage(LanguageManager langm) {
		return langm.translatedArrayHelp("OSU_HELPMESSAGE_SEARCH");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Osu;
	}

	@Override
	public void action(String[] args, String label, User user, TextChannel channel) {
		if (args.length == 0) {
			sendUsage().queue();
			return;
		}
		if (args.length >= 1) {
			List<BeatmapSearch> bb = new ArrayList<BeatmapSearch>();
			List<EmbedBuilder> emb = new ArrayList<EmbedBuilder>();
			sendEmbedMessage(TypeEmbed.LoadingEmbed().setTitle("Looking for Beatmaps")).queue(message -> {
				GoogleSearch searchBearmap = null;
				try {
					searchBearmap = new GoogleSearch(StringUtils.arrayToString2(0, args));
					List<Integer> beatmaplist = searchBearmap.getBeatmapSetIDs2();
					int i = 0;
					for (int n : beatmaplist) {
						bb.add(JSoupGetters.beatmapinfos(n));
						if (bb.get(i) == null) {
							continue;
						}
						emb.add(SearchEmbed.searchEmbed(bb.get(i), channel.getGuild()));
						i++;
					}

					if (bb.contains(null)) {
						bb.remove(null);
					}

					if (bb.size() == 0) {
						throw new SearchNotFoundException("Não foi encontrado beatmap sem search");
					}
				} catch (SearchNotFoundException | InvalidBeatmapException | IOException e) {
					String[] str = getLang().translatedArrayOsuMessages("INEXISTENT_BEATMAPID");
					StringBuffer buffer = new StringBuffer();
					for (String s : str) {
						if (s != str[0]) {
							buffer.append(s + "\n");
						}
					}
					message.editMessage(TypeEmbed.WarningEmbed(str[0], buffer.toString()).build()).queue();
					return;
				}

				message.editMessage(emb.get(0).build()).queue(message2 -> {
					try {
						channel.sendFile(bb.get(0).getBeatmapPreview(), bb.get(0).getTitle() + ".mp3").queue();
					} catch (IOException e) {
						e.printStackTrace();
					}

					EmbedBuilder[] bm = new EmbedBuilder[emb.size()];
					emb.toArray(bm);

					ReactionMessage.searchReactions.add(new TopUserReaction(user, message.getId(), bm, 0));
					// message2.addReaction("U+1F3AF").queue();
					message2.addReaction("U+25C0").queue();
					message2.addReaction("U+25B6").queue();

				});
			});

			return;
		}

	}

}

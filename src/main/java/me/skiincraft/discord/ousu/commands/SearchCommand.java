package me.skiincraft.discord.ousu.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.skiincraft.discord.core.reactions.ReactionObject;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.embed.SearchEmbed;
import me.skiincraft.discord.ousu.exceptions.SearchException;
import me.skiincraft.discord.ousu.htmlpage.BeatmapSearch;
import me.skiincraft.discord.ousu.htmlpage.GoogleSearch;
import me.skiincraft.discord.ousu.htmlpage.JSoupGetters;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import me.skiincraft.discord.ousu.reactions.HistoryLists;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class SearchCommand extends Comando {

	public SearchCommand() {
		super("search", Arrays.asList("pesquisar", "queue"), "search <queue>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Osu;
	}

	public void execute(User user, String[] args, TextChannel channel) {
		if (args.length == 0) {
			replyUsage();
			return;
		}
		if (args.length >= 1) {
			reply(TypeEmbed.LoadingEmbed().build(), message ->{
				try {
					List<Integer> bIds = new GoogleSearch(StringUtils.arrayToString2(0, args)).getBeatmapSetIDs();
					List<EmbedBuilder> embeds = new ArrayList<>();
					int first = 0;
					for (int id : bIds) {
						BeatmapSearch info = JSoupGetters.beatmapInfo(id);
						if (info == null) {
							continue;
						}
						EmbedBuilder sEmbed = SearchEmbed.searchEmbed(info, channel.getGuild());
						if (first == 0 && sEmbed != null) {
							message.editMessage(new EmbedBuilder(sEmbed)
									.setFooter("Loading more beatmaps").build()).queue();
							channel.sendFile(info.getBeatmapPreview(), info.getTitle() + ".mp3").queue();
							first =1;
						}
						
						if (sEmbed == null) {
							continue;
						}
						
						embeds.add(sEmbed);
					}
					
					if (embeds.size() == 0) {
						throw new SearchException("invalid");
					}
					
					message.editMessage(embeds.get(0).build()).queue(message2 -> {
						
						EmbedBuilder[] bm = embeds.toArray(new EmbedBuilder[embeds.size()]);
						
						// message2.addReaction("U+1F3AF").queue();s
						message2.addReaction("U+25C0").queue();
						message2.addReaction("U+25B6").queue();
						
						HistoryLists.addToReaction(user, message2, new ReactionObject(bm, 0));
					});
				} catch (Exception e) {
					String[] msg = getLanguageManager().getStrings("Warnings", "INEXISTENT_BEATMAPID");

					MessageEmbed build = TypeEmbed.WarningEmbed(msg[0], StringUtils.commandMessage(msg)).build();
					message.editMessage(build).queue();
					e.printStackTrace();
					return;
				}
			});
		}
		
	}

}

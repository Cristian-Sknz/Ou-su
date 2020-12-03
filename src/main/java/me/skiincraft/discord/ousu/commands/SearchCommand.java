package me.skiincraft.discord.ousu.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.common.reactions.ReactionObject;
import me.skiincraft.discord.core.common.reactions.Reactions;
import me.skiincraft.discord.core.common.reactions.custom.ReactionPage;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.embed.SearchEmbed;
import me.skiincraft.discord.ousu.exceptions.SearchException;
import me.skiincraft.discord.ousu.crawler.BeatmapSearch;
import me.skiincraft.discord.ousu.crawler.GoogleSearch;
import me.skiincraft.discord.ousu.crawler.JSoupGetters;
import me.skiincraft.discord.ousu.messages.TypeEmbed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SearchCommand extends Comando {

	public SearchCommand() {
		super("search", Arrays.asList("map", "pesquisar", "queue"), "search <name>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Gameplay;
	}

	public void execute(Member user, String[] args, InteractChannel channel) {
		if (args.length == 0) {
			replyUsage(channel.getTextChannel());
			return;
		}
		LanguageManager lang = getLanguageManager(channel.getTextChannel().getGuild());
		channel.reply(TypeEmbed.LoadingEmbed().build(), message ->{
			try {
				List<Integer> bIds = new GoogleSearch(StringUtils.arrayToString2(0, args)).getBeatmapSetIDs();
				List<EmbedBuilder> embeds = new ArrayList<>();
				int first = 0;
				for (int id : bIds) {
					BeatmapSearch info = JSoupGetters.beatmapInfo(id);
					if (info == null) {
						continue;
					}
					EmbedBuilder sEmbed = SearchEmbed.searchEmbed(info, lang);
					if (first == 0) {
						message.editMessage(new EmbedBuilder(sEmbed)
								.setFooter("Loading more beatmaps").build()).queue();
						channel.getTextChannel().sendFile(info.getBeatmapPreview(), info.getTitle() + ".mp3").queue();
						first =1;
					}

					embeds.add(sEmbed);
				}

				if (embeds.size() == 0) {
					throw new SearchException("invalid");
				}

				message.editMessage(embeds.get(0).build()).queue(message2 -> Objects.requireNonNull(Reactions.getInstance()).registerReaction(new ReactionObject(message2, user.getIdLong(),
						new String[]{"U+25C0", "U+25B6"}), new ReactionPage(embeds, true)));
			} catch (Exception e) {
				String[] msg = lang.getStrings("Warnings", "INEXISTENT_BEATMAPID");

				MessageEmbed build = TypeEmbed.WarningEmbed(msg[0], StringUtils.commandMessage(msg)).build();
				message.editMessage(build).queue();
				e.printStackTrace();
			}
		});

	}

}

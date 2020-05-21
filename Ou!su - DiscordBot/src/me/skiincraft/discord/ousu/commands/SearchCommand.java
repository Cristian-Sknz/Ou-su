package me.skiincraft.discord.ousu.commands;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.api.ousu.exceptions.InvalidBeatmapException;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.embeds.SearchEmbed;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.events.TopUserReaction;
import me.skiincraft.discord.ousu.exception.SearchNotFoundException;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.search.SearchBearmap;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.entities.Message;
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
			SearchBearmap searchBearmap = null;
			List<Beatmap> beat;

			try {
				searchBearmap = new SearchBearmap(StringUtils.arrayToString2(0, args));
				beat = OusuBot.getOsu().getBeatmapSet(searchBearmap.getBeatmapSetIDs2().get(0));
			} catch (SearchNotFoundException | InvalidBeatmapException e) {
				String[] str = getLang().translatedArrayOsuMessages("INEXISTENT_BEATMAPID");
				StringBuffer buffer = new StringBuffer();
				for (String s : str) {
					if (s != str[0]) {
						buffer.append(s + "\n");
					}
				}

				sendEmbedMessage(TypeEmbed.WarningEmbed(str[0], buffer.toString())).queue();
				return;
			}

			sendEmbedMessage(SearchEmbed.beatmapEmbed(beat, channel.getGuild())).queue(new Consumer<Message>() {

				@Override
				public void accept(Message message) {
					channel.sendFile(SearchEmbed.getAudioPreview(), beat.get(0).getTitle() + ".mp3").queue();
					Beatmap[] bm = new Beatmap[beat.size()];
					beat.toArray(bm);

					ReactionMessage.searchReactions.add(new TopUserReaction(user, message.getId(), bm, 0));
					message.addReaction("U+1F3AF").queue();
					
				}
			});

			return;
		}

	}

}

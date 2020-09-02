package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.io.IOException;

import me.skiincraft.api.ousu.Request;
import me.skiincraft.api.ousu.entity.beatmap.Beatmap;
import me.skiincraft.api.ousu.entity.beatmap.BeatmapSet;
import me.skiincraft.api.ousu.exceptions.BeatmapException;
import me.skiincraft.discord.core.reactions.ReactionObject;
import me.skiincraft.discord.core.utils.Emoji;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.embed.BeatmapEmbed;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import me.skiincraft.discord.ousu.reactions.HistoryLists;
import me.skiincraft.discord.ousu.utils.OusuUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class BeatmapSetCommand extends Comando {

	public BeatmapSetCommand() {
		super("beatmapset", null, "beatmapset <beatmapsetId>");
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
			if (args[0].matches("-?\\d+(\\.\\d+)?") == false) {
				replyUsage();
				return;
			}
			
			try {
				Request<BeatmapSet> request = OusuBot.getApi().getBeatmapSet(Long.valueOf(args[0]));
				BeatmapSet beatmapSet = request.get();

				reply(TypeEmbed.LoadingEmbed().build(), message -> {
					EmbedBuilder[] embedArray = new EmbedBuilder[beatmapSet.size()];
					int i = 0;
					Color cor = OusuUtils.beatmapColor(beatmapSet.get(0));
					
					for (Beatmap b : beatmapSet) {
						embedArray[i] = BeatmapEmbed.beatmapEmbed(b, channel.getGuild(), cor);
						i++;
					}

					message.editMessage(embedArray[0].build()).queue();

					message.addReaction("U+25C0").queue();
					//message.addReaction("U+25FC").queue();
					message.addReaction("U+25B6").queue();
					
					HistoryLists.addToReaction(user, message, new ReactionObject(embedArray, 0));
					try {
						Beatmap beatmap = beatmapSet.get(0);
						message.getChannel().sendFile(beatmap.getBeatmapPreview(),	embedArray[0].build().getTitle()
								.replace(Emoji.HEADPHONES.getAsMention(), "") + ".mp3")
								.queue();
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				
			} catch (BeatmapException e) {
				String[] msg = getLanguageManager().getStrings("Warnings", "INEXISTENT_BEATMAPID");

				MessageEmbed build = TypeEmbed.WarningEmbed(msg[0], StringUtils.commandMessage(msg)).build();
				reply(build);
				return;
			}
		}
		
	}

}

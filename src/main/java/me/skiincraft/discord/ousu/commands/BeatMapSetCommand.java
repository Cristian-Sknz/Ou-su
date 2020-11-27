package me.skiincraft.discord.ousu.commands;

import me.skiincraft.api.ousu.entity.beatmap.Beatmap;
import me.skiincraft.api.ousu.entity.beatmap.BeatmapSet;
import me.skiincraft.api.ousu.exceptions.BeatmapException;
import me.skiincraft.api.ousu.requests.Request;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.common.reactions.ReactionObject;
import me.skiincraft.discord.core.common.reactions.Reactions;
import me.skiincraft.discord.core.common.reactions.custom.ReactionPage;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.embed.BeatmapEmbed;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import me.skiincraft.discord.ousu.utils.OusuUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BeatmapSetCommand extends Comando {

	public BeatmapSetCommand() {
		super("beatmapset", null, "beatmapset <beatmapsetId>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Gameplay;
	}

	public void execute(Member member, String[] args, InteractChannel channel) {
		if (args.length == 0) {
			replyUsage(channel.getTextChannel());
			return;
		}

		if (!args[0].matches("-?\\d+(\\.\\d+)?")) {
			replyUsage(channel.getTextChannel());
			return;
		}

		try {
			Request<BeatmapSet> request = OusuBot.getApi().getBeatmapSet(Long.parseLong(args[0]));
			BeatmapSet beatmapSet = request.get();
			AtomicInteger integer = new AtomicInteger(0);
			Color cor = OusuUtils.beatmapColor(beatmapSet.get(0));

			channel.reply(TypeEmbed.LoadingEmbed().build(), message -> {
				EmbedBuilder[] embedArray = new EmbedBuilder[beatmapSet.size()];
				beatmapSet.forEach(b -> embedArray[integer.getAndIncrement()] = BeatmapEmbed.beatmapEmbed(b, channel.getTextChannel().getGuild(), cor));
				Objects.requireNonNull(Reactions.getInstance()).registerReaction(new ReactionObject(message, member.getIdLong(),
								new String[]{"U+25C0","U+25B6"}),
						new ReactionPage(Arrays.asList(embedArray), true));

				message.editMessage(embedArray[0].build()).queue();

				try {
					Beatmap beatmap = beatmapSet.get(0);
					message.getChannel().sendFile(beatmap.getBeatmapPreview(),	Objects.requireNonNull(embedArray[0].build()
							.getTitle())
							.replace(":headphones:", "") + ".mp3")
							.queue();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

		} catch (BeatmapException e) {
			String[] msg = getLanguageManager(channel.getTextChannel().getGuild()).getStrings("Warnings", "INEXISTENT_BEATMAPID");

			MessageEmbed build = TypeEmbed.WarningEmbed(msg[0], Arrays.stream(msg).skip(1).collect(Collectors.joining("\n"))).build();
			channel.reply(build);
		} catch (Exception e){
			channel.reply(TypeEmbed.errorMessage(e, channel.getTextChannel()).build());
		}
	}

}

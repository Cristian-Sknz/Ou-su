package me.skiincraft.discord.ousu.commands;

import me.skiincraft.api.ousu.entity.beatmap.Beatmap;
import me.skiincraft.api.ousu.exceptions.BeatmapException;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.embed.BeatmapEmbed;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import me.skiincraft.discord.ousu.utils.OusuUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class BeatmapCommand extends Comando {

	public BeatmapCommand() {
		super("beatmap", null, "beatmap <beatmapId>");
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
			Beatmap beatmap = OusuBot.getAPI().getBeatmap(Integer.parseInt(args[0])).get();
			channel.reply(BeatmapEmbed.beatmapEmbed(beatmap, channel.getTextChannel().getGuild(), OusuUtils.beatmapColor(beatmap)).build(), message -> {
				try {
					message.getChannel().sendFile(beatmap.getBeatmapPreview(), Objects.requireNonNull(message.getEmbeds()
							.get(0)
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
		} catch (Exception e) {
			channel.reply(TypeEmbed.errorMessage(e, channel.getTextChannel()).build());
		}
	}

}

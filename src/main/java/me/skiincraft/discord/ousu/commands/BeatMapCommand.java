package me.skiincraft.discord.ousu.commands;

import java.io.IOException;
import me.skiincraft.api.ousu.entity.beatmap.Beatmap;
import me.skiincraft.api.ousu.exceptions.BeatmapException;
import me.skiincraft.discord.core.utils.Emoji;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.embed.BeatmapEmbed;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import me.skiincraft.discord.ousu.utils.OusuUtils;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class BeatmapCommand extends Comando {

	public BeatmapCommand() {
		super("beatmap", null, "beatmap <beatmapid>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Osu;
	}

	public void execute(User user, String[] args, TextChannel channel) {
		if (args.length == 0) {
			replyUsage();
			return;
		}

		if (!args[0].matches("-?\\d+(\\.\\d+)?")) {
			replyUsage();
			return;
		}
		try {
		Beatmap beatmap = OusuBot.getApi().getBeatmap(Integer.parseInt(args[0])).get();
		reply(BeatmapEmbed.beatmapEmbed(beatmap, channel.getGuild(), OusuUtils.beatmapColor(beatmap)).build(), message -> {
			try {
				message.getChannel().sendFile(beatmap.getBeatmapPreview(),	message.getEmbeds()
						.get(0)
						.getTitle()
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
		}

	}

}

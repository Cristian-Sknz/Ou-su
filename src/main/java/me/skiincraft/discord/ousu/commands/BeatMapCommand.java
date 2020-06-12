package me.skiincraft.discord.ousu.commands;

import java.io.IOException;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.api.ousu.exceptions.InvalidBeatmapException;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.embeds.BeatmapEmbed;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.utils.Emoji;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class BeatMapCommand extends Commands {

	public BeatMapCommand() {
		super("ou!", "beatmap", "ou!beatmap <id>", null);
	}

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("OSU_HELPMESSAGE_BEATMAP");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Osu;
	}

	@Override
	public void action(String[] args, String label, TextChannel channel) {
		if (args.length == 0) {
			sendUsage().queue();
			return;
		}

		if (args.length >= 1) {
			try {
				Beatmap osuBeat = OusuBot.getOsu().getBeatmap(Integer.valueOf(args[0]));
				sendEmbedMessage(BeatmapEmbed.beatmapEmbed(osuBeat, channel.getGuild())).queue(message -> {
					try {
						message.getChannel().sendFile(osuBeat.getBeatmapPreview(),	message.getEmbeds().get(0)
								.getTitle()
								.replace(Emoji.HEADPHONES.getAsMention(), "") + ".mp3")
								.queue();
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			} catch (InvalidBeatmapException e) {
				String[] msg = getLang().translatedArrayOsuMessages("INEXISTENT_BEATMAPID");

				MessageEmbed build = TypeEmbed.WarningEmbed(msg[0], StringUtils.commandMessage(msg)).build();
				sendEmbedMessage(build).queue();
				return;
			} catch (NumberFormatException e) {
				String[] msg = getLang().translatedArrayOsuMessages("USE_NUMBERS");
				MessageEmbed build = TypeEmbed.WarningEmbed(msg[0], StringUtils.commandMessage(msg)).build();

				sendEmbedMessage(build).queue(message -> {
					String[] msg2 = getLang().translatedArrayOsuMessages("USE_NUMBERS2");
					MessageEmbed build2 = TypeEmbed.InfoEmbed(msg2[0], StringUtils.commandMessage(msg2)).build();
					channel.sendMessage(build2).queue();
				});
				return;
			}
		}
	}
}

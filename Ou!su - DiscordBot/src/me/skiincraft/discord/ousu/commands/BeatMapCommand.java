package me.skiincraft.discord.ousu.commands;

import java.util.Arrays;
import java.util.function.Consumer;

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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

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
	public void action(String[] args, String label, User user, TextChannel channel) {
		if (args.length == 0) {
			sendUsage().queue();
			return;
		}

		if (args.length == 1) {
			Beatmap osuBeat;
			try {
				osuBeat = OusuBot.getOsu().getBeatmap(Integer.valueOf(args[0]));
			} catch (InvalidBeatmapException e) {
				String[] msg = getLang().translatedArrayOsuMessages("INEXISTENT_BEATMAPID");

				MessageEmbed build = TypeEmbed.WarningEmbed(msg[0], StringUtils.commandMessage(msg)).build();
				sendEmbedMessage(build).queue();
				return;
			} catch (NumberFormatException e) {
				String[] msg = getLang().translatedArrayOsuMessages("USE_NUMBERS");
				MessageEmbed build = TypeEmbed.WarningEmbed(msg[0], StringUtils.commandMessage(msg)).build();

				sendEmbedMessage(build).queue();
				return;
			}

			sendEmbedMessage(BeatmapEmbed.beatmapEmbed(Arrays.asList(osuBeat), 0, channel.getGuild()))
					.queue(new Consumer<Message>() {

						@Override
						public void accept(Message message) {
							message.getChannel().sendFile(BeatmapEmbed.idb,
									message.getEmbeds().get(0).getTitle().replace(Emoji.HEADPHONES.getAsMention(), "")
											+ ".mp3")
									.queue();
						}
					});
		}
	}
}

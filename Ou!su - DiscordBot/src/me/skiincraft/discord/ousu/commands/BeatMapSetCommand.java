package me.skiincraft.discord.ousu.commands;

import java.util.List;
import java.util.function.Consumer;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.api.ousu.exceptions.InvalidBeatmapException;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.embeds.BeatmapEmbed;
import me.skiincraft.discord.ousu.embedtypes.DefaultEmbed;
import me.skiincraft.discord.ousu.events.TopUserReaction;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class BeatMapSetCommand extends Commands {

	public BeatMapSetCommand() {
		super("ou!", "beatmapset", "ou!beatmapset <id>", null);
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
			List<Beatmap> osuBeat;
			try {
				osuBeat = OusuBot.getOsu().getBeatmapSet(Integer.valueOf(args[0]));
			} catch (InvalidBeatmapException e) {
				String[] msg = getLang().translatedArrayOsuMessages("INEXISTENT_BEATMAPID");
				sendEmbedMessage(new DefaultEmbed(msg[0], StringUtils.arrayToString(1, msg))).queue();
				return;
			} catch (NumberFormatException e) {
				String[] msg = getLang().translatedArrayOsuMessages("USE_NUMBERS");
				sendEmbedMessage(new DefaultEmbed(msg[0], StringUtils.arrayToString(1, msg))).queue();
				return;
			}
			
			sendEmbedMessage(BeatmapEmbed.beatmapEmbed(osuBeat, 0,channel.getGuild())).queue(new Consumer<Message>() {

				@Override
				public void accept(Message message) {
					message.addReaction("U+25C0").queue();
					message.addReaction("U+25FC").queue();
					message.addReaction("U+25B6").queue();
					
					Beatmap[] bm = new Beatmap[osuBeat.size()];
					osuBeat.toArray(bm);
					
					ReactionMessage.beatHistory.add(new TopUserReaction(user, message.getId(), bm, 0));
					message.getChannel().sendFile(BeatmapEmbed.idb, message.getEmbeds().get(0).getTitle() + ".mp3").queue();
				}
			});
		}
	}
}

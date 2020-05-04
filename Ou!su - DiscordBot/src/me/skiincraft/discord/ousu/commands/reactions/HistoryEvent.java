package me.skiincraft.discord.ousu.commands.reactions;

import java.util.List;
import java.util.function.Consumer;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.api.ousu.scores.Score;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.commands.TopUserCommand;
import me.skiincraft.discord.ousu.embeds.BeatmapEmbed;
import me.skiincraft.discord.ousu.events.ReactionUtils;
import me.skiincraft.discord.ousu.manager.ReactionsManager;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class HistoryEvent extends ReactionsManager {

	@Override
	public void action(User user, TextChannel channel, String emoji) {

		if (emoji.equalsIgnoreCase("◀")) {
			ReactionMessage.osuHistory.remove(getUtils());

			int v = getUtils().getValue();
			if (v <= 0) {
				v = 0;
			} else {
				v = getUtils().getValue() - 1;
			}

			EmbedBuilder embed = TopUserCommand
					.embed(OusuBot.getOsu().getTopUser(getUtils().getMsg(), Gamemode.Standard, 5), v);

			ReactionMessage.osuHistory
					.add(new ReactionUtils(getUtils().getUser(), getUtils().getMessageID(), getUtils().getMsg(), v));

			channel.editMessageById(getEvent().getMessageId(), embed.build()).queue();

		}

		// http://b.ppy.sh/preview/music.mp3

		if (emoji.equalsIgnoreCase("◼")) {
			if (ReactionMessage.historyLastMessage.containsKey(user)) {

				EmbedBuilder embed = TopUserCommand.embed(
						OusuBot.getOsu().getTopUser(getUtils().getMsg(), Gamemode.Standard, 5), getUtils().getValue());

				channel.editMessageById(getEvent().getMessageId(), embed.build()).queue();
				ReactionMessage.historyLastMessage.remove(user);

				if (ReactionMessage.removeAudioMessage.containsKey(user)) {
					channel.deleteMessageById(ReactionMessage.removeAudioMessage.get(user)).queue();
					ReactionMessage.removeAudioMessage.remove(user);
				}

			} else {
				List<Score>	userss = OusuBot.getOsu().getTopUser(getUtils().getMsg(), Gamemode.Standard, getUtils().getValue());

				int beatmapset = userss.get(getUtils().getValue()).getBeatmapID();
				Beatmap beatmap = OusuBot.getOsu().getBeatmap(beatmapset);

				String thismessageid = getEvent().getMessageId();

				Message message = channel.getHistory().getMessageById(thismessageid);

				MessageBuilder sb = new MessageBuilder(message);
				ReactionMessage.historyLastMessage.put(user, sb);
				channel.editMessageById(getEvent().getMessageId(), BeatmapEmbed.beatmapEmbed(beatmap).build())
						.queue(new Consumer<Message>() {

							@Override
							public void accept(Message message) {

								message.getChannel()
										.sendFile(BeatmapEmbed.idb, message.getEmbeds().get(0).getTitle() + ".mp3")
										.queue(new Consumer<Message>() {

											@Override
											public void accept(Message t) {
												ReactionMessage.removeAudioMessage.put(user, t.getId());
											}
										});

							}
						});
			}
		}

		if (emoji.equalsIgnoreCase("▶")) {

			int v = getUtils().getValue();
			v += 1;

			try {
				EmbedBuilder embed = TopUserCommand
						.embed(OusuBot.getOsu().getTopUser(getUtils().getMsg(), Gamemode.Standard, 5), v);

				channel.editMessageById(getEvent().getMessageId(), embed.build()).queue();
				if (ReactionMessage.removeAudioMessage.containsKey(user)) {
					channel.deleteMessageById(ReactionMessage.removeAudioMessage.get(user)).queue();
				}
				ReactionMessage.osuHistory.remove(getUtils());
			} catch (IndexOutOfBoundsException e) {
				return;
			}

			ReactionMessage.osuHistory
					.add(new ReactionUtils(getUtils().getUser(), getUtils().getMessageID(), getUtils().getMsg(), v));
		}

		System.out.println(emoji);
	}
}

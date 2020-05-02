package me.skiincraft.discord.ousu.commands.reactions;

import java.net.MalformedURLException;
import java.util.function.Consumer;

import com.oopsjpeg.osu4j.GameMode;
import com.oopsjpeg.osu4j.exception.OsuAPIException;

import me.skiincraft.discord.ousu.commands.TopUserCommand;
import me.skiincraft.discord.ousu.embeds.BeatmapEmbed;
import me.skiincraft.discord.ousu.events.ReactionUtils;
import me.skiincraft.discord.ousu.manager.ReactionsManager;
import me.skiincraft.discord.ousu.osu.BeatmapOsu;
import me.skiincraft.discord.ousu.osu.ScoreType;
import me.skiincraft.discord.ousu.osu.UserOsu;
import me.skiincraft.discord.ousu.osu.UserScores;
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

			EmbedBuilder embed = null;
			try {
				embed = TopUserCommand.embed(
						new UserScores(new UserOsu(getUtils().getMsg(), GameMode.STANDARD), ScoreType.TopsScore, v));
			} catch (MalformedURLException | OsuAPIException e) {
				return;
			}

			ReactionMessage.osuHistory
					.add(new ReactionUtils(getUtils().getUser(), getUtils().getMessageID(), getUtils().getMsg(), v));

			channel.editMessageById(getEvent().getMessageId(), embed.build()).queue();

		}

		// http://b.ppy.sh/preview/music.mp3

		if (emoji.equalsIgnoreCase("◼")) {
			if (ReactionMessage.historyLastMessage.containsKey(user)) {
				EmbedBuilder embed = null;

				try {
					embed = TopUserCommand.embed(new UserScores(new UserOsu(getUtils().getMsg(), GameMode.STANDARD),
							ScoreType.TopsScore, getUtils().getValue()));
				} catch (MalformedURLException | OsuAPIException e) {
					e.printStackTrace();
				}

				channel.editMessageById(getEvent().getMessageId(), embed.build()).queue();
				ReactionMessage.historyLastMessage.remove(user);

				if (ReactionMessage.removeAudioMessage.containsKey(user)) {
					channel.deleteMessageById(ReactionMessage.removeAudioMessage.get(user)).queue();
					ReactionMessage.removeAudioMessage.remove(user);
				}

			} else {
				UserScores userss;
				try {
					userss = new UserScores(new UserOsu(getUtils().getMsg(), GameMode.STANDARD), ScoreType.TopsScore,
							getUtils().getValue());
					int beatmapset = userss.getBeatmap().getID();
					BeatmapOsu osuBeat = new BeatmapOsu(beatmapset);

					String thismessageid = getEvent().getMessageId();

					Message message = channel.getHistory().getMessageById(thismessageid);

					MessageBuilder sb = new MessageBuilder(message);
					ReactionMessage.historyLastMessage.put(user, sb);
					channel.editMessageById(getEvent().getMessageId(), BeatmapEmbed.beatmapEmbed(osuBeat).build())
							.queue(new Consumer<Message>() {

								@Override
								public void accept(Message message) {

									message.getChannel()
											.sendFile(BeatmapEmbed.idb,
													message.getEmbeds().get(0).getTitle() + ".mp3")
											.queue(new Consumer<Message>() {

												@Override
												public void accept(Message t) {
													ReactionMessage.removeAudioMessage.put(user, t.getId());
												}
											});

								}
							});
				} catch (MalformedURLException | OsuAPIException e) {
					e.printStackTrace();
				}
			}
		}

		if (emoji.equalsIgnoreCase("▶")) {

			int v = getUtils().getValue();
			v += 1;

			try {
				EmbedBuilder embed = TopUserCommand.embed(
						new UserScores(new UserOsu(getUtils().getMsg(), GameMode.STANDARD), ScoreType.TopsScore, v));
				channel.editMessageById(getEvent().getMessageId(), embed.build()).queue();
				if (ReactionMessage.removeAudioMessage.containsKey(user)) {
					channel.deleteMessageById(ReactionMessage.removeAudioMessage.get(user)).queue();
				}
				ReactionMessage.osuHistory.remove(getUtils());
			} catch (IndexOutOfBoundsException | MalformedURLException | OsuAPIException e) {
				return;
			}

			ReactionMessage.osuHistory
					.add(new ReactionUtils(getUtils().getUser(), getUtils().getMessageID(), getUtils().getMsg(), v));
		}

		System.out.println(emoji);
	}
}

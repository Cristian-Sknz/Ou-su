package me.skiincraft.discord.ousu.reactions;

import java.util.Arrays;
import java.util.List;

import me.skiincraft.api.ousu.scores.Score;
import me.skiincraft.discord.ousu.commands.TopUserCommand;
import me.skiincraft.discord.ousu.events.TopUserReaction;
import me.skiincraft.discord.ousu.manager.ReactionUtils;
import me.skiincraft.discord.ousu.manager.ReactionsManager;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class HistoryEvent extends ReactionsManager {

	@Override
	public List<ReactionUtils> listHistory() {
		return ReactionMessage.osuHistory;
	}

	@Override
	public void action(User user, TextChannel channel, String emoji) {

		if (emoji.equalsIgnoreCase("◀")) {
			listHistory().remove(getUtils());

			Object obj = getUtils().getObject();
			Score[] score = (Score[]) obj;
			int v = getUtils().getValue();
			if (v <= 0) {
				v = 0;
				listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, v));
				return;
			} else {
				v = getUtils().getValue() - 1;
			}

			EmbedBuilder embed = TopUserCommand.embed(Arrays.asList(score), v, channel.getGuild());

			channel.editMessageById(getEvent().getMessageId(), embed.build()).queue();
			listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, v));

		}

		// http://b.ppy.sh/preview/music.mp3

		if (emoji.equalsIgnoreCase("◼")) {

		}

		if (emoji.equalsIgnoreCase("▶")) {
			listHistory().remove(getUtils());
			int v = getUtils().getValue();
			v += 1;

			Object obj = getUtils().getObject();
			Score[] score = (Score[]) obj;

			if (v >= score.length) {
				listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, score.length - 1));
				return;
			}
			EmbedBuilder embed = TopUserCommand.embed(Arrays.asList(score), v, channel.getGuild());

			channel.editMessageById(getEvent().getMessageId(), embed.build()).queue();
			listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, v));
		}
	}

}

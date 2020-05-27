package me.skiincraft.discord.ousu.commands.reactions;

import java.util.List;

import me.skiincraft.discord.ousu.events.TopUserReaction;
import me.skiincraft.discord.ousu.manager.ReactionUtils;
import me.skiincraft.discord.ousu.manager.ReactionsManager;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class SkinsReactionEvent extends ReactionsManager {

	@Override
	public List<ReactionUtils> listHistory() {
		return ReactionMessage.skinsReaction;
	}

	@Override
	public void action(User user, TextChannel channel, String emoji) {

		if (emoji.equalsIgnoreCase("◀")) {
			listHistory().remove(getUtils());

			Object obj = getUtils().getObject();
			EmbedBuilder[] score = (EmbedBuilder[]) obj;
			int v = getUtils().getValue();
			if (v <= 0) {
				v = 0;
				listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, v));
				return;
			} else {
				v = getUtils().getValue() - 1;
			}

			channel.editMessageById(getEvent().getMessageId(), score[v].build()).queue();
			listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, v));

		}

		if (emoji.equalsIgnoreCase("▶")) {
			listHistory().remove(getUtils());
			int v = getUtils().getValue();
			v += 1;

			Object obj = getUtils().getObject();
			EmbedBuilder[] score = (EmbedBuilder[]) obj;

			if (v >= score.length) {
				listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, score.length - 1));
				return;
			}

			channel.editMessageById(getEvent().getMessageId(), score[v].build()).queue();
			listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, v));
		}
	}

}

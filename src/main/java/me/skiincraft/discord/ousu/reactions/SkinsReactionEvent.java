package me.skiincraft.discord.ousu.reactions;

import java.util.List;

import me.skiincraft.discord.ousu.abstractcore.ReactionUtils;
import me.skiincraft.discord.ousu.abstractcore.ReactionsManager;
import me.skiincraft.discord.ousu.events.DefaultReaction;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class SkinsReactionEvent extends ReactionsManager {

	@Override
	public List<ReactionUtils> listHistory() {
		return ReactionMessage.skinsReaction;
	}

	@Override
	public void action(String userid, TextChannel channel, String emoji) {

		if (emoji.equalsIgnoreCase("◀")) {
			listHistory().remove(getUtils());

			Object obj = getUtils().getObject();
			EmbedBuilder[] score = (EmbedBuilder[]) obj;
			int v = getUtils().getValue();
			if (v <= 0) {
				v = 0;
				listHistory().add(new DefaultReaction(userid, getEvent().getMessageId(), obj, v));
				return;
			} else {
				v = getUtils().getValue() - 1;
			}

			channel.editMessageById(getEvent().getMessageId(), score[v].build()).queue();
			listHistory().add(new DefaultReaction(userid, getEvent().getMessageId(), obj, v));

		}

		if (emoji.equalsIgnoreCase("▶")) {
			listHistory().remove(getUtils());
			int v = getUtils().getValue();
			v += 1;

			Object obj = getUtils().getObject();
			EmbedBuilder[] score = (EmbedBuilder[]) obj;

			if (v >= score.length) {
				listHistory().add(new DefaultReaction(userid, getEvent().getMessageId(), obj, score.length - 1));
				return;
			}

			channel.editMessageById(getEvent().getMessageId(), score[v].build()).queue();
			listHistory().add(new DefaultReaction(userid, getEvent().getMessageId(), obj, v));
		}
	}

}

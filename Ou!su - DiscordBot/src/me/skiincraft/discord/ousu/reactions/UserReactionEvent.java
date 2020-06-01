package me.skiincraft.discord.ousu.reactions;

import java.util.List;

import me.skiincraft.discord.ousu.events.DefaultReaction;
import me.skiincraft.discord.ousu.manager.ReactionUtils;
import me.skiincraft.discord.ousu.manager.ReactionsManager;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class UserReactionEvent extends ReactionsManager {

	@Override
	public List<ReactionUtils> listHistory() {
		return ReactionMessage.userReactions;
	}

	@Override
	public void action(String userid, TextChannel channel, String emoji) {

		if (emoji.equalsIgnoreCase("📰")) {
			listHistory().remove(getUtils());

			Object obj = getUtils().getObject();
			EmbedBuilder[] score = (EmbedBuilder[]) obj;
			int v = getUtils().getValue();
			if (v == 0) {
				v = 1;
				channel.editMessageById(getEvent().getMessageId(), score[v].build()).queue();
				listHistory().add(new DefaultReaction(userid, getEvent().getMessageId(), obj, v));
				return;
			} else {
				v = 0;
				channel.editMessageById(getEvent().getMessageId(), score[v].build()).queue();
				listHistory().add(new DefaultReaction(userid, getEvent().getMessageId(), obj, v));
				return;
			}
		}
	}

}

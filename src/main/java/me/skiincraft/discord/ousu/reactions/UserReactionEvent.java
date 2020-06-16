package me.skiincraft.discord.ousu.reactions;

import java.util.List;

import me.skiincraft.discord.ousu.events.DoubleReaction;
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
		DoubleReaction reactions = (DoubleReaction) getUtils();
		if (emoji.equalsIgnoreCase("📰")) {
			listHistory().remove(reactions);

			Object obj = reactions.getObject();
			Object obj2 = reactions.getObject2();
			EmbedBuilder[] score = (EmbedBuilder[]) obj;
			int v = reactions.getValue();
			if (v >= 2) {
				v = 0;
			}
			
			if (v == 0) {
				v = 1;
				channel.editMessageById(getEvent().getMessageId(), score[v].build()).queue();
				listHistory().add(new DoubleReaction(userid, getEvent().getMessageId(), obj, obj2, v, 0));
				return;
			} else
				v = 0;
				channel.editMessageById(getEvent().getMessageId(), score[v].build()).queue();
				listHistory().add(new DoubleReaction(userid, getEvent().getMessageId(), obj, obj2, v, 0));
				return;
			}
		
		if (emoji.equalsIgnoreCase("📋")) {
			listHistory().remove(reactions);

			Object obj = reactions.getObject();
			Object obj2 = reactions.getObject2();
			EmbedBuilder[] score = (EmbedBuilder[]) obj2;
			int v = reactions.getValue2();
			v += 1;

			if (v >= score.length) {
				listHistory().add(new DoubleReaction(userid, getEvent().getMessageId(), obj, obj2, 1, 0));
				return;
			}
			channel.editMessageById(getEvent().getMessageId(), score[v].build()).queue();
			listHistory().add(new DoubleReaction(userid, getEvent().getMessageId(), obj, obj2, 1, v));
		}
	}

}

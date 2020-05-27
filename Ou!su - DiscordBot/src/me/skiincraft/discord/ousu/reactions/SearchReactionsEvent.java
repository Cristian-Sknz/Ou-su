package me.skiincraft.discord.ousu.reactions;

import java.util.List;
import java.util.function.Consumer;

import me.skiincraft.discord.ousu.events.TopUserReaction;
import me.skiincraft.discord.ousu.manager.ReactionUtils;
import me.skiincraft.discord.ousu.manager.ReactionsManager;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class SearchReactionsEvent extends ReactionsManager {

	@Override
	public List<ReactionUtils> listHistory() {
		return ReactionMessage.searchReactions;
	}

	@Override
	public void action(User user, TextChannel channel, String emoji) {

		if (emoji.equalsIgnoreCase("🎯")) {
			listHistory().remove(getUtils());

			Object obj = getUtils().getObject();
			EmbedBuilder[] score = (EmbedBuilder[]) obj;

			channel.clearReactionsById(getUtils().getMessageID()).queue();
			channel.editMessageById(getEvent().getMessageId(), score[0].build())
					.queue(new Consumer<Message>() {

						@Override
						public void accept(Message message) {
							message.addReaction("U+25C0").queue();
							message.addReaction("U+25FC").queue();
							message.addReaction("U+25B6").queue();
							ReactionMessage.beatHistory.add(new TopUserReaction(user, message.getId(), obj, 0));
						}
					});

		}
	}

}

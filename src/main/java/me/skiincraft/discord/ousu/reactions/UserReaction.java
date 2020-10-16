package me.skiincraft.discord.ousu.reactions;

import java.util.List;

import me.skiincraft.discord.core.reactions.Reaction;
import me.skiincraft.discord.core.reactions.ReactionObject;
import me.skiincraft.discord.core.reactions.ReactionUtil;

import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class UserReaction extends Reaction {

	public UserReaction() {
		super("userreaction");
	}

	public void execute(User user, TextChannel channel, ReactionEmote emote) {
		if (emote.getEmoji().equalsIgnoreCase("📎")) {
			ReactionObject object = getUtils().getReactionObjects()[0];
			if (object.getOrdem() == 0) {
				getContext().changeEmbedNext(object);
				return;
			}
			if (object.getOrdem() == 1) {
				getContext().changeEmbedBack(object);
            }
		}
	}

	public List<ReactionUtil> listHistory() {
		return HistoryLists.reationsList;
	}

}

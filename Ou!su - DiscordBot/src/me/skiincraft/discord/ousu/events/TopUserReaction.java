package me.skiincraft.discord.ousu.events;

import me.skiincraft.discord.ousu.manager.ReactionUtils;
import net.dv8tion.jda.api.entities.User;

public class TopUserReaction extends ReactionUtils {

	public TopUserReaction(User user, String messageID, Object obj, int ordem) {
		super(user, messageID, obj, ordem);
	}

}

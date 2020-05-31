package me.skiincraft.discord.ousu.events;

import me.skiincraft.discord.ousu.manager.ReactionUtils;

public class TopUserReaction extends ReactionUtils {

	public TopUserReaction(String userid, String messageID, Object obj, int ordem) {
		super(userid, messageID, obj, ordem);
	}

}

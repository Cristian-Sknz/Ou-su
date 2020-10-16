package me.skiincraft.discord.ousu.reactions;

import java.util.ArrayList;
import java.util.List;

import me.skiincraft.discord.core.reactions.ReactionObject;
import me.skiincraft.discord.core.reactions.ReactionUtil;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.CreatedReactionEvent;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class HistoryLists {
	
	public static final List<ReactionUtil> reationsList = new ArrayList<>();
	
	public static void addToReaction(User user, Message message, ReactionObject...reactionObjects) {
		ReactionUtil var = new ReactionUtil(user.getIdLong(),message.getIdLong(), message.getGuild().getIdLong(), reactionObjects);
		reationsList.add(var);
		OusuBot.getInstance().getPlugin().getEventManager().callEvent(new CreatedReactionEvent(message.getTextChannel(), user, reationsList, var));
	}
	
}

package me.skiincraft.discord.ousu.common;

import java.util.List;

import me.skiincraft.discord.core.event.Event;
import me.skiincraft.discord.core.reactions.ReactionUtil;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class CreatedReactionEvent extends Event {
	
	private TextChannel channel;
	private User user;
	private ReactionUtil reactionUtil;
	private List<ReactionUtil> lista;
	
	public CreatedReactionEvent(TextChannel channel, User user, List<ReactionUtil> lista, ReactionUtil reactionUtil) {
		this.channel = channel;
		this.user = user;
		this.reactionUtil = reactionUtil;
		this.lista = lista;
	}
	
	public boolean delete() {
		return lista.remove(reactionUtil); 
	}
	
	public TextChannel getChannel() {
		return channel;
	}
	
	public ReactionUtil getReactionUtil() {
		return reactionUtil;
	}
	
	public User getUser() {
		return user;
	}
}


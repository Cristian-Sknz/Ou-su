package me.skiincraft.discord.ousu.manager;

import java.util.List;

import me.skiincraft.discord.ousu.embedtypes.DefaultEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public abstract class ReactionsManager extends ListenerAdapter {

	private String messageID;
	private String emoji;
	private ReactionUtils utils;

	private GuildMessageReactionAddEvent event;

	public ReactionsManager() {
		super();
	}
	
	public abstract List<ReactionUtils> listHistory();
	
	public abstract void action(User user, TextChannel channel, String emoji);

	public boolean isValidReaction(GuildMessageReactionAddEvent event) {
		String eventMessageID = event.getMessageId();
		List<ReactionUtils> osuhistorys = listHistory();
		
		if (event.getUser().isBot()) {
			return false;
		}
		if (osuhistorys.isEmpty()) {
			return false;
		}

		ReactionUtils reactionUtils = null;
		for (ReactionUtils lista : osuhistorys) {
			if (eventMessageID.equalsIgnoreCase(lista.getMessageID())) {
				reactionUtils = lista;
			}
		}

		if (reactionUtils == null) {
			return false;
		}

		utils = reactionUtils;

		try {
			emoji = event.getReaction().getReactionEmote().getEmoji();
		} catch (IllegalStateException e) {
			return false;
		}

		this.event = event;
		try {
			event.getChannel().removeReactionById(event.getMessageId(), emoji, event.getUser()).queue();
		} catch (InsufficientPermissionException ex) {
			System.out.println("Sem permissão para mudar reaction em: \n" + event.getGuild().getName() + " - " + event.getGuild().getId());
		}

		return true;
	}

	public boolean hasPermission(User user, Permission permission) {
		if (event.getGuild().getMember(user).hasPermission(permission)) {
			return true;
		}
		return false;
	}

	public boolean hasRole(User user, String rolename) {
		List<Role> role = getEvent().getGuild().getRolesByName(rolename, true);
		List<Role> memberRoles = event.getGuild().getMember(user).getRoles();

		if (memberRoles.contains(role.get(0))) {
			return true;
		}
		return false;
	}

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		if (!isValidReaction(event)) {
			return;
		}
		action(event.getUser(), event.getChannel(), emoji);

	}

	public MessageAction sendMessage(String message) {
		MessageAction a = event.getChannel().sendMessage(message);
		return a;
	}

	public MessageAction sendPrivateMessage(String message) {
		MessageAction a = event.getUser().openPrivateChannel().complete().sendMessage(message);
		return a;
	}

	public MessageAction sendEmbedMessage(EmbedBuilder e) {
		MessageAction a = event.getChannel().sendMessage(e.build());
		return a;
	}

	public MessageAction sendEmbedMessage(DefaultEmbed e) {
		MessageAction a = event.getChannel().sendMessage(e.construir());
		return a;
	}

	public MessageAction sendPrivateEmbedMessage(DefaultEmbed e) {
		MessageAction a = event.getUser().openPrivateChannel().complete().sendMessage(e.construir());
		return a;
	}

	public GuildMessageReactionAddEvent getEvent() {
		return event;
	}

	public String getEmoji() {
		return emoji;
	}

	public String getMessageID() {
		return messageID;
	}

	public ReactionUtils getUtils() {
		return utils;
	}
}

package me.skiincraft.discord.ousu.events;

import net.dv8tion.jda.api.entities.User;

public class ReactionUtils {
	
	private User user;
	private String messageID;
	private int value;
	private String msg;

	public ReactionUtils(User user, String messageID, String msg ,int value) {
		this.user = user;
		this.messageID = messageID;
		this.value = value;
		this.msg = msg;
	}

	public User getUser() {
		return user;
	}

	public String getMessageID() {
		return messageID;
	}

	public int getValue() {
		return value;
	}

	public String getMsg() {
		return msg;
	}

}

package me.skiincraft.discord.ousu.manager;

import net.dv8tion.jda.api.entities.User;

public abstract class ReactionUtils {

	private User user;
	private String messageID;
	private int value;
	private Object object;

	public ReactionUtils(User user, String messageID, Object obj, int ordem) {
		this.user = user;
		this.messageID = messageID;
		this.value = ordem;
		this.setObject(obj);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

}

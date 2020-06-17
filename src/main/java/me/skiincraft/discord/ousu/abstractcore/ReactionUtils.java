package me.skiincraft.discord.ousu.abstractcore;

import me.skiincraft.discord.ousu.OusuBot;
import net.dv8tion.jda.api.entities.User;

public abstract class ReactionUtils {

	private String userid;
	private String messageID;
	private int value;
	private Object object;

	public ReactionUtils(String userid, String messageID, Object obj, int ordem) {
		this.userid = userid;
		this.messageID = messageID;
		this.value = ordem;
		this.setObject(obj);
	}

	public User getUser() {
		return OusuBot.getShardmanager().getUserById(userid);
	}

	public void setUser(String userid) {
		this.userid = userid;
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

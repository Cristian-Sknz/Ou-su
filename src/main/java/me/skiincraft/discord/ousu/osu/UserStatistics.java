package me.skiincraft.discord.ousu.osu;

import java.util.Map;

import me.skiincraft.api.ousu.entity.user.User;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import me.skiincraft.discord.ousu.crawler.JSoupGetters.InputTypes;

public class UserStatistics {
	
	private final String firstlogin;
	private final String lastActive;
	
	private final Map<InputTypes, Boolean> input;
	private final String lastPpCapture;
	private final String lastScoreCapture;
	
	private final User user;
	
	public UserStatistics(String firstlogin, String lastActive, Map<InputTypes, Boolean> input, String lastPpCapture,
			String lastScoreCapture, User user) {
		super();
		this.firstlogin = firstlogin;
		this.lastActive = lastActive;
		this.input = input;
		this.lastPpCapture = lastPpCapture;
		this.lastScoreCapture = lastScoreCapture;
		this.user = user;
	}
	
	public String getFirstlogin() {
		return firstlogin;
	}
	public String getLastActive() {
		return lastActive;
	}
	public Map<InputTypes, Boolean> getInput() {
		return input;
	}
	public String getLastPpCapture() {
		return lastPpCapture;
	}
	public String getLastScoreCapture() {
		return lastScoreCapture;
	}
	public User getUser() {
		return user;
	}
	public String getInputEmotes() {
		StringBuilder buffer = new StringBuilder();
		if (input.get(InputTypes.Mouse)) {
			buffer.append(":mouse_three_button:").append(" Mouse\n");
		}
		if (input.get(InputTypes.Table)) {
			buffer.append(" ").append(GenericsEmotes.getEmoteAsMention("tablet")).append(" Tablet\n");
		}
		if (input.get(InputTypes.Keyboard)) {
			buffer.append(" ").append(":keyboard:").append(" Keyboard\n");
		}
		if (input.get(InputTypes.Touchpad)) {
			buffer.append(" ").append(GenericsEmotes.getEmoteAsMention("touchpad")).append(" Touchpad\n");
		} 
		
		return (buffer.length() != 0) ?buffer.toString() : "Not Detected";
	}
	
	

}

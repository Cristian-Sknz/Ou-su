package me.skiincraft.discord.ousu.search;

import java.util.Map;

import me.skiincraft.api.ousu.users.User;
import me.skiincraft.discord.ousu.customemoji.OusuEmojis;
import me.skiincraft.discord.ousu.search.JSoupGetters.InputTypes;
import me.skiincraft.discord.ousu.utils.Emoji;

public class UserStatistics {
	
	private String firstlogin;
	private String lastActive;
	
	private Map<InputTypes, Boolean> input;
	private String lastPpCapture;
	private String lastScoreCapture;
	
	private User user;
	
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
		StringBuffer buffer = new StringBuffer();
		if (input.get(InputTypes.Mouse) == true) {
			buffer.append(Emoji.MOUSE_THREE_BUTTON.getAsMention() + " Mouse\n");
		}
		if (input.get(InputTypes.Table) == true) {
			buffer.append(" " + OusuEmojis.getEmoteAsMention("tablet") + " Tablet\n");
		}
		if (input.get(InputTypes.Keyboard) == true) {
			buffer.append(" " + Emoji.KEYBOARD.getAsMention() + " Keyboard\n");
		}
		if (input.get(InputTypes.Touchpad) == true) {
			buffer.append(" " + OusuEmojis.getEmoteAsMention("touchpad") + " Touchpad\n");
		} 
		
		return (buffer.length() != 0) ?buffer.toString() : "Not Detected";
	}
	
	

}

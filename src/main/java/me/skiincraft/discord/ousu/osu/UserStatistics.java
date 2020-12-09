package me.skiincraft.discord.ousu.osu;

import java.util.List;

import me.skiincraft.discord.ousu.crawler.InputType;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;

public class UserStatistics {
	
	private final String firstlogin;
	private final String lastActive;

	private final List<InputType> inputTypes;

	public UserStatistics(String firstlogin, String lastActive, List<InputType> inputTypes) {
		this.firstlogin = firstlogin;
		this.lastActive = lastActive;
		this.inputTypes = inputTypes;
	}

	public String getFirstlogin() {
		return firstlogin;
	}

	public String getLastActive() {
		return lastActive;
	}

	public String getInputEmotes() {
		if (inputTypes.size() == 0) {
			return "Not Detected";
		}
		StringBuilder builder = new StringBuilder();
		if (inputTypes.contains(InputType.Mouse)){
			builder.append(":mouse_three_button:").append(" Mouse\n");
		}
		if (inputTypes.contains(InputType.Tablet)){
			builder.append(GenericsEmotes.getEmoteAsMention("tablet")).append(" Tablet\n");
		}
		if (inputTypes.contains(InputType.Touch)){
			builder.append(GenericsEmotes.getEmoteAsMention("touchpad")).append(" Touchpad\n");
		}
		if (inputTypes.contains(InputType.Keyboard)){
			builder.append(":keyboard:").append(" Keyboard\n");
		}
		
		return builder.toString();
	}
	
	

}

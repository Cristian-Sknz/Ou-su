package me.skiincraft.discord.ousu.object;

import java.util.Arrays;

public enum Gender {
	
	FEMALE("Female"), MALE("Male"), UNKNOWN("Other");
	
	private String name;
	
	Gender(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static Gender getGender(String string) {
		if ("" == string || string.equalsIgnoreCase("?")) {
			return Gender.UNKNOWN;
		}
		return Arrays.stream(Gender.values()).filter(o -> o.name().equalsIgnoreCase(string)).findAny().orElse(Gender.UNKNOWN);
	}

}

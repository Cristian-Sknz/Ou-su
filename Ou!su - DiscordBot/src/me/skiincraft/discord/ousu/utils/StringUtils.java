package me.skiincraft.discord.ousu.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static boolean containsSpecialCharacters(String str) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
		Matcher matcher = pattern.matcher(str);

		if (!matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static String arrayToString(int num, String[] str) {
		StringBuffer buffer = new StringBuffer();
		for (int i = num; i < str.length; i++) {
			buffer.append(str[i] + "\n");
		}
		return buffer.toString();
	}

}

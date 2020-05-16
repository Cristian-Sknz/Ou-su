package me.skiincraft.discord.ousu.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	
	public static String arrayToString2(int num, String[] str) {
		StringBuffer buffer = new StringBuffer();
		for (int i = num; i < str.length; i++) {
			buffer.append(str[i] + " ");
		}
		return buffer.toString();
	}
	
	public static String[] removeStrings(String[] stringarray, int remove) {
		List<String> list = Arrays.asList(stringarray);
		list.remove(remove);
		String[] str = new String[list.size()];
		list.toArray(str);
		return str;
	}
	
	public static String[] removeString(String[] stringarray, int remove){
		List<String> list = new ArrayList<String>();
		for (String str : stringarray) {
			if (stringarray[remove] != str) {
				list.add(str);	
			}
		}
		
		stringarray = new String[list.size()];
		list.toArray(stringarray);
		
		return stringarray;
	}

}

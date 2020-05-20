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

	public static String commandMessage(String[] stringarray) {
		StringBuffer buffer = new StringBuffer();
		for (String str : stringarray) {
			if (str != stringarray[0]) {
				buffer.append(str + "\n");
			}
		}
		return buffer.toString();
	}

	public static String[] removeString(String[] stringarray, int remove) {
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
	
	public static String unescapeJavaString(String st) {

	    StringBuilder sb = new StringBuilder(st.length());

	    for (int i = 0; i < st.length(); i++) {
	        char ch = st.charAt(i);
	        if (ch == '\\') {
	            char nextChar = (i == st.length() - 1) ? '\\' : st
	                    .charAt(i + 1);
	            // Octal escape?
	            if (nextChar >= '0' && nextChar <= '7') {
	                String code = "" + nextChar;
	                i++;
	                if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
	                        && st.charAt(i + 1) <= '7') {
	                    code += st.charAt(i + 1);
	                    i++;
	                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
	                            && st.charAt(i + 1) <= '7') {
	                        code += st.charAt(i + 1);
	                        i++;
	                    }
	                }
	                sb.append((char) Integer.parseInt(code, 8));
	                continue;
	            }
	            switch (nextChar) {
	            case '\\':
	                ch = '\\';
	                break;
	            case 'b':
	                ch = '\b';
	                break;
	            case 'f':
	                ch = '\f';
	                break;
	            case 'n':
	                ch = '\n';
	                break;
	            case 'r':
	                ch = '\r';
	                break;
	            case 't':
	                ch = '\t';
	                break;
	            case '\"':
	                ch = '\"';
	                break;
	            case '\'':
	                ch = '\'';
	                break;
	            // Hex Unicode: u????
	            case 'u':
	                if (i >= st.length() - 5) {
	                    ch = 'u';
	                    break;
	                }
	                int code = Integer.parseInt(
	                        "" + st.charAt(i + 2) + st.charAt(i + 3)
	                                + st.charAt(i + 4) + st.charAt(i + 5), 16);
	                sb.append(Character.toChars(code));
	                i += 5;
	                continue;
	            }
	            i++;
	        }
	        sb.append(ch);
	    }
	    return sb.toString();
	}

}

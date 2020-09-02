package me.skiincraft.discord.ousu.osu;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebUser {
	
	public static String getName(String name) {
		try {
			Document a = Jsoup.parse(new URL("https://old.ppy.sh/u/" + name), 10000);
			return a.getElementsByClass("profile-username").text();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getName(long id) {
		try {
			Document a = Jsoup.parse(new URL("https://old.ppy.sh/u/" + id), 10000);
			return a.getElementsByClass("profile-username").text();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static long getId(String name) {
		try {
			Document a = Jsoup.parse(new URL("https://old.ppy.sh/u/" + name), 10000);
			Scanner s = new Scanner(a.toString());
		    while (s.hasNextLine()) {
		        String line = s.nextLine();
		        if(line.contains("userId")) { 
		            return Integer.valueOf(line.replaceAll("\\D+", ""));
		        }
		    }
		    return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		} 

	}
	
}

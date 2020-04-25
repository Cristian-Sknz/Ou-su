package me.skiincraft.discord.ousu.imagebuilders;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import me.skiincraft.discord.ousu.api.ImageBuilder;
import me.skiincraft.discord.ousu.osu.UserOsu;

public class OsuProfileNote {
	
	
	public static InputStream drawImage(UserOsu osuUser) {
		ImageBuilder builder = new ImageBuilder(osuUser.getUser().getID() + "_osuProfileNote", 900, 250);
		try {
			builder.addImage(new File("resource/osu_images/notes/Layer.png"), 0, 0, builder.getSize());
			
			scoreCalculates(builder, osuUser.getUser().getCountRankSSH(), 150);
			scoreCalculates(builder, osuUser.getUser().getCountRankSS(), 294);
			scoreCalculates(builder, osuUser.getUser().getCountRankSH(), 444);
			scoreCalculates(builder, osuUser.getUser().getCountRankS(), 595);
			scoreCalculates(builder, osuUser.getUser().getCountRankA(), 739);
			
			return builder.buildInput();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	public static void levelCalculate(ImageBuilder builder, String level) {
		if (level.length() == 1) {
			builder.addCentralizedString(level, 911, 201, 
					   new Font("Arial", Font.PLAIN, 53));
		}
		
		if (level.length() == 2) {
			builder.addCentralizedString(level, 911, 198, 
					   new Font("Arial", Font.PLAIN, 43));
		}
		
		if (level.length() == 3) {
			builder.addCentralizedString(level, 911, 199, 
					   new Font("Arial", Font.PLAIN, 31));
		}
	}
	
	public static void scoreCalculates(ImageBuilder builder, int level, int x) {
		String l = level+"";
		if (l.length() == 5) {
			builder.addCentralizedString(l, x, 211, 
					   new Font("Arial", Font.PLAIN, 34));
		} else {
			builder.addCentralizedString(l, x, 211, 
					   new Font("Arial", Font.PLAIN, 38));
		}
	}

}

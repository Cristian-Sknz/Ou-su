package me.skiincraft.discord.ousu.imagebuilders;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;

import me.skiincraft.discord.ousu.api.ImageBuilder;
import me.skiincraft.discord.ousu.osu.UserOsu;
import me.skiincraft.discord.ousu.utils.CustomFont;
import me.skiincraft.discord.ousu.utils.CustomFont.CustomFonts;

public class OsuProfile {
	
	public static InputStream drawImage(UserOsu osuUser) {
		DecimalFormat formatter = new DecimalFormat("#");
		ImageBuilder builder = new ImageBuilder(osuUser.getUser().getID() + "_osuProfile", 1000, 750);
		try {
			builder.addImage(new File("resource/osu_images/Background.png"), 0, 0, builder.getSize());
			URL url = new URL(osuUser.getAvatarURL());
			builder.addImage(url, 5, 5, new Dimension(192, 192));
			
			builder.addImage(new File("resource/osu_images/Overlayer.png"), 0, 0, builder.getSize());
			
			// CIMA
			Font defaultfont = new Font("Arial", Font.PLAIN, 27);
			
			String accuracy = "";
			String level = "";
			try{
			    accuracy = formatter.format(osuUser.getUser().getAccuracy());
			    level = formatter.format(osuUser.getUser().getLevel());
			  }catch(Exception ex){
			    System.err.println("Erro ao formatar numero: " + ex);
			  }
			accuracy+= "%";
			
			builder.getGraphic().setColor(new Color(0, 137, 255));
			builder.addCentralizedStringY(osuUser.getUsername(), 345, 85, defaultfont);
			builder.addCentralizedStringY(accuracy, 370, 120, defaultfont);
			builder.addCentralizedStringY(osuUser.getUser().getID()+"", 448, 178, defaultfont);
			builder.addCentralizedStringY(osuUser.getUser().getPlayCount()+"", 449, 150, defaultfont);
			
			
			// MEIO
			Font midfont = CustomFont.getFont(CustomFonts.Lane_Cane, Font.PLAIN, 78F);
			builder.getGraphic().setColor(Color.YELLOW);
			builder.addCentralizedString("#" + osuUser.getRank(), 775, 357, midfont);
			builder.addCentralizedStringY("#" + osuUser.getNacionalRank(), 611, 476, 
					midfont.deriveFont(44F));
			
			String pais = osuUser.getPais().getAlpha2();
			
			builder.addImage(new URL("https://osu.ppy.sh/images/flags/" + pais + ".png"), 852, 424, 
					new Dimension(96,65)); builder.addCentralizedString(pais, 900, 509, CustomFont.getFont(CustomFonts.InsanityBurger, Font.PLAIN, 36F));//Bandeira
			
			builder.getGraphic().setColor(new Color(255, 194, 60));
			builder.addCentralizedString(osuUser.getPp()+"", 93, 270, CustomFont.getFont(CustomFonts.InsanityBurger, Font.PLAIN, 56F));
			   //Level
			builder.getGraphic().setColor(Color.YELLOW);
			levelCalculate(builder, level);
			
			// Baixo
			builder.getGraphic().setColor(Color.WHITE);
			builder.addCentralizedStringY(osuUser.getTotalscore()+"", 45, 697, 
					new Font("Arial", Font.PLAIN, 32));
			
			   // Notas
			scoreCalculates(builder, osuUser.getUser().getCountRankSSH(), 445);
			scoreCalculates(builder, osuUser.getUser().getCountRankSS(), 562);
			scoreCalculates(builder, osuUser.getUser().getCountRankSH(), 683);
			scoreCalculates(builder, osuUser.getUser().getCountRankS(), 805);
			scoreCalculates(builder, osuUser.getUser().getCountRankA(), 921);
			
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
			builder.addCentralizedString(l, x, 662, 
					   new Font("Arial", Font.PLAIN, 34));
		} else {
			builder.addCentralizedString(l, x, 662, 
					   new Font("Arial", Font.PLAIN, 38));
		}
	}

}

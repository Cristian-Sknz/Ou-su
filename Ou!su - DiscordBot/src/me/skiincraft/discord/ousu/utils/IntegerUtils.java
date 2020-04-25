package me.skiincraft.discord.ousu.utils;

import java.text.DecimalFormat;

public class IntegerUtils {
	
	public static String getPorcentagem(int valororiginal, int valorsubtraido) {
		
		float v1 = Float.valueOf(valororiginal+"");
		float v2 = Float.valueOf(valorsubtraido+"");
		
		float resultado = (v2*100)/v1;
		
		DecimalFormat decimal = new DecimalFormat("#.0");
		return decimal.format(resultado) + "%";
	}
	
	public static String getPorcentagem(float valororiginal, float valorsubtraido) {
		
		float resultado = (valorsubtraido*100)/valororiginal;
		
		DecimalFormat decimal = new DecimalFormat("#.0");
		return decimal.format(resultado) + "%";
	}

}

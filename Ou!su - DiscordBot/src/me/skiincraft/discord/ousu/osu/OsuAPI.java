package me.skiincraft.discord.ousu.osu;

import com.oopsjpeg.osu4j.backend.Osu;

import me.skiincraft.discord.ousu.OusuBot;

public abstract class OsuAPI {

	private Osu osu = OusuBot.getOusu().getOsu();
	
	
	public OsuAPI() {
		this.osu = OusuBot.getOusu().getOsu();
	}


	public Osu getOsu() {
		return this.osu;
	}

}

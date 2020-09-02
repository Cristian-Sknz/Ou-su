package me.skiincraft.discord.ousu.common;

import me.skiincraft.discord.core.configuration.LanguageManager;

public enum CommandCategory {

	Administracao("Administração"), Ajuda("Ajuda"), Osu("Osu!"), Utilidade("Utilidade"), Sobre("Sobre"), Owner("Dono");

	private String name;

	CommandCategory(String name) {
		this.name = name;
	}

	public String getCategoria() {
		return name;
	}
	
	public String getCategoryName(LanguageManager languageManager) {
		return languageManager.getString("Category", name().toUpperCase());
	}

}

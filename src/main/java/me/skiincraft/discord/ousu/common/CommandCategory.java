package me.skiincraft.discord.ousu.common;

import me.skiincraft.discord.core.configuration.LanguageManager;

public enum CommandCategory {

	Configuration("Configuração"), Gameplay("Gameplay"), Statistics("Estatisticas"), About("Sobre"), Owner("Dono");

	private final String name;

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

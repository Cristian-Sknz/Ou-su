package me.skiincraft.discord.ousu.manager;

import me.skiincraft.discord.ousu.language.LanguageManager.Language;

public enum CommandCategory {

	Administração("Administração"), Ajuda("Ajuda"), Osu("Osu!"), Sobre("Sobre");

	private String name;

	CommandCategory(String name) {
		this.name = name;
	}

	public String getCategoria() {
		return name;
	}
	
	public String getCategoria(Language lang) {
		if (lang == Language.English) {
			if (name.equalsIgnoreCase("Administração")) {
				return "Administration";
			}
			if (name.equalsIgnoreCase("Ajuda")) {
				return "Help";
			}
			if (name.equalsIgnoreCase("Sobre")) {
				return "About";
			}
		}
		return name;
	}


}

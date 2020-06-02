package me.skiincraft.discord.ousu.manager;

import me.skiincraft.discord.ousu.language.LanguageManager.Language;

public enum CommandCategory {

	Administracao("Administração"), Ajuda("Ajuda"), Osu("Osu!"), Utilidade("Utilidade"), Sobre("Sobre"), Owner("Dono");

	private String name;

	CommandCategory(String name) {
		this.name = name;
	}

	public String getCategoria() {
		return name;
	}

	public String getCategoria(Language lang) {
		if (lang == Language.English) {
			if (name.equalsIgnoreCase("Administracao")) {
				return "Administration";
			}
			if (name.equalsIgnoreCase("Ajuda")) {
				return "Help";
			}
			if (name.equalsIgnoreCase("Utilidade")) {
				return "Util";
			}
			if (name.equalsIgnoreCase("Sobre")) {
				return "About";
			}
		}
		return name;
	}

}

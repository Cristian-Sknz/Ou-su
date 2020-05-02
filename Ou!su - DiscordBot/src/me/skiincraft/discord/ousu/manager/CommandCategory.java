package me.skiincraft.discord.ousu.manager;

public enum CommandCategory {

	Administração("Administração"), Ajuda("Ajuda"), Osu("Osu!"), Sobre("Sobre");

	private String name;

	CommandCategory(String name) {
		this.name = name;
	}

	public String getCategoria() {
		return name;
	}

}

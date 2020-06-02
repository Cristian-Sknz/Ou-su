package me.skiincraft.discord.ousu.owneraccess;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.skiincraft.discord.ousu.OusuBot;
import net.dv8tion.jda.api.entities.Guild;

public class ConfigAcess {

	private String databaseName = "`config`";
	private Guild guild;
	private OusuBot Ousu;

	private final String defaultPrefix = "ou!";

	public String getDefaultPrefix() {
		return defaultPrefix;
	}

	public ConfigAcess(Guild guild) {
		this.guild = guild;
		this.Ousu = OusuBot.getOusu();
	}

	public boolean existe() {
		try {
			StringBuffer exists = new StringBuffer();
			exists.append("SELECT * FROM" + databaseName);
			exists.append(" WHERE `MainServer` = ");
			exists.append("'" + guild.getId() + "';");

			ResultSet resultSet = Ousu.getSQL().getConnection().createStatement().executeQuery(exists.toString());

			if (resultSet.next()) {
				return resultSet.getString("MainServer") != null;
			}

			return false;
		} catch (Exception e) {
			Ousu.logger("Não foi possivel verificar se existe " + guild.getId());
			return true;
		}
	}

	public void criar() {
		if (existe()) {
			return;
		}
		try {
			StringBuffer insertinto = new StringBuffer();
			StringBuffer values = new StringBuffer();

			insertinto.append("INSERT INTO " + databaseName);
			insertinto.append("(`MainServer`, ");
			insertinto.append("`Logchannel`, ");
			insertinto.append("`Owner`)");

			values.append(" VALUES");
			values.append("('" + guild.getId() + "', ");
			values.append("'" + 0 + "', ");
			values.append("'" + "247096601242238991" + "');");

			String inserta = insertinto.toString();
			String value = insertinto.toString();

			Ousu.getSQL().getConnection().createStatement().execute(inserta + value);
			return;
		} catch (SQLException e) {
			Ousu.logger("Ocorreu um erro ao criar uma nova tabela.");
			Ousu.logger(e.getMessage());
			return;
		}
	}

	public void deletar() {
		try {
			Ousu.getSQL().getConnection().createStatement()
					.execute("DELETE FROM " + databaseName + "WHERE `MainServer` = '" + guild.getId() + "';");
		} catch (SQLException e) {
			Ousu.logger("Ocorreu um erro ao deletar uma tabela: " + guild.getId());
		}

	}

	public String get(String coluna) {
		if (!existe()) {
			criar();
		}

		try {
			ResultSet resultSet = Ousu.getSQL().getConnection().createStatement()
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `MainServer` = '" + guild.getId() + "';");

			if (resultSet.next()) {
				return resultSet.getString(coluna);
			}
			return null;
		} catch (Exception e) {
			Ousu.logger("Ocorreu um erro ao pegar um valor de uma tabela: " + guild.getId());
			return null;
		}
	}

	public int getInt(String coluna) {
		if (!existe()) {
			criar();
		}

		try {
			ResultSet resultSet = Ousu.getSQL().getConnection().createStatement()
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `MainServer` = '" + guild.getId() + "';");

			if (resultSet.next()) {
				return resultSet.getInt(coluna);
			}
			return 0;
		} catch (Exception e) {
			Ousu.logger("Ocorreu um erro ao pegar um valor inteiro de uma tabela: " + guild.getId());
			return 0;
		}
	}

	public void set(String coluna, String valor) {
		if (!existe()) {
			criar();
		}

		try {
			Ousu.getSQL().getConnection().createStatement().execute("UPDATE " + databaseName + " SET `" + coluna
					+ "` = '" + valor + "' WHERE `MainServer` = '" + guild.getId() + "';");
			return;
		} catch (SQLException e) {
			Ousu.logger("Ocorreu um erro ao setar um valor de uma tabela: " + guild.getId());
			return;
		}
	}

	public void set(String coluna, int valor) {
		if (!existe()) {
			criar();
		}

		try {
			Ousu.getSQL().getConnection().createStatement().execute("UPDATE " + databaseName + " SET `" + coluna
					+ "` = '" + valor + "' WHERE `MainServer` = '" + guild.getId() + "';");
			return;
		} catch (SQLException e) {
			Ousu.logger("Ocorreu um erro ao setar um valor de uma tabela: " + guild.getId());
			return;
		}
	}
}

package me.skiincraft.discord.ousu.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.skiincraft.discord.ousu.OusuBot;
import net.dv8tion.jda.api.entities.Guild;

public class SQLAccess {

	private String databaseName = "`servidores`";
	private Guild guild;
	private OusuBot Ousu;

	private final String defaultPrefix = "ou!";

	public String getDefaultPrefix() {
		return defaultPrefix;
	}

	public SQLAccess(Guild guild) {
		this.guild = guild;
		this.Ousu = OusuBot.getOusu();
	}

	public boolean existe() {
		try {
			ResultSet resultSet = Ousu.getSQL().getConnection().createStatement()
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `guildid` = '" + guild.getId() + "';");

			if (resultSet.next()) {
				return resultSet.getString("guildid") != null;
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
			String date = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(new Date());
			Ousu.getSQL().getConnection().createStatement().execute("INSERT INTO " + databaseName
					+ "(`guildid`, `nome`, `membros`, `prefix`, `adicionado em`, `language`) VALUES" + "('"
					+ guild.getId() + "', " + "'" + guild.getName() + "', " + "'" + guild.getMemberCount() + "', " + "'"
					+ getDefaultPrefix() + "', " + "'" + date + "', " + "'" + generatelang() + "');");
			return;
		} catch (SQLException e) {
			Ousu.logger("Ocorreu um erro ao criar uma nova tabela.");
			Ousu.logger(e.getMessage());
			return;
		}
	}

	private String generatelang() {
		if (guild.getRegionRaw().contains("brazil")) {
			return "Portuguese";
		}
		if (guild.getRegionRaw().contains("us")) {
			return "English";
		}
		return "English";
	}

	public void deletar() {
		try {
			Ousu.getSQL().getConnection().createStatement()
					.execute("DELETE FROM " + databaseName + "WHERE `guildid` = '" + guild.getId() + "';");
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
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `guildid` = '" + guild.getId() + "';");

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
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `guildid` = '" + guild.getId() + "';");

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
					+ "` = '" + valor + "' WHERE `guildid` = '" + guild.getId() + "';");
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
					+ "` = '" + valor + "' WHERE `guildid` = '" + guild.getId() + "';");
			return;
		} catch (SQLException e) {
			Ousu.logger("Ocorreu um erro ao setar um valor de uma tabela: " + guild.getId());
			return;
		}
	}
}

package me.skiincraft.discord.ousu.mysql;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.skiincraft.discord.ousu.OusuBot;
import net.dv8tion.jda.api.entities.Guild;

public class SQLAccess {

	private String databaseName = "`servidores`";
	private String guildId;

	private final String defaultPrefix = "ou!";

	public String getDefaultPrefix() {
		return defaultPrefix;
	}

	public SQLAccess(Guild guild) {
		this.guildId = guild.getId();
	}
	
	public SQLAccess(String guildId) {
		this.guildId = guildId;
	}

	public boolean existe() {
		try {
			ResultSet resultSet = OusuBot.getOusu().getSQL().getConnection().createStatement()
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `guildid` = '" + guildId + "';");

			if (resultSet.next()) {
				return resultSet.getString("guildid") != null;
			}

			return false;
		} catch (Exception e) {
			OusuBot.getOusu().logger("Não foi possivel verificar se existe");
			OusuBot.getOusu().logger(OusuBot.getJda().getGuildById(guildId).getName() + " - " + guildId);
			return true;
		}
	}

	public void criar() {
		if (existe()) {
			return;
		}
		String date = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(new Date());
		Guild guild = OusuBot.getJda().getGuildById(guildId);
		try {
			String guildname = guild.getName().replace("'", "").replace("´", "");
			OusuBot.getOusu().getSQL().getConnection().createStatement()
					.execute("INSERT INTO " + databaseName
							+ "(`guildid`, `nome`, `membros`, `prefix`, `adicionado em`, `language`) VALUES" + "('"
							+ guildId + "', " + "'" + guildname + "', " + "'" + guild.getMemberCount() + "', "
							+ "'" + getDefaultPrefix() + "', " + "'" + date + "', " + "'" + generatelang() + "');");
			return;
		} catch (SQLException e) {
			OusuBot.getOusu().logger("");
			OusuBot.getOusu().logger("Não foi possivel verificar se existe");
			OusuBot.getOusu().logger(guild.getName() + " - " + guildId);
			OusuBot.getOusu().logger("Count: " + guild.getMemberCount() + " | " + getDefaultPrefix());
			OusuBot.getOusu().logger("Data: " + date + " | " + generatelang());
			OusuBot.getOusu().logger(e.getMessage());
			return;
		}
	}

	private String generatelang() {
		if (OusuBot.getJda().getGuildById(guildId).getRegionRaw().contains("brazil")) {
			return "Portuguese";
		}
		if (OusuBot.getJda().getGuildById(guildId).getRegionRaw().contains("us")) {
			return "English";
		}
		return "English";
	}

	public void deletar() {
		try {
			OusuBot.getOusu().getSQL().getConnection().createStatement()
					.execute("DELETE FROM " + databaseName + "WHERE `guildid` = '" + guildId + "';");
		} catch (SQLException e) {
			OusuBot.getOusu().logger("Ocorreu um erro ao deletar uma tabela: " + guildId);
		}

	}

	public String get(String coluna) {
		if (!existe()) {
			criar();
		}

		try {
			ResultSet resultSet = OusuBot.getOusu().getSQL().getConnection().createStatement()
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `guildid` = '" + guildId + "';");

			if (resultSet.next()) {
				return resultSet.getString(coluna);
			}
			return null;
		} catch (Exception e) {
			OusuBot.getOusu().logger("Ocorreu um erro ao pegar um valor de uma tabela: ");
			OusuBot.getOusu().logger(" - " + guildId);
			return null;
		}
	}

	public int getInt(String coluna) {
		if (!existe()) {
			criar();
		}

		try {
			ResultSet resultSet = OusuBot.getOusu().getSQL().getConnection().createStatement()
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `guildid` = '" + guildId + "';");

			if (resultSet.next()) {
				return resultSet.getInt(coluna);
			}
			return 0;
		} catch (Exception e) {
			OusuBot.getOusu().logger("Ocorreu um erro ao pegar um valor(int) de uma tabela: ");
			OusuBot.getOusu().logger(" - " + guildId);
			return 0;
		}
	}

	public void set(String coluna, String valor) {
		if (!existe()) {
			criar();
		}

		try {
			OusuBot.getOusu().getSQL().getConnection().createStatement().execute("UPDATE " + databaseName + " SET `" + coluna
					+ "` = '" + valor + "' WHERE `guildid` = '" + guildId + "';");
			return;
		} catch (SQLException e) {
			OusuBot.getOusu().logger("Ocorreu um erro ao setar um valor de uma tabela: ");
			OusuBot.getOusu().logger(" - " + guildId);
			return;
		}
	}

	public void set(String coluna, int valor) {
		if (!existe()) {
			criar();
		}

		try {
			OusuBot.getOusu().getSQL().getConnection().createStatement().execute("UPDATE " + databaseName + " SET `" + coluna
					+ "` = '" + valor + "' WHERE `guildid` = '" + guildId + "';");
			return;
		} catch (SQLException e) {
			OusuBot.getOusu().logger("Ocorreu um erro ao setar um valor(int) de uma tabela: ");
			OusuBot.getOusu().logger(OusuBot.getJda().getGuildById(guildId)+ " - " + guildId);
			return;
		}
	}

	public Map<String, String> getOrderBy(String colunm, int limit, boolean desc) {
		try {
			String d;
			if (desc == false) {
				d = "` ";
			} else {
				d = "` DESC ";
			}
			// SELECT * FROM `servidores` GROUP BY `ID` ORDER BY `adicionado em` DESC LIMIT
			// 5; esse era teste*
			Map<String, String> map = new HashMap<String, String>();
			ResultSet result = OusuBot.getOusu().getSQL().getConnection().prepareStatement(
					"SELECT * FROM `" + databaseName + "` GROUP BY `ID` ORDER BY `" + colunm + d + "LIMIT " + limit)
					.executeQuery();
			do {
				if (!result.next()) {
					result.close();
					return map;
				}
				map.put(result.getString("guildid"), result.getString(colunm));
			} while (true);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}

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
			ResultSet resultSet = OusuBot.getSQL().getOusuStatement()
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `guildid` = '" + guildId + "';");

			if (resultSet.next()) {
				return resultSet.getString("guildid") != null;
			}
			return false;
		} catch (Exception e) {
			OusuBot.getOusu().logger("Não foi possivel verificar se existe");
			OusuBot.getOusu().logger(OusuBot.getShardmanager().getGuildById(guildId).getName() + " - " + guildId);
			return true;
		} finally {
			fechar();
		}
	}

	public void criar() {
		if (existe()) {
			return;
		}
		String date = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(new Date());
		Guild guild = OusuBot.getShardmanager().getGuildById(guildId);
		try {
			String guildname = guild.getName().replace("'", "").replace("´", "");
			OusuBot.getSQL().getOusuStatement()
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
		} finally {
			fechar();
		}
		
	}

	private String generatelang() {
		if (OusuBot.getShardmanager().getGuildById(guildId).getRegionRaw().contains("brazil")) {
			return "Portuguese";
		}
		if (OusuBot.getShardmanager().getGuildById(guildId).getRegionRaw().contains("us")) {
			return "English";
		}
		return "English";
	}

	public void deletar() {
		try {
			OusuBot.getSQL().getOusuStatement()
					.execute("DELETE FROM " + databaseName + "WHERE `guildid` = '" + guildId + "';");
		} catch (SQLException e) {
			OusuBot.getOusu().logger("Ocorreu um erro ao deletar uma tabela: " + guildId);
		} finally {
			fechar();
		}
	}

	public String get(String coluna) {
		if (!existe()) {
			criar();
		}

		try {
			ResultSet resultSet = OusuBot.getSQL().getOusuStatement()
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `guildid` = '" + guildId + "';");

			if (resultSet.next()) {
				return resultSet.getString(coluna);
			}
			return null;
		} catch (Exception e) {
			OusuBot.getOusu().logger("Ocorreu um erro ao pegar um valor de uma tabela: ");
			OusuBot.getOusu().logger(" - " + guildId);
			return null;
		} finally {
			fechar();
		}
	}

	public int getInt(String coluna) {
		if (!existe()) {
			criar();
		}

		try {
			ResultSet resultSet = OusuBot.getSQL().getOusuStatement()
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `guildid` = '" + guildId + "';");

			if (resultSet.next()) {
				return resultSet.getInt(coluna);
			}
			return 0;
		} catch (Exception e) {
			OusuBot.getOusu().logger("Ocorreu um erro ao pegar um valor(int) de uma tabela: ");
			OusuBot.getOusu().logger(" - " + guildId);
			return 0;
		} finally {
			fechar();
		}
	}

	public void set(String coluna, String valor) {
		if (!existe()) {
			criar();
		}

		try {
			OusuBot.getSQL().getOusuStatement().execute("UPDATE " + databaseName + " SET `" + coluna
					+ "` = '" + valor + "' WHERE `guildid` = '" + guildId + "';");
			return;
		} catch (SQLException e) {
			OusuBot.getOusu().logger("Ocorreu um erro ao setar um valor de uma tabela: ");
			OusuBot.getOusu().logger(" - " + guildId);
			return;
		} finally {
			fechar();
		}
	}

	public void set(String coluna, int valor) {
		if (!existe()) {
			criar();
		}

		try {
			OusuBot.getSQL().getOusuStatement().execute("UPDATE " + databaseName + " SET `" + coluna
					+ "` = '" + valor + "' WHERE `guildid` = '" + guildId + "';");
			return;
		} catch (SQLException e) {
			OusuBot.getOusu().logger("Ocorreu um erro ao setar um valor(int) de uma tabela: ");
			OusuBot.getOusu().logger(OusuBot.getShardmanager().getGuildById(guildId)+ " - " + guildId);
			return;
		} finally {
			fechar();
		}
	}

	public Map<String, String> getOrderBy(String colunm, int limit, boolean desc) {
		ResultSet result = null;
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
			result = OusuBot.getSQL().getConnection().prepareStatement(
					"SELECT * FROM `" + databaseName + "` GROUP BY `ID` ORDER BY `" + colunm + d + "LIMIT " + limit)
					.executeQuery();
			do {
				if (!result.next()) {
					result.getStatement().close();
					result.close();
					return map;
				}
				map.put(result.getString("guildid"), result.getString(colunm));
			} while (true);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				result.getStatement().close();
			} catch (SQLException e) {
				//e.printStackTrace();
			}
		}
	}
	
	private void fechar() {
		try {
			OusuBot.getSQL().getOusuStatement().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

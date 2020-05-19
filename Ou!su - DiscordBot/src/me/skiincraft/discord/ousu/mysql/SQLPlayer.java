package me.skiincraft.discord.ousu.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import me.skiincraft.discord.ousu.OusuBot;
import net.dv8tion.jda.api.entities.User;

public class SQLPlayer {

	private static String databaseName = "`users`";
	private User user;
	private OusuBot Ousu;

	private final String defaultPrefix = "ou!";

	public String getDefaultPrefix() {
		return defaultPrefix;
	}

	public SQLPlayer(User user) {
		this.user = user;
		this.Ousu = OusuBot.getOusu();
	}

	public boolean existe() {
		try {
			ResultSet resultSet = Ousu.getSQL().getConnection().createStatement()
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `userid` = '" + user.getId() + "';");

			if (resultSet.next()) {
				return resultSet.getString("userid") != null;
			}

			return false;
		} catch (Exception e) {
			Ousu.logger("Não foi possivel verificar se existe " + user.getId());
			return true;
		}
	}

	public void criar() {
		if (existe()) {
			return;
		}

		try {
			String name = user.getName().replace("´", "").replace("'", "");
			Ousu.getSQL().getConnection().createStatement()
					.execute("INSERT INTO " + databaseName + "(`userid`, `username`, `osu_account`) VALUES" + "('"
							+ user.getId() + "', " + "'" + name + "#" + user.getDiscriminator() + "', " + "'" + "none"
							+ "');");
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
					.execute("DELETE FROM " + databaseName + "WHERE `userid` = '" + user.getId() + "';");
		} catch (SQLException e) {
			Ousu.logger("Ocorreu um erro ao deletar uma tabela: " + user.getId());
		}

	}

	public String get(String coluna) {
		if (!existe()) {
			criar();
		}

		try {
			ResultSet resultSet = Ousu.getSQL().getConnection().createStatement()
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `userid` = '" + user.getId() + "';");

			if (resultSet.next()) {
				return resultSet.getString(coluna);
			}
			return null;
		} catch (Exception e) {
			Ousu.logger("Ocorreu um erro ao pegar um valor de uma tabela: " + user.getId());
			return null;
		}
	}

	public int getInt(String coluna) {
		if (!existe()) {
			criar();
		}

		try {
			ResultSet resultSet = Ousu.getSQL().getConnection().createStatement()
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `userid` = '" + user.getId() + "';");

			if (resultSet.next()) {
				return resultSet.getInt(coluna);
			}
			return 0;
		} catch (Exception e) {
			Ousu.logger("Ocorreu um erro ao pegar um valor inteiro de uma tabela: " + user.getId());
			return 0;
		}
	}

	public void set(String coluna, String valor) {
		if (!existe()) {
			criar();
		}

		try {
			Ousu.getSQL().getConnection().createStatement().execute("UPDATE " + databaseName + " SET `" + coluna
					+ "` = '" + valor + "' WHERE `userid` = '" + user.getId() + "';");
			return;
		} catch (SQLException e) {
			Ousu.logger("Ocorreu um erro ao setar um valor de uma tabela: " + user.getId());
			return;
		}
	}

	public void set(String coluna, int valor) {
		if (!existe()) {
			criar();
		}

		try {
			Ousu.getSQL().getConnection().createStatement().execute("UPDATE " + databaseName + " SET `" + coluna
					+ "` = '" + valor + "' WHERE `userid` = '" + user.getId() + "';");
			return;
		} catch (SQLException e) {
			Ousu.logger("Ocorreu um erro ao setar um valor de uma tabela: " + user.getId());
			return;
		}
	}

	public static Map<String, String> getOrderBy(String colunm, int limit, boolean desc) {
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
					"SELECT * FROM " + databaseName + " GROUP BY `ID` ORDER BY `" + colunm + d + "LIMIT " + limit)
					.executeQuery();
			do {
				if (!result.next()) {
					result.close();
					return map;
				}
				map.put(result.getString("userid"), result.getString(colunm));
			} while (true);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}

package me.skiincraft.discord.ousu.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.skiincraft.api.ousu.users.User;
import me.skiincraft.discord.ousu.OusuBot;

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
			ResultSet resultSet = OusuBot.getSQL().getOusuStatement()
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `userid` = '" + user.getUserID() + "';");

			if (resultSet.next()) {
				return resultSet.getString("userid") != null;
			}

			return false;
		} catch (Exception e) {
			Ousu.logger("Não foi possivel verificar se existe " + user.getUserID());
			return true;
		} finally {
			fechar();
		}
	}

	public void criar() {
		if (existe()) {
			return;
		}

		try {
			String name = user.getUserName().replace("´", "").replace("'", "");
			OusuBot.getSQL().getOusuStatement()
					.execute("INSERT INTO " + databaseName + "(`userid`, `username`, `lastpp`, `lastscore`) VALUES" + "('"
							+ user.getUserID() + "', "
							+ "'" + name +  "', "
							+ "'" + user.getPP()+ "&" + user.getPP() + "', "
							+ "'" + user.getTotalScore() + "&" + user.getTotalScore()
							+ "');");
			return;
		} catch (SQLException e) {
			Ousu.logger("Ocorreu um erro ao criar uma nova tabela.");
			Ousu.logger(e.getMessage());
			return;
		} finally {
			fechar();
		}
	}

	public void deletar() {
		try {
			OusuBot.getSQL().getOusuStatement()
					.execute("DELETE FROM " + databaseName + "WHERE `userid` = '" + user.getUserID() + "';");
		} catch (SQLException e) {
			Ousu.logger("Ocorreu um erro ao deletar uma tabela: " + user.getUserID());
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
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `userid` = '" + user.getUserID() + "';");

			if (resultSet.next()) {
				return resultSet.getString(coluna);
			}
			return null;
		} catch (Exception e) {
			Ousu.logger("Ocorreu um erro ao pegar um valor de uma tabela: " + user.getUserID());
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
					.executeQuery("SELECT * FROM " + databaseName + " WHERE `userid` = '" + user.getUserID() + "';");

			if (resultSet.next()) {
				return resultSet.getInt(coluna);
			}
			return 0;
		} catch (Exception e) {
			Ousu.logger("Ocorreu um erro ao pegar um valor inteiro de uma tabela: " + user.getUserID());
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
					+ "` = '" + valor + "' WHERE `userid` = '" + user.getUserID() + "';");
			return;
		} catch (SQLException e) {
			Ousu.logger("Ocorreu um erro ao setar um valor de uma tabela: " + user.getUserID());
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
					+ "` = '" + valor + "' WHERE `userid` = '" + user.getUserID() + "';");
			return;
		} catch (SQLException e) {
			Ousu.logger("Ocorreu um erro ao setar um valor de uma tabela: " + user.getUserID());
			return;
		} finally {
			fechar();
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

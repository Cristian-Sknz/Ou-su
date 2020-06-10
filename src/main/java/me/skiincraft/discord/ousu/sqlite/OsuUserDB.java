package me.skiincraft.discord.ousu.sqlite;

import java.sql.ResultSet;
import java.sql.SQLException;
import me.skiincraft.api.ousu.users.User;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.utils.StringUtils;

public class OsuUserDB {

	private static String databaseName = "`users`";
	private User user;
	
	private boolean lastboolean;
	private String laststring;
	private int lastint;
	
	private void setLast(boolean bool) {
		lastboolean = bool;
	}
	private void setLast(String string) {
		laststring = string;
	}
	private void setLast(int integer) {
		lastint = integer;
	}
	private boolean getLastBoolean() {
		return lastboolean;
	}
	private String getLastString() {
		return laststring;
	}
	private int getLastInteger() {
		return lastint;
	}
	
	public OsuUserDB(User user) {
		this.user = user;
	}
	
	public boolean exists() {
		OusuBot.getSQL().executeStatementTask(statement -> {
			try {
				StringBuffer buffer = new StringBuffer();
				buffer.append("SELECT * FROM " + databaseName + " WHERE ");
				buffer.append("`userid` = '" + user.getUserID() + "';");
				ResultSet result = statement.executeQuery(buffer.toString());
				
				setLast((result.next()) ? result.getString("userid") != null
						: false);
			} catch (SQLException e) {
				setLast(false);
				OusuBot.getOusu().logger("| Não foi possivel verificar se uma tabela (osuuser) existe.");
				OusuBot.getOusu().logger("|" + user.getUserID() + " - " + user.getUserName());					
				e.printStackTrace();
			}
		});
	return getLastBoolean();
}
	public void create() {
		if (exists()) {
			return;
		}
		OusuBot.getSQL().executeStatementTask(statement -> {
		try {
			String insert = StringUtils.insertBuild("userid", "username", "lastpp", "lastscore");
			String values = StringUtils.selectBuild(user.getUserID()+"", user.getUserName(),
					+ user.getPP()+ "&" + user.getPP(), user.getTotalScore() + "&" + user.getTotalScore());
			
			statement.execute("INSERT INTO " + databaseName + insert + " VALUES" + values + ";");
		} catch (SQLException e) {
			e.printStackTrace();
			OusuBot.getOusu().logger("");
			OusuBot.getOusu().logger("Não foi possivel criar tabelas");
			OusuBot.getOusu().logger(user.getUserName() + " - " + user.getUserID());
			OusuBot.getOusu().logger(e.getMessage());
		}
		});
	}
	public void delete() {
		if (!exists()) {
			return;
		}
		OusuBot.getSQL().executeStatementTask(statement ->{
			try {
				statement.execute("DELETE FROM " + databaseName + "WHERE `userid` = '" + user.getUserID() + "';");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}
	
	public String get(String coluna) {
		if (!exists()) {
			create();
		}
		OusuBot.getSQL().executeStatementTask(statement ->{
			try {
				ResultSet result = statement.executeQuery("SELECT * FROM " + databaseName + " WHERE `userid` = '" + user.getUserID() + "';");
				setLast((result.next())? result.getString(coluna) : null);
			} catch (SQLException e) {
				OusuBot.getOusu().logger("Ocorreu um erro ao pegar um valor de uma tabela: ");
				OusuBot.getOusu().logger(user.getUserName() + " - " + user.getUserID());
			}
		});
		return getLastString();
	}

	public int getInt(String coluna) {
		if (!exists()) {
			create();
		}
		OusuBot.getSQL().executeStatementTask(statement ->{
			try {
				ResultSet result = statement.executeQuery("SELECT * FROM " + databaseName + " WHERE `userid` = '" + user.getUserID() + "';");
				setLast((result.next())? (int) result.getInt(coluna) : 0);
			} catch (SQLException e) {
				OusuBot.getOusu().logger("Ocorreu um erro ao pegar um valor de uma tabela: ");
				OusuBot.getOusu().logger(user.getUserName() + " - " + user.getUserID());
			}
		});
		return getLastInteger();
	}
	
	public void set(String coluna, String valor) {
		if (!exists()) {
			create();
		}
		OusuBot.getSQL().executeStatementTask(statement -> {
			try {
				statement.execute("UPDATE " + databaseName + " SET `" + coluna + "` = '" + valor + "' WHERE `userid` = '" + user.getUserID() + "';");
				return;
			} catch (SQLException e) {
				OusuBot.getOusu().logger("Ocorreu um erro ao setar um valor de uma tabela: ");
				OusuBot.getOusu().logger(user.getUserName() + " - " + user.getUserID());
			}
		});
	}
	
	public void set(String coluna, int valor) {
		set(coluna, valor+"");
	}
	
	public void set(String coluna, long valor) {
		set(coluna, valor+"");
	}
}

package me.skiincraft.discord.ousu.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.logging.Level;

import me.skiincraft.discord.ousu.OusuBot;

public class SQLite {

	public OusuBot getOusu() {
		return ousu;
	}

	private Connection connection;
	private Statement statement;
	private OusuBot ousu;

	private String host, database, user, password;
	private int port;

	public String getHost() {
		return host;
	}

	public String getDatabase() {
		return database;
	}

	public String getUser() {
		return user;
	}

	public int getPort() {
		return port;
	}

	public String getPassword() {
		return password;
	}

	public Connection getConnection() {
		return connection;
	}

	public Statement getStatement() {
		return statement;
	}

	// Atualizado para SQLITE.
	public SQLite(OusuBot main) {
		this.ousu = main;
	}

	public synchronized void abrir() {
		try {
			if (connection != null) {
				if (!connection.isClosed()) {
					return;
				}
			}
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:sqlite:banco_de_dados/banco_sqlite.db";
			this.connection = DriverManager.getConnection(url);

			this.statement = this.connection.createStatement();
			ousu.logger("Conexao com o banco de dados estabelecida com sucesso.", Level.CONFIG);
			ousu.setDBSQL(true);
			return;
		} catch (ClassNotFoundException exception) {
			exception.printStackTrace();
			ousu.logger("Driver n�o encontrado");
			ousu.setDBSQL(false);
			return;
		} catch (SQLClientInfoException exception) {
			exception.printStackTrace();
			ousu.logger("Usuario e/ou senha incorreto(s).", Level.SEVERE);
			ousu.setDBSQL(false);
			return;
		} catch (SQLTimeoutException exception) {
			exception.printStackTrace();
			ousu.logger("Tempo de conexao excedido.", Level.SEVERE);
			ousu.setDBSQL(false);
			return;
		} catch (SQLException exception) {
			exception.printStackTrace();
			ousu.logger("Não foi possivel achar a database, tente criar manualmente.", Level.SEVERE);
			ousu.setDBSQL(false);
			return;
		}
	}

	public synchronized void close() {
		if (this.connection == null) {
			return;
		}
		try {
			this.connection.close();
			if (this.statement != null) {
				this.statement.close();
			}
			return;
		} catch (SQLException exception) {
			exception.printStackTrace();
			return;
		}
	}

	public synchronized void execute(String command) {
		if (this.connection == null || this.statement == null) {
			ousu.logger("Dados -[Connection/Statement]- não estão nulos. (execute())", Level.SEVERE);
			return;
		}
		try {
			this.statement.execute(command);
		} catch (SQLException exception) {
			exception.printStackTrace();
			return;
		}
	}

	public synchronized ResultSet resultSet(String query) {
		if (this.connection == null || this.statement == null) {
			ousu.logger("Dados -[Connection/Statement]- n�o est�o nulos. (resultSet())", Level.SEVERE);
			System.exit(0);
			return null;
		}
		try {
			return this.statement.executeQuery(query);
		} catch (SQLException exception) {
			exception.printStackTrace();
			return null;
		}
	}
	
	public Statement getOusuStatement() {
		if (statement != null && connection != null) {
			try {
				if (statement.isClosed()) {
					statement = connection.createStatement();
					return statement;
				}
				return statement;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return statement;	
		}
		abrir();
		return statement;
	}

	public synchronized void executeUpdateAsync(String update) {
		if (this.connection == null || this.statement == null) {
			ousu.logger("Dados -[Connection/Statement]- n�o est�o nulos. (executeUpdateAsync())", Level.SEVERE);
			System.exit(0);
			return;
		}
		try {
			Statement s = connection.createStatement();
			s.executeUpdate(update);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String createtableString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE TABLE IF NOT EXISTS ");
		buffer.append("`servidores` ");
		buffer.append("(ID INTEGER PRIMARY KEY AUTOINCREMENT,");
		buffer.append("`guildid` VARCHAR(64) NOT NULL, ");
		buffer.append("`nome` VARCHAR(64) NOT NULL, ");
		buffer.append("`membros` INT, ");
		buffer.append("`prefix` VARCHAR(10) NOT NULL, ");
		buffer.append("`adicionado em` VARCHAR(24) NOT NULL, ");
		buffer.append("`language` VARCHAR(24) NOT NULL");
		buffer.append(");");

		return buffer.toString();
	}

	String createtablePlayerString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE TABLE IF NOT EXISTS ");
		buffer.append("`users` ");
		buffer.append("(ID INTEGER PRIMARY KEY AUTOINCREMENT,");
		buffer.append("`userid` VARCHAR(64) NOT NULL, ");
		buffer.append("`username` VARCHAR(64) NOT NULL, ");
		buffer.append("`lastpp` VARCHAR(64) NOT NULL, ");
		buffer.append("`lastscore` VARCHAR(64) NOT NULL");
		buffer.append(");");

		return buffer.toString();
	}

	public String createConfigString() throws SQLException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE TABLE IF NOT EXISTS ");
		buffer.append("`config` ");
		buffer.append("(ID INTEGER PRIMARY KEY AUTOINCREMENT,");
		buffer.append("`MainServer` VARCHAR(64) NOT NULL, ");
		buffer.append("`Logchannel` VARCHAR(64) NOT NULL, ");
		buffer.append("`Owner` VARCHAR(64) NOT NULL");
		buffer.append(");");
		return buffer.toString();
	}

	public synchronized void setup() {
		if (this.connection == null || this.statement == null) {
			ousu.logger("Dados -[Connection/Statement]- são nulos. (setup())", Level.SEVERE);
			ousu.setDBSQL(false);
			return;
		}
		try {

			this.statement.execute(createtableString());
			this.statement.execute(createtablePlayerString());
			this.statement.execute(createConfigString());

			createConfigString();
			ousu.setDBSQL(true);
		} catch (SQLException exception) {
			exception.printStackTrace();
			ousu.setDBSQL(false);
		}
	}

}

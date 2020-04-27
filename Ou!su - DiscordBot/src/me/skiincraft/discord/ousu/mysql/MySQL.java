package me.skiincraft.discord.ousu.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;

import me.skiincraft.discord.ousu.OusuBot;

public class MySQL {

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
	// Por enquanto não há problema em deixar isso publico.
	public MySQL(OusuBot main) {
		this.ousu = main;
		this.host = "localhost";//IP
		this.database = "ousubot";
		this.port = 3306;
		this.user = "ousu";
		this.password = "123";
	}

	public boolean isNull() {
		return this.host == null || this.database == null || this.user == null || this.password == null;
	}

	public synchronized void abrir() {
		if (!ousu.isDBSQL()) {
			ousu.setDBSQL(false);
			return;
		}
		
		if (isNull()) {
			ousu.logger("Dados do MYSQL não foram preenchidos.");
			ousu.setDBSQL(false);
			return;
		}
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection(
					"jdbc:mysql://" + this.host + ":" + 3306 + "/" + this.database + "?autoReconnect=true", this.user,
					this.password);
			
			this.statement = this.connection.createStatement();
			ousu.logger("Conexao com o banco de dados estabelecida com sucesso.");
			ousu.setDBSQL(true);
			return;
		} catch (ClassNotFoundException exception) {
			exception.printStackTrace();
			ousu.logger("Driver n�o encontrado");
			ousu.setDBSQL(false);
			return;
		} catch (SQLClientInfoException exception) {
			exception.printStackTrace();
			ousu.logger("Usuario e/ou senha incorreto(s).");
			ousu.setDBSQL(false);
			return;
		} catch (SQLTimeoutException exception) {
			exception.printStackTrace();
			ousu.logger("Tempo de conexao excedido.");
			ousu.setDBSQL(false);
			return;
		} catch (SQLException exception) {
			exception.printStackTrace();
			ousu.logger("Não foi possivel achar a database, tente criar manualmente.");
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
			ousu.logger("Dados -[Connection/Statement]- não estão nulos. (execute())");
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
			ousu.logger("Dados -[Connection/Statement]- n�o est�o nulos. (resultSet())");
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

	public synchronized void executeUpdateAsync(String update) {
		if (this.connection == null || this.statement == null) {
			ousu.logger("Dados -[Connection/Statement]- n�o est�o nulos. (executeUpdateAsync())");
			System.exit(0);
			return;
		}
		try {
			Statement s = getConnection().createStatement();
			s.executeUpdate(update);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void setup() {
		if (this.connection == null || this.statement == null) {
			ousu.logger("Dados -[Connection/Statement]- são nulos. (setup())");
			ousu.setDBSQL(false);
			return;
		}
		try {
			this.statement.execute("CREATE TABLE IF NOT EXISTS `" + this.database + 
					"`.`servidores` (`id` INT UNSIGNED NOT NULL AUTO_INCREMENT, `guildid` VARCHAR(64) NOT NULL, `nome` VARCHAR(64) NOT NULL, `membros` INT, `prefix` VARCHAR(24) NOT NULL, `adicionado em` VARCHAR(24) NOT NULL, PRIMARY KEY(`id`));");
			//Tabelas(GuildID, Nome, Membros, Prefix, Adicionado em);
			this.statement.execute("CREATE TABLE IF NOT EXISTS `" + this.database + 
					"`.`usuarios` (`id` INT UNSIGNED NOT NULL AUTO_INCREMENT, `userid` VARCHAR(64) NOT NULL, `username` VARCHAR(64) NOT NULL, `osu_account` VARCHAR(64) NOT NULL, PRIMARY KEY(`id`));");
			//Tabelas(UserID, Username, Osu_Account);
			ousu.setDBSQL(true);
		} catch (SQLException exception) {
			exception.printStackTrace();
			ousu.setDBSQL(false);
		}
	}

}

package me.skiincraft.discord.ousu.sqlite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.entities.Guild;

public class GuildsDB {

	private String databaseName = "`servidores`";
	private String guildId;
	
	private final String defaultPrefix = "ou!";

	private Map<String, String> lastmap;
	private String laststring;
	private int lastint;
	
	public String getDefaultPrefix() {
		return defaultPrefix;
	}

	public GuildsDB(Guild guild) {
		this.guildId = guild.getId();
	}
	
	public GuildsDB(String guildId) {
		this.guildId = guildId;
	}
	private void setLast(String string) {
		laststring = string;
	}
	private void setLast(Map<String, String> map) {
		lastmap = map;
	}
	private void setLast(int integer) {
		lastint = integer;
	}
	private String getLastString() {
		return laststring;
	}
	private int getLastInteger() {
		return lastint;
	}
	
	private Map<String, String> getLastMap() {
		return lastmap;
	}
	
	public boolean exists() {
		List<Boolean> booleans = new ArrayList<Boolean>();
			OusuBot.getSQL().executeStatementTask(statement -> {
				try {
					StringBuffer buffer = new StringBuffer();
					buffer.append("SELECT * FROM " + databaseName + " WHERE ");
					buffer.append("`guildid` = '" + guildId + "';");
					ResultSet result = statement.executeQuery(buffer.toString());
					
					booleans.add((result.next()) ? result.getString("guildid") != null
							: false);
				} catch (SQLException e) {
					booleans.add(false);
					OusuBot.getOusu().logger("| Não foi possivel verificar se uma tabela existe.");
					OusuBot.getOusu().logger("|" + OusuBot.getShardmanager().getGuildById(guildId).getName() + " - " + guildId);					
					e.printStackTrace();
				}
			});
			while (booleans.size() == 0) {
				try {
					Thread.sleep(200L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		return booleans.get(0);
	}
	
	public void create() {
		if (exists()) {
			return;
		}
		Guild guild = OusuBot.getShardmanager().getGuildById(guildId);
		Date newDate = new Date(TimeUnit.SECONDS.toMillis(guild.getSelfMember().getTimeJoined().toEpochSecond()));
		String simple = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(newDate);
		OusuBot.getSQL().executeStatementTask(statement -> {
		try {
			String guildname = guild.getName().replace("'", "").replace("´", "");
			String insert = StringUtils.insertBuild("guildid", "nome", "membros", "prefix", "adicionado em", "language");
			String values = StringUtils.selectBuild(guildId, guildname, guild.getMemberCount() + "", getDefaultPrefix(),
					simple, generatelang());
			
			statement.execute("INSERT INTO " + databaseName + insert + " VALUES" + values + ";");
		} catch (SQLException e) {
			e.printStackTrace();
			OusuBot.getOusu().logger("");
			OusuBot.getOusu().logger("Não foi possivel criar tabelas");
			OusuBot.getOusu().logger(guild.getName() + " - " + guildId);
			OusuBot.getOusu().logger("Count: " + guild.getMemberCount() + " | " + getDefaultPrefix());
			OusuBot.getOusu().logger(e.getMessage());
			
		}
		});
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

	public void delete() {
		OusuBot.getSQL().executeStatementTask(statement ->{
			try {
				statement.execute("DELETE FROM " + databaseName + "WHERE `guildid` = '" + guildId + "';");
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
				ResultSet result = statement.executeQuery("SELECT * FROM " + databaseName + " WHERE `guildid` = '" + guildId + "';");
				setLast((result.next())? result.getString(coluna) : null);
			} catch (SQLException e) {
				OusuBot.getOusu().logger("Ocorreu um erro ao pegar um valor de uma tabela: ");
				OusuBot.getOusu().logger(OusuBot.getGuildById(guildId).getName() + " - " + guildId);
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
				ResultSet result = statement.executeQuery("SELECT * FROM " + databaseName + " WHERE `guildid` = '" + guildId + "';");
				setLast((result.next())? (int) result.getInt(coluna) : 0);
			} catch (SQLException e) {
				OusuBot.getOusu().logger("Ocorreu um erro ao pegar um valor de uma tabela: ");
				OusuBot.getOusu().logger(OusuBot.getGuildById(guildId).getName() + " - " + guildId);
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
				statement.execute("UPDATE " + databaseName + " SET `" + coluna + "` = '" + valor + "' WHERE `guildid` = '" + guildId + "';");
				return;
			} catch (SQLException e) {
				OusuBot.getOusu().logger("Ocorreu um erro ao setar um valor de uma tabela: ");
				OusuBot.getOusu().logger(OusuBot.getGuildById(guildId).getName() + " - " + guildId);
			}
		});
	}
	public void set(String coluna, int valor) {
		set(coluna, valor+"");
	}
	
	public void set(String coluna, long valor) {
		set(coluna, valor+"");
	}
	
	public Map<String, String> getOrderBy(String colunm, int limit, boolean desc) {
		String orderby = (desc)? "` DESC ": "` ";
		
		OusuBot.getSQL().executePrepareStatementTask("SELECT * FROM `" + databaseName + "` GROUP BY `ID` ORDER BY `" + colunm +
				orderby + "LIMIT " + limit, statement -> {
					Map<String, String> map = new HashMap<>();
					try {
						ResultSet result = statement.getResultSet();
						
						do {
							if (!result.next()) {
								setLast(map);
								return;
							}
							map.put(result.getString("guildid"), result.getString(colunm));
						} while (true);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					
				});
		return getLastMap();
	}}

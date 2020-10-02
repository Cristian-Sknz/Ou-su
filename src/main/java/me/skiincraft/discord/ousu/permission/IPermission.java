package me.skiincraft.discord.ousu.permission;

import java.util.ArrayList;
import java.util.List;

import me.skiincraft.discord.core.sqlobjects.AccessTable;
import me.skiincraft.discord.core.sqlobjects.Table;
import me.skiincraft.discord.core.sqlobjects.Table.Column.ColumnType;
import me.skiincraft.discord.ousu.OusuBot;

import net.dv8tion.jda.api.entities.User;

public class IPermission extends Table {
	
	private User user;
	
	public IPermission(User user) {
		super("internalpermission", OusuBot.getMain().getPlugin());
		this.user = user;
	}

	public enum InternalPermission {
		ALL(3), WRITE_AND_VIEW(2), VIEW(1);
		
		private int value;
		
		InternalPermission(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}

	public AccessTable accessTable() {
		return new AccessTable("userId", user.getId());
	}

	public List<Column> columns() {
		List<Column> columns = new ArrayList<>();
		columns.add(new Column("userId", ColumnType.LONG, 60, user.getIdLong()));
		columns.add(new Column("permission", ColumnType.INTEGER, 60, user.getIdLong()));
		return columns;
	}

}

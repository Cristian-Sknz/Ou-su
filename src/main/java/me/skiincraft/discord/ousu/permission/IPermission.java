package me.skiincraft.discord.ousu.permission;

import me.skiincraft.discord.core.sqlobjects.Table;
import me.skiincraft.discord.core.sqlobjects.Table.Column.ColumnType;
import me.skiincraft.discord.core.sqlobjects.TableReference;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public class IPermission extends Table {
	
	private final User user;
	
	public IPermission(User user) {
		super("internalpermission");
		this.user = user;
	}

	public enum InternalPermission {
		ALL(3), WRITE_AND_VIEW(2), VIEW(1);
		
		private final int value;
		
		InternalPermission(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}


	@Override
	public TableReference reference() {
		return new TableReference("userId", user.getId());
	}

	public List<Column> columns() {
		List<Column> columns = new ArrayList<>();
		columns.add(new Column("userId", ColumnType.LONG, 60, user.getIdLong()));
		columns.add(new Column("permission", ColumnType.INTEGER, 60, user.getIdLong()));
		return columns;
	}

}

package me.skiincraft.discord.ousu.events;

import me.skiincraft.discord.ousu.manager.ReactionUtils;

public class DoubleReaction extends ReactionUtils {

	private Object segundo;
	private int value2;
	
	public DoubleReaction(String userid, String messageID, Object principal, Object segundo, int ordem, int ordem2) {
		super(userid, messageID, principal, ordem);		this.setObject2(segundo); this.setValue2(ordem2);
	}

	public Object getObject2() {
		return segundo;
	}

	public void setObject2(Object segundo) {
		this.segundo = segundo;
	}

	public int getValue2() {
		return value2;
	}

	public void setValue2(int value2) {
		this.value2 = value2;
	}
	

}

package me.skiincraft.discord.ousu.richpresence;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.User;

public class PresenceGetter {

	private Guild guild;
	private String presence;
	private boolean isId;

	public PresenceGetter(Guild guild, String presence, boolean isId) {
		this.presence = presence;
		this.guild = guild;
		this.isId = isId;
	}

	public List<User> getPlayers() {
		List<User> j = new ArrayList<User>();

		for (Member m : guild.getMembers()) {
			User u = m.getUser();
			for (Activity act : m.getActivities()) {
				if (act.isRich()) {
					RichPresence rich = act.asRichPresence();
					if (isId) {
						if (rich.getApplicationId().equalsIgnoreCase(presence)) {
							j.add(u);
						}
					} else {
						if (rich.getName().contains(presence)) {
							j.add(u);
						}
					}
				}
			}
		}
		return j;
	}

	public List<Member> getPlayersAtPresence() {
		List<Member> j = new ArrayList<Member>();
		for (Member m : guild.getMembers()) {
			Member u = m;
			for (Activity act : m.getActivities()) {
				if (act.isRich()) {
					RichPresence rich = act.asRichPresence();
					if (isId) {
						if (rich.getApplicationId().equalsIgnoreCase(presence)) {
							j.add(u);
						}
					} else {
						if (rich.getName().contains(presence)) {
							j.add(u);
						}
					}
				}
			}
		}
		return j;
	}

	public List<Rich> getRichPresences() {
		List<Rich> pre = new ArrayList<Rich>();
		List<Member> lista = getPlayersAtPresence();
		if (lista.size() == 0) {
			return null;
		}

		for (Member membros : lista) {
			User u = membros.getUser();
			for (Activity act : membros.getActivities()) {
				if (act.isRich()) {
					RichPresence rich = act.asRichPresence();
					if (isId) {
						if (rich.getApplicationId().equalsIgnoreCase(presence)) {
							pre.add(new Rich(u, rich));
						}
					} else {
						if (rich.getName().contains(presence)) {
							pre.add(new Rich(u, rich));
						}
					}
				}
			}
		}
		return pre;
	}

}

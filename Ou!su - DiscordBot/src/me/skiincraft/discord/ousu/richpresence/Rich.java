package me.skiincraft.discord.ousu.richpresence;

import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.User;

public class Rich {

	private User user;
	private RichPresence rich;

	public Rich(User user, RichPresence rich) {
		this.rich = rich;
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public RichPresence getRich() {
		return rich;
	}

	public void setRich(RichPresence rich) {
		this.rich = rich;
	}

}

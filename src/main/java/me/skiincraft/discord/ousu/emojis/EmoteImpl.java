package me.skiincraft.discord.ousu.emojis;

import java.util.List;

import com.google.gson.JsonObject;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.managers.EmoteManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

public class EmoteImpl implements Emote {

	private JsonObject object;

	public EmoteImpl(JsonObject object) {
		this.object = object;
	}

	@Override
	public boolean isFake() {
		return true;
	}

	@Override
	public long getIdLong() {
		return object.get("id").getAsLong();
	}

	@Override
	public boolean isManaged() {
		return false;
	}

	@Override
	public boolean isAnimated() {
		return false;
	}

	@Override
	public List<Role> getRoles() {
		return null;
	}

	@Override
	public String getName() {
		return object.get("name").getAsString();
	}

	@Override
	public EmoteManager getManager() {
		return null;
	}

	@Override
	public JDA getJDA() {
		return null;
	}

	@Override
	public Guild getGuild() {
		return null;
	}

	@Override
	public AuditableRestAction<Void> delete() {
		return null;
	}

	@Override
	public boolean canProvideRoles() {
		return false;
	}

}

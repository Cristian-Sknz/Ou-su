package me.skiincraft.discord.ousu.common;

import java.util.List;

import me.skiincraft.discord.core.command.Command;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.ousu.messages.Messages;
import me.skiincraft.discord.ousu.permission.IPermission;
import me.skiincraft.discord.ousu.permission.IPermission.InternalPermission;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public abstract class Comando extends Command {
	
	public Comando(String name, List<String> aliases, String usage) {
		super(name, aliases, usage);
	}
	
	public abstract CommandCategory getCategory();
	
	public void replyUsage(TextChannel channel) {
		new InteractChannel(channel).reply(Messages.usageMessage(this, channel.getGuild()));
	}
	
	public boolean isOwner(User user) {
		return user.getIdLong() == Long.parseLong("247096601242238991");
	}
	
	public boolean hasIPermission(User user, InternalPermission perm) {
		IPermission permission = new IPermission(user);
		if (isOwner(user)) {
			permission.set("permission", 3);
			return true;
		}
		if (permission.exists()) {
			return permission.getInt("permission") >= perm.getValue();
		}
		return false;
	}

}

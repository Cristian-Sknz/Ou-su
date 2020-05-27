package me.skiincraft.discord.ousu.owneraccess;

import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class LogChannelCommand extends Commands {

	public LogChannelCommand() {
		super("ou!", "setlogger", "setlogger", null);
	}

	@Override
	public String[] helpMessage(LanguageManager langm) {
		return null;
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Owner;
	}

	@Override
	public void action(String[] args, String label, User user, TextChannel channel) {
		if (!isOwner()) {
			return;
		}

		new ConfigAcess(channel.getGuild()).set("Logchannel", channel.getId());
		//sendEmbedMessage(new DefaultEmbed("Configuração", "Você setou o chat de logging")).queue();
	}

}

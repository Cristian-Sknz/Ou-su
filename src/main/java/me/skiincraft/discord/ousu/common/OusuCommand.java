package me.skiincraft.discord.ousu.common;

import java.util.List;

import me.skiincraft.discord.core.command.Command;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.ousu.messages.Messages;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public abstract class OusuCommand extends Command {
	
	public OusuCommand(String name, List<String> aliases, String usage) {
		super(name, aliases, usage);
	}
	
	public abstract CommandCategory getCategory();
	
	public void replyUsage(TextChannel channel) {
		new InteractChannel(channel).reply(Messages.usageMessage(this, channel.getGuild()));
	}
	
	public boolean isOwner(User user) {
		return user.getIdLong() == Long.parseLong("247096601242238991");
	}

	public enum CommandCategory {
		Configuration("Configuração"), Gameplay("Gameplay"), Statistics("Estatisticas"), About("Sobre"), Owner("Dono");

		private final String name;

		CommandCategory(String name) {
			this.name = name;
		}

		public String getCategoria() {
			return name;
		}

		public String getCategoryName(LanguageManager languageManager) {
			return languageManager.getString("Category", name().toUpperCase());
		}
	}
}

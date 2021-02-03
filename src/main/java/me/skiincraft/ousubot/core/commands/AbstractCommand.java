package me.skiincraft.ousubot.core.commands;

import me.skiincraft.ousubot.view.Messages;
import me.skiincraft.ousucore.command.CommandExecutor;
import me.skiincraft.ousucore.language.Language;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public abstract class AbstractCommand extends CommandExecutor {

    public AbstractCommand(String name, List<String> aliases, String usage) {
        super(name, aliases, usage);
    }

    public abstract CommandType getCategory();

    public String getCommandDescription(Language language){
        return language.getString("command.description." + getName());
    }

    public boolean isOwner(User user) {
        return user.getIdLong() == Long.parseLong("247096601242238991");
    }

    public void replyUsage(TextChannel textChannel) {
        textChannel.sendMessage(Messages.getUsage(this, textChannel.getGuild())).queue();
    }

    public enum CommandType {
        Configuration("Configuração"), Gameplay("Gameplay"), Statistics("Estatisticas"), About("Sobre"), Owner("Dono");

        private final String name;

        CommandType(String name) {
            this.name = name;
        }

        public String getCategoryName() {
            return name;
        }

        public String getCategoryName(Language language) {
            return language.getString("command", "category", name().toLowerCase());
        }

        public String getDescription(Language language) {
            return String.join("\n", language.getStrings("messages.helpcommand." + name().toLowerCase()));
        }
    }
}

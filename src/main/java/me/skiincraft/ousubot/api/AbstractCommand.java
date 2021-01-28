package me.skiincraft.ousubot.api;

import me.skiincraft.discord.core.command.Command;
import me.skiincraft.discord.core.language.Language;
import me.skiincraft.ousubot.view.Messages;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public abstract class AbstractCommand extends Command {

    public AbstractCommand(String name, List<String> aliases, String usage) {
        super(name, aliases, usage);
    }

    public abstract CommandType getCategory();

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
        public String getDescription(Language language){
            return String.join("\n", language.getStrings("messages.helpcommand." + name().toLowerCase()));
        }
    }

    public boolean isOwner(User user) {
        return user.getIdLong() == Long.parseLong("247096601242238991");
    }

    protected void replyUsage(TextChannel textChannel) {
        textChannel.sendMessage(Messages.getUsage(this, textChannel.getGuild())).queue();
    }
}

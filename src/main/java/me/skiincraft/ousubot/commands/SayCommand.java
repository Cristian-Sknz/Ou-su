package me.skiincraft.ousubot.commands;

import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousucore.command.utils.CommandTools;
import me.skiincraft.ousucore.language.Language;
import me.skiincraft.ousubot.core.commands.AbstractCommand;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;

@CommandMap
public class SayCommand extends AbstractCommand {

    public SayCommand() {
        super("say", Arrays.asList("falar", "fala"), "say <args>");
    }

    public CommandType getCategory() {
        return CommandType.Owner;
    }

    public void execute(String label, String[] args, CommandTools channel) {
        Language guildLang = Language.getGuildLanguage(channel.getChannel().getGuild());
        if (!channel.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            channel.reply(channel.getMember().getAsMention() + "w " + guildLang.getString("command.messages.permission")
                    .replace("{permission}", Permission.MANAGE_SERVER.getName()));
            return;
        }
        channel.reply(new MessageBuilder(String.join(" ", args)).build());
    }

}

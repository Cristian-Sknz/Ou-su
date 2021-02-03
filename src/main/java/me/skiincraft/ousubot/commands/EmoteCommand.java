package me.skiincraft.ousubot.commands;

import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousucore.OusuCore;
import me.skiincraft.ousubot.api.AbstractCommand;
import me.skiincraft.ousubot.view.emotes.GenericEmote;
import me.skiincraft.ousubot.view.emotes.GenericsEmotes;
import me.skiincraft.ousucore.command.utils.CommandTools;

import java.util.List;

@CommandMap
public class EmoteCommand extends AbstractCommand {

    @Inject
    private GenericsEmotes emotes;

    public EmoteCommand() {
        super("emotes", null, "emotes <add>");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.Owner;
    }

    @Override
    public void execute(String label, String[] args, CommandTools channel) {
        if (!isOwner(channel.getMember().getUser())) {
            channel.reply("You are not allowed to perform this command");
            return;
        }
        if (args.length == 0) {
            replyUsage(channel.getChannel());
            return;
        }
        if (args[0].equalsIgnoreCase("add")) {
            List<GenericEmote> emote = emotes.parseEmotes(channel.getChannel().getGuild());
            emotes.saveEmotes(OusuCore.getAssetsPath().toAbsolutePath() + "/emotes/", emote);
            channel.reply("Emotes salvos com sucesso.");
        }
    }
}

package me.skiincraft.ousubot.commands;

import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.ousubot.api.AbstractCommand;
import me.skiincraft.ousubot.view.emotes.GenericEmote;
import me.skiincraft.ousubot.view.emotes.GenericsEmotes;
import net.dv8tion.jda.api.entities.Member;

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
    public void execute(Member user, String[] args, InteractChannel channel) {
        if (!isOwner(user.getUser())) {
            channel.reply("You are not allowed to perform this command");
            return;
        }
        if (args.length == 0){
            replyUsage(channel.getTextChannel());
            return;
        }
        if (args[0].equalsIgnoreCase("add")){
            List<GenericEmote> emote = emotes.parseEmotes(channel.getTextChannel().getGuild());
            emotes.saveEmotes(OusuCore.getAssetsPath().toAbsolutePath() + "/emotes/", emote);
            channel.reply("Emotes salvos com sucesso.");
        }
    }
}

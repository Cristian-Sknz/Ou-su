package me.skiincraft.ousubot.commands.about;

import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousucore.command.utils.CommandTools;
import me.skiincraft.ousucore.language.Language;
import me.skiincraft.ousubot.core.commands.AbstractCommand;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Arrays;

@CommandMap
public class InviteCommand extends AbstractCommand {

    public InviteCommand() {
        super("invite", Arrays.asList("convite", "convide", "convidar"), "invite");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.About;
    }

    public void execute(String label, String[] args, CommandTools channel) {
        Guild guild = channel.getChannel().getGuild();
        MessageModel model = new MessageModel("embeds/vanilla/invite_message", Language.getGuildLanguage(guild));
        String[] links = new String[]{"https://discordapp.com/oauth2/authorize?client_id=701825726449582192&scope=bot&permissions=1678108752",
                "https://discord.gg/VtkYdBR"};

        model.addProperty("selfuser", guild.getSelfMember().getUser());
        model.addProperty("inviteurl", links[0]);
        model.addProperty("botdiscordurl", links[1]);

        channel.reply(model.getEmbedBuilder().build());
    }

}

package me.skiincraft.ousubot.commands.about;

import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousubot.core.commands.AbstractCommand;
import me.skiincraft.ousucore.command.utils.CommandTools;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

@CommandMap
public class PingCommand extends AbstractCommand {

    public PingCommand() {
        super("ping", null, "ping");
    }

    public CommandType getCategory() {
        return CommandType.About;
    }

    public void execute(String label, String[] args, CommandTools channel) {
        final long inicialms = System.currentTimeMillis();
        channel.reply(message(channel.getMember().getUser(), channel.getChannel()).build(), m -> m.editMessage(message(channel.getMember().getUser(), channel.getChannel()).replace("{?}", (System.currentTimeMillis() - inicialms) + "").build()));
    }

    public MessageBuilder message(User user, TextChannel channel) {
        MessageBuilder message = new MessageBuilder();
        message.append(user.getAsMention()).append(" Pong!");
        message.append("\n:timer:| GatewayPing: `").append(String.valueOf(channel.getJDA().getGatewayPing()));
        message.append("ms`\n:incoming_envelope:| API Ping: `{?}ms`");

        return message;
    }

}

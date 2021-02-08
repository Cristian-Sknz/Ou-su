package me.skiincraft.ousubot.commands.configuration;

import me.skiincraft.api.osu.entity.user.SimpleUser;
import me.skiincraft.api.osu.exceptions.ResourceNotFoundException;
import me.skiincraft.api.osu.exceptions.TokenException;
import me.skiincraft.api.osu.object.game.GameMode;
import me.skiincraft.api.osu.requests.Token;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousubot.core.commands.AbstractCommand;
import me.skiincraft.ousubot.core.OusuAPI;
import me.skiincraft.ousubot.models.OusuUser;
import me.skiincraft.ousubot.repositories.UserRepository;
import me.skiincraft.ousubot.view.Messages;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousubot.view.models.UserAdapter;
import me.skiincraft.ousubot.view.utils.CountryCodes;
import me.skiincraft.ousucore.command.objecs.Command;
import me.skiincraft.ousucore.command.utils.CommandTools;
import me.skiincraft.ousucore.language.Language;

@CommandMap
public class SetUserCommand extends AbstractCommand {

    @Inject
    private OusuAPI api;
    @Inject
    private UserRepository userRepository;

    public SetUserCommand() {
        super("setuser", null, "setuser <user>");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.Configuration;
    }

    @Override
    public void execute(String label, String[] args, CommandTools channel) {
        if (args.length == 0) {
            replyUsage(channel.getChannel());
            return;
        }
        Token token = api.getAPIV1().getTokens().get(0);
        SimpleUser user = token.getEndpoint().getUser(String.join(" ", args), GameMode.Osu).get();

        MessageModel model = new MessageModel("embeds/setuser", Language.getGuildLanguage(channel.getChannel().getGuild()));
        model.addProperty("countryCode", CountryCodes.getCountryCode(user.getCountryCode()));
        model.addProperty("userAdapter", new UserAdapter(user));
        userRepository.save(userRepository.getById(channel.getMember().getIdLong()).orElse(new OusuUser(channel.getMember().getUser(), user.getId())).setOsuId(user.getId()));
        channel.reply(model.getEmbedBuilder().build());
    }


    @Override
    public void onFailure(Exception exception, Command command) {
        CommandTools tools = new CommandTools(command.getMessage());
        if (exception instanceof ResourceNotFoundException){
            tools.reply(Messages.getWarning("command.messages.user.inexistent_user", tools.getGuild()));
            return;
        }
        if (exception instanceof TokenException){
            tools.reply(Messages.getWarning("messages.error.token", tools.getGuild()));
            return;
        }
        tools.reply(Messages.getError(exception, tools.getGuild()).build());
    }
}

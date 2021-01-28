package me.skiincraft.ousubot.commands;

import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.language.Language;
import me.skiincraft.ousubot.api.AbstractCommand;
import me.skiincraft.ousubot.api.OusuAPI;
import me.skiincraft.ousubot.models.APIKey;
import me.skiincraft.ousubot.repositories.APIKeyRepository;
import me.skiincraft.ousubot.view.Messages;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@CommandMap
public class TokenCommand extends AbstractCommand {

    @Inject
    private APIKeyRepository apiKeyRepository;
    @Inject
    private OusuAPI api;

    public TokenCommand() {
        super("token", null, "token <arguments>");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.Owner;
    }

    @Override
    public void execute(Member member, String[] args, InteractChannel channel) {
        if (!isOwner(member.getUser())) {
            channel.reply("> Somente o Developer pode utilizar este comando.");
            return;
        }

        Language language = Language.getGuildLanguage(channel.getTextChannel().getGuild());
        if (args.length == 0){
            List<APIKey> keys = apiKeyRepository.getAll();
            if (keys.size() == 0){
                channel.reply("Não existe nenhuma key disponível!");
                return;
            }
            MessageModel model = new MessageModel("embeds/api/tokens_embed", language);
            String[] keysArray = keys.stream().map(organizeAll())
                    .toArray(String[]::new);

            model.addProperty("size", keysArray.length);
            for (int i = 0; i < 3; i++){
                List<String> values = new ArrayList<>();
                for (String string : keysArray){
                    String[] keysSplit = string.split(",");
                    values.add(keysSplit[i]);
                }
                model.addProperty((i == 0) ? "identify": (i == 1) ? "refresh" : "expires", String.join("\n", values));
            }

            channel.reply(model.getEmbedBuilder().build());
        }

        if (args.length <= 1){
            return;
        }

        if (args[0].equalsIgnoreCase("add")){
            try {
                api.createToken(args[1]);
                channel.reply("Um token foi criado com sucesso!");
            } catch (Exception e){
                channel.reply(Messages.getError(e, channel.getTextChannel().getGuild()).build());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("remove")){
            if (args[1].length() != 12 || !apiKeyRepository.getById(args[1]).isPresent()){
                channel.reply("Não existe nenhum token com esta identificação.");
                return;
            }
            api.remove(args[1]);
            channel.reply(String.format("Você removeu o token com o identificador '`%s`'.", args[1]));
            return;
        }

        if (args[0].equalsIgnoreCase("refresh")){
            if (args[1].length() <= 12) {
                APIKey apiKey = apiKeyRepository.getById(args[1]).orElse(null);
                if (Objects.isNull(apiKey)){
                    channel.reply("Não existe nenhum token com esta identificação.");
                    return;
                }
                try {
                    api.refreshToken(apiKey.getRefreshToken());
                    channel.reply("Refresh Token feito com sucesso!");
                } catch (Exception e){
                    channel.reply(Messages.getError(e, channel.getTextChannel().getGuild()).build());
                }
                return;
            }
            try {
                api.refreshToken(args[1]);
                channel.reply("Refresh Token feito com sucesso!");
            } catch (Exception e) {
                channel.reply(Messages.getError(e, channel.getTextChannel().getGuild()).build());
            }
        }
    }


    private Function<? super APIKey, ? extends String> organizeAll(){
        return (Function<APIKey, String>) apiKey -> {
            String expireDate = apiKey.getExpireInString();
            return String.format("`%s`,`%s`,(`%s`)", apiKey.getIdentification(), (apiKey.getRefreshToken() == null) ? "none" : apiKey.getRefreshToken().substring(0, 12), (expireDate == null) ? "Nunca" : expireDate);
        };
    }

}

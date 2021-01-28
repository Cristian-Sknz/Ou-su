package me.skiincraft.ousubot.commands;

import me.skiincraft.api.osu.entity.beatmap.Beatmap;
import me.skiincraft.api.osu.exceptions.ResourceNotFoundException;
import me.skiincraft.api.osu.requests.Endpoint;
import me.skiincraft.api.osu.requests.Token;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.language.Language;
import me.skiincraft.ousubot.api.AbstractCommand;
import me.skiincraft.ousubot.api.OusuAPI;
import me.skiincraft.ousubot.view.Messages;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousubot.view.models.BeatmapSimple;
import me.skiincraft.ousubot.view.utils.ColorThief;
import net.dv8tion.jda.api.entities.Member;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;

@CommandMap
public class BeatmapCommand extends AbstractCommand {

    @Inject
    private OusuAPI api;

    public BeatmapCommand() {
        super("beatmap", Collections.singletonList("beatmapid"), "beatmap <beatmapId>");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.Gameplay;
    }

    @Override
    public void execute(Member member, String[] args, InteractChannel channel) {
        if (args.length == 0) {
            replyUsage(channel.getTextChannel());
            return;
        }
        if (!args[0].matches("-?\\d+(\\.\\d+)?")){
            SearchCommand searchCommand = (SearchCommand) OusuCore.getCommandManager().getCommands().stream().filter(command -> command instanceof SearchCommand).findFirst()
                    .orElse(null);
            if (Objects.isNull(searchCommand)){
                replyUsage(channel.getTextChannel());
                return;
            }
            searchCommand.execute(member, args, channel);
            return;
        }

        Token token = api.getAvailableTokens();
        Endpoint endpoint = token.getEndpoint();
        try {
            Beatmap beatmap = endpoint.getBeatmap(Integer.parseInt(args[0])).get();
            MessageModel model = new MessageModel("embeds/beatmapv1", Language.getGuildLanguage(channel.getTextChannel().getGuild()));
            model.addProperty("beatmapAdapter", new BeatmapSimple(beatmap, model.getEmotes()));
            model.addProperty("color", ColorThief.getPredominatColor(ImageIO.read(new URL(beatmap.getBeatmapSet().getCovers().getCard())), false));
            channel.reply(model.getEmbedBuilder().build(), (message) -> {
                try {
                    message.getChannel().sendFile(beatmap.getBeatmapSet().getPreview(),
                            beatmap.getBeatmapSet().getBeatmapSetId() + " " + beatmap.getBeatmapSet().getTitle() + ".mp3")
                            .queue();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (ResourceNotFoundException | IOException e) {
            channel.reply(Messages.getWarning("command.messages.beatmap.inexistent_id", channel.getTextChannel().getGuild()));
        } catch (Exception e) {
            channel.reply(Messages.getError(e, channel.getTextChannel().getGuild()).build());
        }
    }
}

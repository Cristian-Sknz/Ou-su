package me.skiincraft.ousubot.commands;

import me.skiincraft.api.osu.entity.beatmap.Beatmap;
import me.skiincraft.api.osu.exceptions.ResourceNotFoundException;
import me.skiincraft.api.osu.requests.Endpoint;
import me.skiincraft.api.osu.requests.Token;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousubot.OusuBot;
import me.skiincraft.ousubot.api.AbstractCommand;
import me.skiincraft.ousubot.api.OusuAPI;
import me.skiincraft.ousubot.models.ChannelTracking;
import me.skiincraft.ousubot.view.Messages;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousubot.view.models.BeatmapSimple;
import me.skiincraft.ousubot.view.utils.ColorThief;
import me.skiincraft.ousucore.OusuCore;
import me.skiincraft.ousucore.command.objecs.Command;
import me.skiincraft.ousucore.command.objecs.CommandMessage;
import me.skiincraft.ousucore.command.utils.CommandTools;
import me.skiincraft.ousucore.language.Language;
import me.skiincraft.ousucore.utils.ThrowableConsumer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@CommandMap
public class BeatmapCommand extends AbstractCommand {

    @Inject
    private OusuAPI api;

    public BeatmapCommand() {
        super("beatmap", Arrays.asList("beatmapid", "map", "m"), "beatmap <beatmapId>");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.Gameplay;
    }

    @Override
    public void execute(String label, String[] args, CommandTools channel) {
        if (args.length == 0) {
            Optional<ChannelTracking> tracking = ChannelTracking.getFromRepository(channel.getChannel());
            if (!tracking.isPresent() || tracking.get().getBeatmapId() == 0) {
                replyUsage(channel.getChannel());
                return;
            }
            args = new String[]{String.valueOf(tracking.get().getBeatmapId())};
        }
        if (!args[0].matches("-?\\d+(\\.\\d+)?")) {
            SearchCommand searchCommand = (SearchCommand) OusuCore.getCommandManager().getCommands()
                    .stream()
                    .filter(command -> command instanceof SearchCommand).findFirst()
                    .orElse(null);

            if (Objects.isNull(searchCommand)) {
                replyUsage(channel.getChannel());
                return;
            }
            searchCommand.execute("search", args, channel);
            return;
        }

        Token token = api.getAvailableTokens();
        Endpoint endpoint = token.getEndpoint();
        Beatmap beatmap = endpoint.getBeatmap(Integer.parseInt(args[0])).get();
        OusuBot.getTrackingRepository().save(new ChannelTracking(channel.getChannel(), beatmap.getBeatmapId()));
        MessageModel model = new MessageModel("embeds/beatmapv1", Language.getGuildLanguage(channel.getChannel().getGuild()));
        channel.reply(buildModel(model, beatmap).build(), sendPreview(beatmap));
    }

    private EmbedBuilder buildModel(MessageModel model, Beatmap beatmap) {
        model.addProperty("beatmapAdapter", new BeatmapSimple(beatmap, model.getEmotes()));
        try {
            model.addProperty("color", ColorThief.getPredominatColor(ImageIO.read(new URL(beatmap.getBeatmapSet().getCovers().getCard())), false));
        } catch (IOException e){
            model.addProperty("color", Color.ORANGE);
        }
        return model.getEmbedBuilder();
    }

    public ThrowableConsumer<CommandMessage> sendPreview(Beatmap beatmap){
        return (message) -> {
          MessageChannel channel = message.getMessage().getChannel();
          channel.sendFile(beatmap.getBeatmapSet().getPreview(), beatmap.getBeatmapSetId() + beatmap.getBeatmapSet().getTitle() + ".mp3").queue();
        };
    }

    @Override
    public void onFailure(Exception exception, Command command) {
        CommandTools tools = new CommandTools(command.getMessage());
        if (exception instanceof ResourceNotFoundException){
            tools.reply(Messages.getWarning("command.messages.beatmap.inexistent_id", tools.getGuild()));
            return;
        }
        if (exception instanceof IOException){
            exception.printStackTrace();
            return;
        }
        tools.reply(Messages.getError(exception, tools.getChannel().getGuild()).build());
    }
}

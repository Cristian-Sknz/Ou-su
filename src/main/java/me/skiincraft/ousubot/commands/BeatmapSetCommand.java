package me.skiincraft.ousubot.commands;

import me.skiincraft.api.osu.entity.beatmap.BeatmapSet;
import me.skiincraft.api.osu.exceptions.ResourceNotFoundException;
import me.skiincraft.api.osu.requests.Endpoint;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousubot.OusuBot;
import me.skiincraft.ousubot.api.AbstractCommand;
import me.skiincraft.ousubot.api.OusuAPI;
import me.skiincraft.ousubot.impl.BeatmapSetReactionConsumer;
import me.skiincraft.ousubot.models.ChannelTracking;
import me.skiincraft.ousubot.view.Messages;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousubot.view.models.BeatmapSimple;
import me.skiincraft.ousubot.view.utils.ColorThief;
import me.skiincraft.ousucore.OusuCore;
import me.skiincraft.ousucore.command.objecs.Command;
import me.skiincraft.ousucore.command.utils.CommandTools;
import me.skiincraft.ousucore.common.reactions.ReactionObject;
import me.skiincraft.ousucore.common.reactions.Reactions;
import me.skiincraft.ousucore.common.reactions.custom.ReactionPage;
import me.skiincraft.ousucore.language.Language;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@CommandMap
public class BeatmapSetCommand extends AbstractCommand {

    @Inject
    private OusuAPI api;

    public BeatmapSetCommand() {
        super("beatmapset", Collections.singletonList("beatmapsetid"), "beatmapset <beatmapSetId>");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.Gameplay;
    }

    @Override
    public void execute(String label, String[] args, CommandTools channel) {
        if (args.length == 0) {
            replyUsage(channel.getChannel());
            return;
        }
        if (!args[0].matches("-?\\d+(\\.\\d+)?")) {
            SearchCommand searchCommand = (SearchCommand) OusuCore.getCommandManager()
                    .getCommands(SearchCommand.class, (cmd) -> cmd instanceof SearchCommand)
                    .get(0);

            if (Objects.isNull(searchCommand)) {
                replyUsage(channel.getChannel());
                return;
            }
            searchCommand.execute("search", args, channel);
            return;
        }
        Endpoint endpoint = api.getAvailableTokens().getEndpoint();
        BeatmapSet beatmapSet = endpoint.getBeatmapSet(Integer.parseInt(args[0])).get();
        MessageModel model = new MessageModel("embeds/beatmapv1", Language.getGuildLanguage(channel.getChannel().getGuild()));
        channel.reply(Messages.getLoading(), (message) -> {
            EmbedBuilder[] embedArray = getModelEmbedBuilders(model, beatmapSet);
            Reactions.getInstance().registerReaction(new ReactionObject(message.getMessage(), channel.getMember().getIdLong(),
                    new String[]{"U+25C0", "U+25B6"}), new ReactionPage(Arrays.asList(embedArray), true)
                    .setOnReaction(new BeatmapSetReactionConsumer(embedArray, beatmapSet.getBeatmaps())));
            OusuBot.getTrackingRepository().save(new ChannelTracking(channel.getChannel(), beatmapSet.getBeatmaps().get(0).getBeatmapId()));
            message.editMessage(embedArray[0].build());
            channel.getChannel().sendFile(beatmapSet.getPreview(),
                    beatmapSet.getBeatmapSetId() + " " + beatmapSet.getTitle() + ".mp3")
                    .queue();
        });
    }

    @Override
    public void onFailure(Exception exception, Command command) {
        CommandTools tools = new CommandTools(command.getMessage());
        if (exception instanceof ResourceNotFoundException){
            tools.reply(Messages.getWarning("command.messages.beatmap.inexistent_id", tools.getChannel().getGuild()));
            return;
        }
        tools.reply(Messages.getError(exception, tools.getChannel().getGuild()).build());
    }

    public EmbedBuilder[] getModelEmbedBuilders(MessageModel model, BeatmapSet beatmapSet) {
        AtomicReference<Color> color = new AtomicReference<>(Color.ORANGE);
        try {
            color.set(ColorThief.getPredominatColor(ImageIO.read(new URL(beatmapSet.getCovers().getCard())), false));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return beatmapSet.getBeatmaps().stream().map(beatmap -> {
            model.addProperty("beatmapAdapter", new BeatmapSimple(beatmap, model.getEmotes()));
            model.addProperty("color", color.get());
            return model.getEmbedBuilder();
        }).toArray(EmbedBuilder[]::new);
    }
}

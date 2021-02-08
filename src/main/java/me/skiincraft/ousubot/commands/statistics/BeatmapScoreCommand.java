package me.skiincraft.ousubot.commands.statistics;

import me.skiincraft.api.osu.entity.beatmap.BeatmapSet;
import me.skiincraft.api.osu.entity.score.Score;
import me.skiincraft.api.osu.exceptions.ResourceNotFoundException;
import me.skiincraft.api.osu.exceptions.TokenException;
import me.skiincraft.api.osu.requests.Endpoint;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousubot.OusuBot;
import me.skiincraft.ousubot.core.OusuAPI;
import me.skiincraft.ousubot.core.commands.AbstractCommand;
import me.skiincraft.ousubot.impl.ScoreReactionConsumer;
import me.skiincraft.ousubot.models.ChannelTracking;
import me.skiincraft.ousubot.view.Messages;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousubot.view.models.ScoreAdapter;
import me.skiincraft.ousubot.view.utils.ColorThief;
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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@CommandMap
public class BeatmapScoreCommand extends AbstractCommand {

    @Inject
    private OusuAPI api;

    public BeatmapScoreCommand() {
        super("beatmapscore", Arrays.asList("bs", "score"), "beatmapscore <beatmapId>");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.Statistics;
    }

    @Override
    public void execute(String label, String[] args, CommandTools channel) {
        if (args.length == 0) {
            ChannelTracking channelTracking = ChannelTracking.getFromRepository(channel.getChannel()).orElse(null);
            if (channelTracking == null || channelTracking.getBeatmapId() == 0) {
                replyUsage(channel.getChannel());
                return;
            }
            args = new String[] {String.valueOf(channelTracking.getBeatmapId())};
        }
        if (!args[0].matches("-?\\d+(\\.\\d+)?")) {
            replyUsage(channel.getChannel());
            return;
        }

        Endpoint endpoint = api.getAvailableTokens().getEndpoint();
        List<Score> scores = endpoint.getBeatmapScores(Long.parseLong(args[0])).get().getScores();
        if (scores.size() == 0){
            throw new ResourceNotFoundException("Não tem nenhum score");
        }
        MessageModel model = new MessageModel("embeds/score", Language.getGuildLanguage(channel.getChannel().getGuild()));
        BeatmapSet beatmapSet = endpoint.getBeatmapSet(scores.get(0).getBeatmapId()).get();
        channel.reply(Messages.getLoading(), (message) -> {
            EmbedBuilder[] embedArray = getModelEmbedBuilders(model, scores, beatmapSet);
            Reactions.getInstance().registerReaction(new ReactionObject(message.getMessage(), channel.getMember().getIdLong(),
                            new String[]{"U+25C0", "U+25B6"}),
                    new ReactionPage(Arrays.asList(embedArray), true)
                            .setOnReaction(new ScoreReactionConsumer(embedArray, scores)));
            message.editMessage(embedArray[0].build());
            OusuBot.getTrackingRepository().save(new ChannelTracking(channel.getChannel(), scores.get(0).getBeatmapId()));
        });
    }

    @Override
    public void onFailure(Exception exception, Command command) {
        CommandTools tools = new CommandTools(command.getMessage());
        if (exception instanceof ResourceNotFoundException){
            tools.reply(Messages.getWarning("command.messages.beatmap.inexistent_id", tools.getGuild()));
            return;
        }
        if (exception instanceof TokenException){
            tools.reply(Messages.getWarning("messages.error.token", tools.getGuild()));
            return;
        }
        tools.reply(Messages.getError(exception, tools.getGuild()).build());
    }

    public EmbedBuilder[] getModelEmbedBuilders(MessageModel model, List<Score> scores, BeatmapSet beatmapSet) {
        return scores.stream().map(score -> {
            AtomicReference<Color> color = new AtomicReference<>(Color.ORANGE);
            try {
                color.set(ColorThief.getPredominatColor(ImageIO.read(new URL(beatmapSet.getCovers().getList2x())), false));
            } catch (IOException ignored) {}
            model.addProperty("scoreAdapter", new ScoreAdapter(score, beatmapSet, model.getEmotes()));
            model.addProperty("color", color.get());
            if (color.get() == Color.ORANGE){
                return model.getEmbedBuilder()
                        .setTimestamp(score.getCreatedDate())
                        .setImage("https://i.imgur.com/LfF0VBR.gif");
            }
            return model.getEmbedBuilder().setTimestamp(score.getCreatedDate());
        }).toArray(EmbedBuilder[]::new);
    }
}

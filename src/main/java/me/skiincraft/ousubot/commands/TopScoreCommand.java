package me.skiincraft.ousubot.commands;

import me.skiincraft.api.osu.entity.score.Score;
import me.skiincraft.api.osu.exceptions.ResourceNotFoundException;
import me.skiincraft.api.osu.object.score.ScoreOption;
import me.skiincraft.api.osu.requests.Endpoint;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousubot.OusuBot;
import me.skiincraft.ousubot.api.AbstractCommand;
import me.skiincraft.ousubot.api.OusuAPI;
import me.skiincraft.ousubot.impl.ScoreReactionConsumer;
import me.skiincraft.ousubot.models.ChannelTracking;
import me.skiincraft.ousubot.models.OusuUser;
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
import net.dv8tion.jda.api.entities.Member;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@CommandMap
public class TopScoreCommand extends AbstractCommand {

    @Inject
    private OusuAPI api;

    public TopScoreCommand() {
        super("top", Arrays.asList("topuser", "topscore", "ts"), "top <username>");
    }

    public static EmbedBuilder[] getModelEmbedBuilders(MessageModel model, List<Score> scores) {
        return scores.stream().map(score -> {
            AtomicReference<Color> color = new AtomicReference<>(Color.ORANGE);
            try {
                color.set(ColorThief.getPredominatColor(ImageIO.read(new URL(Objects.requireNonNull(score.getBeatmapSet()).getCovers().getList2x())), false));
            } catch (IOException e) {
                e.printStackTrace();
            }
            model.addProperty("scoreAdapter", new ScoreAdapter(score, model.getEmotes()));
            model.addProperty("color", color.get());
            return model.getEmbedBuilder().setTimestamp(score.getCreatedDate());
        }).toArray(EmbedBuilder[]::new);
    }

    @Override
    public CommandType getCategory() {
        return CommandType.Statistics;
    }

    @Override
    public void execute(String label, String[] args, CommandTools channel) {
        if (args.length == 0) {
            long userId = getOsuId(channel.getMember());
            if (userId == 0) {
                replyUsage(channel.getChannel());
                return;
            }
            args = new String[]{String.valueOf(userId)};
        }
        Endpoint endpoint = api.getAvailableTokens().getEndpoint();
        List<Score> scores = endpoint.getUserScore(getUserId(endpoint, String.join(" ", args)), new ScoreOption()).get();
        if (scores.size() == 0) {
            throw new ResourceNotFoundException("Este usuário não tem nenhum historico!");
        }
        MessageModel model = new MessageModel("embeds/score", Language.getGuildLanguage(channel.getChannel().getGuild()));
        channel.reply(Messages.getLoading(), (message) -> {
            EmbedBuilder[] embedArray = getModelEmbedBuilders(model, scores);
            Reactions.getInstance().registerReaction(new ReactionObject(message.getMessage(), channel.getMember().getIdLong(),
                    new String[]{"U+25C0", "U+25B6"}), new ReactionPage(Arrays.asList(embedArray), true)
                    .setOnReaction(new ScoreReactionConsumer(embedArray, scores)));
            message.editMessage(embedArray[0].build());
            OusuBot.getTrackingRepository().save(new ChannelTracking(channel.getChannel(), scores.get(0).getBeatmapId()));
        });
    }

    @Override
    public void onFailure(Exception exception, Command command) {
        CommandTools tools = new CommandTools(command.getMessage());
        if (exception instanceof ResourceNotFoundException) {
            tools.reply(Messages.getWarning("command.messages.score.inexistent_user", tools.getGuild()));
            return;
        }
        tools.reply(Messages.getError(exception, tools.getGuild()).build());
    }

    public boolean isUserId(String string) {
        return string.matches("-?\\d+(\\.\\d+)?");
    }

    public long getUserId(Endpoint endpoint, String string) {
        if (isUserId(string)) {
            return Long.parseLong(string);
        }
        return endpoint.getUserId(string).get();
    }

    private long getOsuId(Member member) {
        OusuUser user = OusuBot.getUserRepository().getById(member.getIdLong()).orElse(null);
        if (user == null) {
            return 0;
        }
        return user.getOsuId();
    }
}

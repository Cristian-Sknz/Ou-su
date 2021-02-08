package me.skiincraft.ousubot.commands.statistics;

import me.skiincraft.api.osu.entity.score.Score;
import me.skiincraft.api.osu.exceptions.ResourceNotFoundException;
import me.skiincraft.api.osu.exceptions.TokenException;
import me.skiincraft.api.osu.object.game.GameMode;
import me.skiincraft.api.osu.object.score.ScoreOption;
import me.skiincraft.api.osu.object.score.ScoreType;
import me.skiincraft.api.osu.requests.Endpoint;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousubot.OusuBot;
import me.skiincraft.ousubot.core.OusuAPI;
import me.skiincraft.ousubot.core.commands.OptionCommand;
import me.skiincraft.ousubot.core.commands.options.CommandOption;
import me.skiincraft.ousubot.core.commands.options.GamemodeOption;
import me.skiincraft.ousubot.core.commands.options.Options;
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
public class RecentScoreCommand extends OptionCommand {

    @Inject
    private OusuAPI api;

    private final CommandOption[] options = {
            new GamemodeOption(GameMode.Osu),
            new GamemodeOption(GameMode.Taiko),
            new GamemodeOption(GameMode.Mania),
            new GamemodeOption(GameMode.Fruits),
    };

    public RecentScoreCommand() {
        super("recentscore", Arrays.asList("recent", "r", "rs"), "recentscore <username> [-gamemode]");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.Statistics;
    }

    @Override
    public void executeWithOptions(String label, String[] args, Options options, CommandTools channel) {
        if (args.length == 0) {
            long userId = getOsuId(channel.getMember());
            if (userId == 0) {
                replyUsage(channel.getChannel());
                return;
            }
            args = new String[]{String.valueOf(userId)};
        }
        Endpoint endpoint = api.getAvailableTokens().getEndpoint();
        List<Score> scores = endpoint.getUserScore(getUserId(endpoint, String.join(" ", args)), buildSearchOption(options)).get();
        if (scores.size() == 0) {
            throw new ResourceNotFoundException("Este usuário não tem nenhum historico!");
        }
        MessageModel model = new MessageModel("embeds/score", Language.getGuildLanguage(channel.getChannel().getGuild()));
        channel.reply(Messages.getLoading(), (message) -> {
            EmbedBuilder[] embedArray = getModelEmbedBuilders(model, scores);
            Reactions.getInstance().registerReaction(new ReactionObject(message.getMessage(), channel.getMember().getIdLong(),
                    new String[]{"U+25C0", "U+25B6"}),
                    new ReactionPage(Arrays.asList(embedArray), true)
                    .setOnReaction(new ScoreReactionConsumer(embedArray, scores)));
            OusuBot.getTrackingRepository().save(new ChannelTracking(channel.getChannel(), scores.get(0).getBeatmapId()));
            message.editMessage(embedArray[0].build());
        });
    }

    @Override
    public CommandOption[] getCommandOptions() {
        return options;
    }

    @Override
    public void onFailure(Exception exception, Command command) {
        CommandTools tools = new CommandTools(command.getMessage());
        if (exception instanceof ResourceNotFoundException){
            tools.reply(Messages.getWarning("command.messages.score.inexistent_user", tools.getGuild()));
            return;
        }
        if (exception instanceof TokenException){
            tools.reply(Messages.getWarning("messages.error.token", tools.getGuild()));
            return;
        }
        tools.reply(Messages.getError(exception, tools.getGuild()).build());
    }

    private ScoreOption buildSearchOption(Options options){
        return new ScoreOption(ScoreType.RECENT)
                .setIncludeFails(true)
                .setGameMode(gamemodeOption(options));
    }

    private GameMode gamemodeOption(Options options) {
        for (Options.OptionArguments arguments: options){
            if (arguments.getOption() instanceof GamemodeOption){
                return ((GamemodeOption) arguments.getOption()).getGameMode();
            }
        }
        return GameMode.Osu;
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

    public EmbedBuilder[] getModelEmbedBuilders(MessageModel model, List<Score> scores) {
        return scores.stream().map(score -> {
            AtomicReference<Color> color = new AtomicReference<>(Color.ORANGE);
            try {
                color.set(ColorThief.getPredominatColor(ImageIO.read(new URL(Objects.requireNonNull(score.getBeatmapSet()).getCovers().getList2x())), false));
            } catch (IOException ignored) {}
            model.addProperty("scoreAdapter", new ScoreAdapter(score, model.getEmotes()));
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

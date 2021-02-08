package me.skiincraft.ousubot.commands.statistics;

import me.skiincraft.api.osu.entity.score.Score;
import me.skiincraft.api.osu.exceptions.ResourceNotFoundException;
import me.skiincraft.api.osu.exceptions.TokenException;
import me.skiincraft.api.osu.object.game.GameMode;
import me.skiincraft.api.osu.requests.Token;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousubot.OusuBot;
import me.skiincraft.ousubot.core.commands.AbstractCommand;
import me.skiincraft.ousubot.core.OusuAPI;
import me.skiincraft.ousubot.models.ChannelTracking;
import me.skiincraft.ousubot.models.OusuUser;
import me.skiincraft.ousubot.repositories.UserRepository;
import me.skiincraft.ousubot.view.Messages;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousucore.command.objecs.Command;
import me.skiincraft.ousucore.command.utils.CommandTools;
import me.skiincraft.ousucore.common.reactions.ReactionObject;
import me.skiincraft.ousucore.common.reactions.Reactions;
import me.skiincraft.ousucore.common.reactions.custom.ReactionPage;
import me.skiincraft.ousucore.language.Language;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CommandMap
public class CompareCommand extends AbstractCommand {

    @Inject
    private OusuAPI api;
    @Inject
    private UserRepository userRepository;

    public CompareCommand() {
        super("compare", Arrays.asList("comparar", "c"), "compare <username>");
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
        ChannelTracking channelTracking = ChannelTracking.getFromRepository(channel.getChannel()).orElse(null);
        if (channelTracking == null || channelTracking.getBeatmapId() == 0) {
            replyUsage(channel.getChannel());
            return;
        }
        Token token = api.getAPIV1().getTokens().get(0);
        String user = String.join(" ", args);
        List<Score> scores = token.getEndpoint().getUserScore(user, channelTracking.getBeatmapId()).get()
                .stream()
                .map(score -> api.getAvailableTokens().getEndpoint().getScore(GameMode.Osu, score.getScoreId()).get())
                .collect(Collectors.toList());

        if (scores.size() == 0) {
            channel.reply(Messages.getWarning("command.messages.score.inexistent_user", channel.getChannel().getGuild()));
        }
        channel.reply(Messages.getLoading(), (message) -> {
            MessageModel model = new MessageModel("embeds/score", Language.getGuildLanguage(channel.getChannel().getGuild()));
            EmbedBuilder[] embedBuilder = TopScoreCommand.getModelEmbedBuilders(model, scores);
            message.editMessage(embedBuilder[0].build());
            if (embedBuilder.length > 1) {
                Reactions.getInstance().registerReaction(new ReactionObject(message.getMessage(), channel.getMember().getIdLong(),
                        new String[]{"U+25C0", "U+25B6"}), new ReactionPage(Arrays.asList(embedBuilder), true));
            }
        });
    }

    private long getOsuId(Member member) {
        OusuUser user = OusuBot.getUserRepository().getById(member.getIdLong()).orElse(null);
        if (user == null) {
            return 0;
        }
        return user.getOsuId();
    }

    @Override
    public void onFailure(Exception exception, Command command) {
        CommandTools tools = new CommandTools(command.getMessage());
        if (exception instanceof ResourceNotFoundException) {
            tools.reply(Messages.getWarning("command.messages.score.inexistent_user", tools.getGuild()));
            return;
        }
        if (exception instanceof TokenException){
            tools.reply(Messages.getWarning("messages.error.token", tools.getGuild()));
            return;
        }
        tools.reply(Messages.getError(exception, tools.getGuild()).build());
    }
}

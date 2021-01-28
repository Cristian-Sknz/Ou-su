package me.skiincraft.ousubot.commands;

import me.skiincraft.api.osu.entity.score.Score;
import me.skiincraft.api.osu.exceptions.ResourceNotFoundException;
import me.skiincraft.api.osu.object.score.ScoreType;
import me.skiincraft.api.osu.requests.Endpoint;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.common.reactions.ReactionObject;
import me.skiincraft.discord.core.common.reactions.Reactions;
import me.skiincraft.discord.core.common.reactions.custom.ReactionPage;
import me.skiincraft.discord.core.language.Language;
import me.skiincraft.ousubot.api.AbstractCommand;
import me.skiincraft.ousubot.api.OusuAPI;
import me.skiincraft.ousubot.view.Messages;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousubot.view.models.ScoreAdapter;
import me.skiincraft.ousubot.view.utils.ColorThief;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@CommandMap
public class RecentScoreCommand extends AbstractCommand {

    @Inject
    private OusuAPI api;

    public RecentScoreCommand() {
        super("recent", Collections.singletonList("recentuser"), "recent <username>");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.Statistics;
    }

    @Override
    public void execute(Member member, String[] args, InteractChannel channel) {
        if (args.length == 0) {
            replyUsage(channel.getTextChannel());
            return;
        }
        try {
            Endpoint endpoint = api.getAvailableTokens().getEndpoint();
            List<Score> scores = endpoint.getUserScore(getUserId(endpoint, String.join(" ", args)), ScoreType.RECENT).get();
            if (scores.size() == 0){
                throw new ResourceNotFoundException("Este usuário não tem nenhum historico!");
            }
            MessageModel model = new MessageModel("embeds/score", Language.getGuildLanguage(channel.getTextChannel().getGuild()));
            channel.reply(Messages.getLoading(), (message) -> {
                EmbedBuilder[] embedArray = getModelEmbedBuilders(model, scores);
                Reactions.getInstance().registerReaction(new ReactionObject(message, member.getIdLong(),
                        new String[]{"U+25C0", "U+25B6"}), new ReactionPage(Arrays.asList(embedArray), true));

                message.editMessage(embedArray[0].build()).queue();
            });
        } catch (ResourceNotFoundException e) {
            channel.reply(Messages.getWarning("command.messages.score.inexistent_user", channel.getTextChannel().getGuild()));
        } catch (Exception e) {
            channel.reply(Messages.getError(e, channel.getTextChannel().getGuild()).build());
        }
    }

    public boolean isUserId(String string){
        return string.matches("-?\\d+(\\.\\d+)?");
    }

    public long getUserId(Endpoint endpoint,String string){
        if (isUserId(string)) {
            return Long.parseLong(string);
        }
        return endpoint.getUserId(string).get();
    }

    public EmbedBuilder[] getModelEmbedBuilders(MessageModel model, List<Score> scores){
        return scores.stream().map(score -> {
            AtomicReference<Color> color = new AtomicReference<>(Color.ORANGE);
            try {
                color.set(ColorThief.getPredominatColor(ImageIO.read(new URL(Objects.requireNonNull(score.getBeatmapSet()).getCovers().getList2x())), false));
            } catch (IOException e){
                e.printStackTrace();
            }
            model.addProperty("scoreAdapter", new ScoreAdapter(score, model.getEmotes()));
            model.addProperty("color", color.get());
            return model.getEmbedBuilder().setTimestamp(score.getCreatedDate());
        }).toArray(EmbedBuilder[]::new);
    }
}

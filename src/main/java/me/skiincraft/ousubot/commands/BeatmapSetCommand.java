package me.skiincraft.ousubot.commands;

import me.skiincraft.api.osu.entity.beatmap.BeatmapSet;
import me.skiincraft.api.osu.exceptions.ResourceNotFoundException;
import me.skiincraft.api.osu.requests.Endpoint;
import me.skiincraft.api.osu.requests.Token;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.common.reactions.ReactionObject;
import me.skiincraft.discord.core.common.reactions.Reactions;
import me.skiincraft.discord.core.common.reactions.custom.ReactionPage;
import me.skiincraft.discord.core.language.Language;
import me.skiincraft.ousubot.api.AbstractCommand;
import me.skiincraft.ousubot.api.OusuAPI;
import me.skiincraft.ousubot.view.Messages;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousubot.view.models.BeatmapSimple;
import me.skiincraft.ousubot.view.utils.ColorThief;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

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
            BeatmapSet beatmapSet = endpoint.getBeatmapSet(Integer.parseInt(args[0])).get();
            MessageModel model = new MessageModel("embeds/beatmapv1", Language.getGuildLanguage(channel.getTextChannel().getGuild()));
            channel.reply(Messages.getLoading(), (message) -> {
                EmbedBuilder[] embedArray = getModelEmbedBuilders(model, beatmapSet);
                Reactions.getInstance().registerReaction(new ReactionObject(message, member.getIdLong(),
                                new String[]{"U+25C0", "U+25B6"}), new ReactionPage(Arrays.asList(embedArray), true));
                message.editMessage(embedArray[0].build()).queue();
                try {
                    message.getChannel().sendFile(beatmapSet.getPreview(),
                            beatmapSet.getBeatmapSetId() + " " + beatmapSet.getTitle() + ".mp3")
                            .queue();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (ResourceNotFoundException e) {
            channel.reply(Messages.getWarning("command.messages.beatmap.inexistent_id", channel.getTextChannel().getGuild()));
        } catch (Exception e) {
            channel.reply(Messages.getError(e, channel.getTextChannel().getGuild()).build());
        }
    }

    public EmbedBuilder[] getModelEmbedBuilders(MessageModel model, BeatmapSet beatmapSet){
        AtomicReference<Color> color = new AtomicReference<>(Color.ORANGE);
        try {
             color.set(ColorThief.getPredominatColor(ImageIO.read(new URL(beatmapSet.getCovers().getCard())), false));
        } catch (IOException e){
            e.printStackTrace();
        }
        return beatmapSet.getBeatmaps().stream().map(beatmap -> {
            model.addProperty("beatmapAdapter", new BeatmapSimple(beatmap, model.getEmotes()));
            model.addProperty("color", color.get());
            return model.getEmbedBuilder();
        }).toArray(EmbedBuilder[]::new);
    }
}

package me.skiincraft.ousubot.commands;

import me.skiincraft.api.osu.entity.beatmap.BeatmapSearch;
import me.skiincraft.api.osu.entity.beatmap.BeatmapSet;
import me.skiincraft.api.osu.exceptions.ResourceNotFoundException;
import me.skiincraft.api.osu.object.beatmap.Approval;
import me.skiincraft.api.osu.object.beatmap.SearchOption;
import me.skiincraft.api.osu.object.game.GameMode;
import me.skiincraft.api.osu.requests.Endpoint;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousubot.api.AbstractCommand;
import me.skiincraft.ousubot.api.OusuAPI;
import me.skiincraft.ousubot.view.Messages;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousubot.view.models.BeatmapSimple;
import me.skiincraft.ousucore.command.objecs.Command;
import me.skiincraft.ousucore.command.objecs.CommandMessage;
import me.skiincraft.ousucore.command.utils.CommandTools;
import me.skiincraft.ousucore.common.reactions.ReactionObject;
import me.skiincraft.ousucore.common.reactions.Reactions;
import me.skiincraft.ousucore.common.reactions.custom.ReactionPage;
import me.skiincraft.ousucore.language.Language;
import me.skiincraft.ousucore.utils.ThrowableConsumer;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@CommandMap
public class SearchCommand extends AbstractCommand {

    @Inject
    private OusuAPI api;

    public SearchCommand() {
        super("search", Arrays.asList("pesquisar", "s"), "search <name>");
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

        Endpoint endpoint = api.getAvailableTokens().getEndpoint();
        List<Object> filter = searchFilterBuilder(args);
        BeatmapSearch beatmaps = endpoint.searchBeatmaps(filter.get(0).toString(), (SearchOption) filter.get(1)).get();
        MessageModel model = new MessageModel("embeds/search", Language.getGuildLanguage(channel.getChannel().getGuild()));
        channel.reply(Messages.getLoading(), (message) -> {
            EmbedBuilder[] embedArray = getModelEmbedBuilders(model, beatmaps);
            Reactions.getInstance().registerReaction(new ReactionObject(message.getMessage(), channel.getMember().getIdLong(),
                    new String[]{"U+25C0", "U+25B6"}), new ReactionPage(Arrays.asList(embedArray), true));
            BeatmapSet beatmapSet = beatmaps.getBeatmapSets().get(0);
            message.editMessage(embedArray[0].build(), sendAudio(beatmapSet));
        });
    }

    @Override
    public void onFailure(Exception exception, Command command) {
        CommandTools tools = new CommandTools(command.getMessage());
        if (exception instanceof ResourceNotFoundException){
            tools.reply(Messages.getWarning("command.messages.search.notfound", tools.getGuild()));
            return;
        }
        tools.reply(Messages.getError(exception, tools.getGuild()).build());
    }

    public ThrowableConsumer<CommandMessage> sendAudio(BeatmapSet beatmap){
        return (message) -> message.getMessage().getChannel().sendFile(beatmap.getPreview(),
                beatmap.getBeatmapSetId() + " " + beatmap.getTitle() + ".mp3")
                .queue();
    }

    public EmbedBuilder[] getModelEmbedBuilders(MessageModel model, BeatmapSearch beatmapSet) {
        return beatmapSet.getBeatmapSets().stream().map(beatmap -> {
            model.addProperty("beatmapAdapter", new BeatmapSimple(beatmap, model.getEmotes()));
            return model.getEmbedBuilder();
        }).toArray(EmbedBuilder[]::new);
    }

    public List<Object> searchFilterBuilder(String[] args) {
        List<Object> objects = new ArrayList<>();
        List<String> arg = new ArrayList<>(Arrays.asList(args));
        SearchOption sf = new SearchOption();
        if (args.length >= 2) {
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            for (String word : newArgs) {
                GameMode gamemode = GameMode.byName(word);
                if (Objects.nonNull(gamemode)) {
                    sf.setGameMode(gamemode);
                    arg.remove(word);
                    continue;
                }
                Approval approval = getApprovalByName(word);
                if (Objects.nonNull(approval)) {
                    sf.setCategory(approval);
                    arg.remove(word);
                }
            }
        }
        objects.add(String.join(" ", arg));
        objects.add(sf);
        return objects;
    }

    public Approval getApprovalByName(String name) {
        return Arrays.stream(Approval.values())
                .filter(approval -> approval.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}

package me.skiincraft.ousubot.commands.gameplay;

import me.skiincraft.api.osu.entity.beatmap.BeatmapSearch;
import me.skiincraft.api.osu.entity.beatmap.BeatmapSet;
import me.skiincraft.api.osu.exceptions.ResourceNotFoundException;
import me.skiincraft.api.osu.exceptions.TokenException;
import me.skiincraft.api.osu.object.beatmap.Approval;
import me.skiincraft.api.osu.object.beatmap.Language;
import me.skiincraft.api.osu.object.beatmap.SearchOption;
import me.skiincraft.api.osu.object.game.GameMode;
import me.skiincraft.api.osu.requests.Endpoint;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousubot.core.OusuAPI;
import me.skiincraft.ousubot.core.commands.OptionCommand;
import me.skiincraft.ousubot.core.commands.options.*;
import me.skiincraft.ousubot.view.Messages;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousubot.view.models.BeatmapSimple;
import me.skiincraft.ousucore.command.objecs.Command;
import me.skiincraft.ousucore.command.objecs.CommandMessage;
import me.skiincraft.ousucore.command.utils.CommandTools;
import me.skiincraft.ousucore.common.reactions.ReactionObject;
import me.skiincraft.ousucore.common.reactions.Reactions;
import me.skiincraft.ousucore.common.reactions.custom.ReactionPage;
import me.skiincraft.ousucore.utils.ThrowableConsumer;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandMap
public class SearchCommand extends OptionCommand {

    @Inject
    private OusuAPI api;
    private final CommandOption[] options = getSearchOptions();

    public SearchCommand() {
        super("search", Arrays.asList("pesquisar", "s"), "search <name> [-options]");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.Gameplay;
    }

    @Override
    public void executeWithOptions(String label, String[] args, Options options, CommandTools channel) {
        if (args.length == 0) {
            replyUsage(channel.getChannel());
            return;
        }

        Endpoint endpoint = api.getAvailableTokens().getEndpoint();
        BeatmapSearch beatmaps = endpoint.searchBeatmaps(String.join(" ", args), buildSearchOption(options)).get();
        MessageModel model = new MessageModel("embeds/search", me.skiincraft.ousucore.language.Language.getGuildLanguage(channel.getChannel().getGuild()));
        channel.reply(Messages.getLoading(), (message) -> {
            EmbedBuilder[] embedArray = getModelEmbedBuilders(model, beatmaps);
            Reactions.getInstance().registerReaction(new ReactionObject(message.getMessage(), channel.getMember().getIdLong(),
                    new String[]{"U+25C0", "U+25B6"}), new ReactionPage(Arrays.asList(embedArray), true));
            BeatmapSet beatmapSet = beatmaps.getBeatmapSets().get(0);
            message.editMessage(embedArray[0].build(), sendAudio(beatmapSet));
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
            tools.reply(Messages.getWarning("command.messages.search.notfound", tools.getGuild()));
            return;
        }
        if (exception instanceof TokenException){
            tools.reply(Messages.getWarning("messages.error.token", tools.getGuild()));
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

    public SearchOption buildSearchOption(Options options){
        return new SearchOption().setCategory(getApproval(options))
                .setGameMode(getGamemode(options))
                .setVideo(options.contains("video"))
                .setStoryboard(options.contains("storyboard"))
                .setLanguage(getLanguage(options));
    }

    private Approval getApproval(Options options){
        return Arrays.stream(options.getOptionArguments()).filter(op -> op.getOption() instanceof ApprovalOption).map(op -> ((ApprovalOption) op.getOption()).getApproval())
                .findFirst()
                .orElse(Approval.ANY);
    }

    private GameMode getGamemode(Options options){
        return Arrays.stream(options.getOptionArguments()).filter(op -> op.getOption() instanceof GamemodeOption).map(op -> ((GamemodeOption) op.getOption()).getGameMode())
                .findFirst()
                .orElse(GameMode.Osu);
    }

    private Language getLanguage(Options options){
        return Arrays.stream(options.getOptionArguments()).filter(op -> op.getOption() instanceof LanguageOption).map(op -> ((LanguageOption) op.getOption()).getLanguage())
                .findFirst()
                .orElse(me.skiincraft.api.osu.object.beatmap.Language.Any);
    }

    private static CommandOption[] getSearchOptions(){
        List<CommandOption> options = new ArrayList<>(Arrays.asList(GamemodeOption.getAllGameOptions()));
        options.addAll(Arrays.asList(ApprovalOption.getScoreableOptions()));
        options.addAll(Arrays.asList(LanguageOption.getAllOptions()));
        options.add(new CommandOption("video", new String[]{"vídeo", "v"}, "video"));
        options.add(new CommandOption("storyboard", new String[]{"v"}, "storyboard"));
        return options.toArray(new CommandOption[0]);
    }
}

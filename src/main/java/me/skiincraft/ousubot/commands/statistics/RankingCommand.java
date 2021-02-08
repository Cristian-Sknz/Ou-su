package me.skiincraft.ousubot.commands.statistics;

import com.google.common.collect.Lists;
import me.skiincraft.api.osu.entity.ranking.Ranking;
import me.skiincraft.api.osu.exceptions.TokenException;
import me.skiincraft.api.osu.object.game.GameMode;
import me.skiincraft.api.osu.object.ranking.RankingOption;
import me.skiincraft.api.osu.object.ranking.RankingType;
import me.skiincraft.api.osu.object.user.UserStatistics;
import me.skiincraft.api.osu.requests.Endpoint;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousubot.core.commands.AbstractCommand;
import me.skiincraft.ousubot.core.OusuAPI;
import me.skiincraft.ousubot.view.Messages;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousubot.view.utils.CountryCodes;
import me.skiincraft.ousucore.command.objecs.Command;
import me.skiincraft.ousucore.command.utils.CommandTools;
import me.skiincraft.ousucore.common.reactions.ReactionObject;
import me.skiincraft.ousucore.common.reactions.Reactions;
import me.skiincraft.ousucore.common.reactions.custom.ReactionPage;
import me.skiincraft.ousucore.language.Language;
import net.dv8tion.jda.api.EmbedBuilder;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@CommandMap
public class RankingCommand extends AbstractCommand {

    @Inject
    private OusuAPI api;

    public RankingCommand() {
        super("leaderboard", Collections.singletonList("ranking"), "leaderboard [countrycode]");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.Statistics;
    }

    @Override
    public void execute(String label, String[] args, CommandTools channel) {
        Language language = Language.getGuildLanguage(channel.getChannel().getGuild());
        Endpoint endpoint = api.getAvailableTokens().getEndpoint();
        if (args.length != 0) {
            if (CountryCodes.isCountryCode(args[0])) {
                MessageModel model = new MessageModel("embeds/rankingCountry", language);
                channel.reply(Messages.getLoading(), message -> {
                    try {
                        Ranking ranking = endpoint.getRanking(new RankingOption(GameMode.Osu, RankingType.Performance)
                                .setCountry(args[0].charAt(0), args[0].charAt(1))).get();

                        List<EmbedBuilder> embeds = buildEmbeds(model, Lists.partition(ranking.getUsers(), 10));
                        message.editMessage(embeds.get(0).build());
                        Reactions.getInstance().registerReaction(new ReactionObject(message.getMessage(), channel.getMember().getIdLong(),
                                new String[]{"U+25C0", "U+25B6"}), new ReactionPage(embeds, true));
                    } catch (Exception e) {
                        message.editMessage(Messages.getError(e, channel.getGuild()).build());
                    }
                });
                return;
            }
        }
        MessageModel model = new MessageModel("embeds/ranking", language);
        channel.reply(Messages.getLoading(), message -> {
            try {
                Ranking ranking = endpoint.getRanking(new RankingOption(GameMode.Osu, RankingType.Performance)).get();
                List<EmbedBuilder> embeds = buildEmbeds(model, Lists.partition(ranking.getUsers(), 10));
                message.editMessage(embeds.get(0).build());
                Reactions.getInstance().registerReaction(new ReactionObject(message.getMessage(), channel.getMember().getIdLong(),
                        new String[]{"U+25C0", "U+25B6"}), new ReactionPage(embeds, true));
            } catch (Exception e) {
                message.editMessage(Messages.getError(e, channel.getChannel().getGuild()).build());
            }
        });
    }

    @Override
    public void onFailure(Exception exception, Command command) {
        CommandTools tools = new CommandTools(command.getMessage());
        if (exception instanceof TokenException){
            tools.reply(Messages.getWarning("messages.error.token", tools.getGuild()));
            return;
        }
        tools.reply(Messages.getError(exception, tools.getGuild()).build());
    }

    private List<EmbedBuilder> buildEmbeds(MessageModel model, List<List<UserStatistics>> itens) {
        List<EmbedBuilder> embeds = new ArrayList<>();
        int i = 1;
        String top1avatar = itens.get(0).get(0).getUser().getAvatarURL();
        for (List<UserStatistics> list : itens) {
            AtomicInteger integer = new AtomicInteger(1);
            String fields = list.stream().map(user -> {
                String username = integer.getAndIncrement() +
                        ". :flag_" + user.getUser().getCountryCode().toLowerCase() + ": " +
                        user.getUser().getUsername();
                NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
                return username + "%;" + nf.format((int) user.getPp());
            }).collect(Collectors.joining(";"));

            String[] usernames = Arrays.stream(fields.split(";"))
                    .filter(item -> item.contains("%"))
                    .map(item -> item.replace("%", "")).toArray(String[]::new);

            String[] performancepoints = Arrays.stream(fields.split(";"))
                    .filter(item -> !item.contains("%"))
                    .map(item -> item.replace("%", "")).toArray(String[]::new);

            model.addProperty("usernames", String.join("\n", usernames));
            model.addProperty("performancepoints", String.join("\n", performancepoints));
            model.addProperty("order", i);
            embeds.add(model.getEmbedBuilder().setThumbnail(top1avatar));
            i++;
        }
        return embeds;
    }
}

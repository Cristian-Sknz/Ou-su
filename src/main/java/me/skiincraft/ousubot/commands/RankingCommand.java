package me.skiincraft.ousubot.commands;

import com.google.common.collect.Lists;
import me.skiincraft.api.osu.entity.ranking.Ranking;
import me.skiincraft.api.osu.object.game.GameMode;
import me.skiincraft.api.osu.object.ranking.RankingFilter;
import me.skiincraft.api.osu.object.ranking.RankingType;
import me.skiincraft.api.osu.object.user.UserStatistics;
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
import me.skiincraft.ousubot.view.utils.CountryCodes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

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
    public void execute(Member member, String[] args, InteractChannel channel) {
        Language language = Language.getGuildLanguage(channel.getTextChannel().getGuild());
        Endpoint endpoint = api.getAvailableTokens().getEndpoint();
        if (args.length != 0) {
            if (CountryCodes.isCountryCode(args[0])) {
                MessageModel model = new MessageModel("embeds/rankingCountry", language);
                channel.reply(Messages.getLoading(), message -> {
                    try {
                        Ranking ranking = endpoint.getRanking(new RankingFilter(GameMode.Osu, RankingType.Performance)
                                .setCountry(args[0].charAt(0), args[0].charAt(1))).get();

                        List<EmbedBuilder> embeds = buildEmbeds(model, Lists.partition(ranking.getUsers(), 10));
                        message.editMessage(embeds.get(0).build()).queue();
                        Reactions.getInstance().registerReaction(new ReactionObject(message, member.getIdLong(),
                                new String[]{"U+25C0", "U+25B6"}), new ReactionPage(embeds, true));
                    } catch (Exception e) {
                        message.editMessage(Messages.getError(e, channel.getTextChannel().getGuild()).build()).queue();
                    }
                });
                return;
            }
        }
        MessageModel model = new MessageModel("embeds/ranking", language);
        channel.reply(Messages.getLoading(), message -> {
            try {
                Ranking ranking = endpoint.getRanking(new RankingFilter(GameMode.Osu, RankingType.Performance)).get();
                List<EmbedBuilder> embeds = buildEmbeds(model, Lists.partition(ranking.getUsers(), 10));
                message.editMessage(embeds.get(0).build()).queue();
                Reactions.getInstance().registerReaction(new ReactionObject(message, member.getIdLong(),
                        new String[]{"U+25C0", "U+25B6"}), new ReactionPage(embeds, true));
            } catch (Exception e) {
                message.editMessage(Messages.getError(e, channel.getTextChannel().getGuild()).build()).queue();
            }
        });
    }

    private List<EmbedBuilder> buildEmbeds(MessageModel model, List<List<UserStatistics>> itens){
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

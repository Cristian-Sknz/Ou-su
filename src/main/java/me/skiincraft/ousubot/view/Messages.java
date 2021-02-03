package me.skiincraft.ousubot.view;

import me.skiincraft.ousucore.OusuCore;
import me.skiincraft.ousucore.language.Language;
import me.skiincraft.ousucore.repository.OusuGuild;
import me.skiincraft.ousubot.api.AbstractCommand;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousubot.view.emotes.GenericEmote;
import me.skiincraft.ousubot.view.emotes.GenericsEmotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.text.WordUtils;

import java.awt.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Messages {

    public static MessageEmbed getUsage(AbstractCommand command, Guild guild) {
        MessageModel model = new MessageModel("embeds/vanilla/command_usage", getLanguage(guild));
        String prefix = OusuCore.getGuildRepository().getById(guild.getIdLong())
                .map(OusuGuild::getPrefix)
                .orElse("ou!");

        model.addProperty("prefix", prefix);
        model.addProperty("command", command);
        model.addProperty("selfuser", guild.getSelfMember().getUser());
        model.addProperty("category", getCategoryEmote(command.getCategory()).getEmoteUrl());
        model.addProperty("description", WordUtils.wrap(command.getCommandDescription(model.getLanguage()), 38));

        return model.getEmbedBuilder().build();
    }

    public static MessageEmbed getWarning(String translation, Guild guild) {
        EmbedBuilder embed = new EmbedBuilder();
        Language language = getLanguage(guild);
        String[] translate = language.getStrings(translation);
        String prefix = OusuCore.getGuildRepository().getById(guild.getIdLong()).map(OusuGuild::getPrefix)
                .orElse("ou!");

        embed.setTitle(translate[0]);
        embed.setDescription(String.join("\n", Arrays.copyOfRange(translate, 1, translate.length)));
        embed.setThumbnail("https://i.imgur.com/4ZkdIyq.png");// warning
        embed.setColor(Color.RED);
        embed.setFooter(prefix + "help to help!");

        return embed.build();
    }

    public static EmbedBuilder getConfiguration(String title, String description) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(title);
        embed.setDescription(description);

        embed.setThumbnail("https://i.imgur.com/QsOc85X.gif");
        embed.setColor(new Color(119, 86, 215));
        embed.setFooter("ou!help to help!");

        return embed;
    }

    public static EmbedBuilder getError(Exception exception, Guild guild) {
        EmbedBuilder embed = new EmbedBuilder();
        Language lang = Language.getGuildLanguage(guild);
        String[] string = lang.getStrings("messages.error.problem_ocurred");

        embed.setTitle(":tickets: " + string[0]);
        String exceptionMessage = generateStringException(exception);
        embed.setAuthor("Ou!su - Discord Bot", null, guild.getSelfMember().getUser().getAvatarUrl());
        embed.addField(string[1], "```css\n" + exceptionMessage + "```", false);
        embed.setFooter(lang.getString("messages.error.contact_developer"), "https://i.imgur.com/vt49jhG.png");
        embed.setColor(new Color(18, 138, 133));
        embed.setTimestamp(OffsetDateTime.now(Clock.systemUTC()));

        exception.printStackTrace();

        return embed;
    }

    private static String generateStringException(Exception e) {
        StringBuilder builder = new StringBuilder();
        List<StackTraceElement> allElements = new ArrayList<>(Arrays.asList(e.getStackTrace()));
        List<StackTraceElement> traceElements = allElements.stream()
                .filter(trace -> trace.toString().contains("me.skiincraft.discord"))
                .collect(Collectors.toList());
        builder.append(e.getLocalizedMessage()).append("\n");

        if (traceElements.size() == 0) {
            for (StackTraceElement element : allElements) {
                if (allElements.get(allElements.size() - 1) == element) {
                    builder.append("     in ").append(element.toString()).append("\n");
                    break;
                }
                builder.append("   at ").append(element.toString()).append("\n");
            }
        }

        if (traceElements.size() != 0) {
            for (StackTraceElement element : allElements) {
                if (traceElements.get(traceElements.size() - 1) == element) {
                    builder.append("     in ").append(element.toString()).append("\n");
                    break;
                }
                builder.append("   at ").append(element.toString()).append("\n");
            }
        }
        builder.setLength(1000);
        return builder.toString();
    }


    public static MessageEmbed getLoading() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Loading...");
        embed.setColor(Color.YELLOW);
        embed.setThumbnail("https://i.imgur.com/kPLyktW.gif");
        return embed.build();
    }


    public static Language getLanguage(Guild guild) {
        return Language.getGuildLanguage(guild);
    }

    private static GenericEmote getCategoryEmote(AbstractCommand.CommandType category) {
        return OusuCore.getInjector().getInstanceOf(GenericsEmotes.class).getEmoteEquals(category.name());
    }

}

package me.skiincraft.ousubot.commands;

import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousucore.OusuCore;
import me.skiincraft.ousucore.command.utils.CommandTools;
import me.skiincraft.ousucore.language.Language;
import me.skiincraft.ousucore.repository.GuildRepository;
import me.skiincraft.ousucore.repository.OusuGuild;
import me.skiincraft.ousubot.core.commands.AbstractCommand;
import me.skiincraft.ousubot.view.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CommandMap
public class LanguageCommand extends AbstractCommand {

    public LanguageCommand() {
        super("language", Arrays.asList("idioma", "lang", "linguagem"), "language <availablelanguage>");
    }

    public static String removeAccents(String text) {
        return text == null ? null : Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public CommandType getCategory() {
        return CommandType.Configuration;
    }

public void execute(String label, String[] args, CommandTools channel) {
        Language guildLang = Language.getGuildLanguage(channel.getChannel().getGuild());
        if (!channel.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            channel.reply(channel.getMember().getAsMention() + " " + guildLang.getString("command.messages.permission")
                    .replace("{permission}", Permission.MANAGE_SERVER.getName()));
            return;
        }

        List<Language> languages = OusuCore.getLanguages();
        if (args.length == 0) {
            channel.reply(languages(languages, guildLang));
            return;
        }

        List<Language> filter = languages.stream()
                .filter(l -> l.getLanguageName().equalsIgnoreCase(args[0])
                        || l.getLanguageCode().equalsIgnoreCase(args[0])
                        || l.getName().equalsIgnoreCase(args[0])
                        || l.getCountryCode().equalsIgnoreCase(args[0])
                        || l.getCountry().equalsIgnoreCase(args[0])
                        || removeAccents(l.getLanguageName()).equalsIgnoreCase(args[0])
                        || removeAccents(l.getName()).equalsIgnoreCase(args[0])
                        || removeAccents(l.getCountry()).equalsIgnoreCase(args[0]))
                .collect(Collectors.toList());

        if (filter.size() == 0) {
            channel.reply(languages(languages, guildLang));
            return;
        }

        Language lang = filter.get(0);
        GuildRepository repository = OusuCore.getGuildRepository();
        OusuGuild guild = repository.getById(channel.getChannel().getGuild().getIdLong()).orElse(new OusuGuild(channel.getChannel().getGuild()));
        repository.save(guild.setLanguage(lang));

        String[] str = lang.getStrings("command.messages.language.language_changed");
        EmbedBuilder var = Messages.getConfiguration(str[0], ":small_blue_diamond: " + String.join("\n", Arrays.copyOfRange(str, 1, str.length)))
                .setThumbnail("https://i.imgur.com/sxIERAT.png")
                .setFooter("A multilanguage bot!", "https://i.imgur.com/wDczNj3.jpg");

        channel.reply(var.build());
    }

    public MessageEmbed languages(List<Language> languages, Language lang) {
        String[] str = lang.getStrings("command.messages.language.available_language");

        String availables = languages.stream().map(language -> ":small_blue_diamond:" +
                upperFirstWord(language.getName()) + " - " + language.getCountry()).collect(Collectors.joining("\n"));

        return Messages.getConfiguration(str[0], String.join("\n", Arrays.copyOfRange(str, 1, str.length)).replace("{LANGUAGES}", availables))
                .setThumbnail("https://i.imgur.com/sxIERAT.png")
                .setFooter("A multilanguage bot!", "https://i.imgur.com/wDczNj3.jpg").build();
    }

    public String upperFirstWord(String string) {
        return String.valueOf(string.charAt(0)).toUpperCase() + string.substring(1);
    }

}

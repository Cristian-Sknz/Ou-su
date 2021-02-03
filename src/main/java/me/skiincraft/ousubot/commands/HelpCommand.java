package me.skiincraft.ousubot.commands;

import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousucore.OusuCore;
import me.skiincraft.ousucore.command.utils.CommandTools;
import me.skiincraft.ousucore.common.reactions.ReactionObject;
import me.skiincraft.ousucore.common.reactions.Reactions;
import me.skiincraft.ousucore.common.reactions.custom.ReactionSelector;
import me.skiincraft.ousucore.language.Language;
import me.skiincraft.ousucore.repository.OusuGuild;
import me.skiincraft.ousubot.core.commands.AbstractCommand;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousubot.view.emotes.GenericEmote;
import me.skiincraft.ousubot.view.emotes.GenericsEmotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CommandMap
public class HelpCommand extends AbstractCommand {

    public HelpCommand() {
        super("help", Arrays.asList("ajuda", "h"), "help <command>");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.About;
    }

    @Override
    public void execute(String label, String[] args, CommandTools channel) {
        Guild guild = channel.getChannel().getGuild();
        Language language = Language.getGuildLanguage(channel.getChannel().getGuild());
        if (args.length == 0) {
            MessageModel model = new MessageModel("embeds/vanilla/help_home", language);
            model.addProperty("description", language.getString("messages.helpcommand.home"));
            model.addProperty("selfuser", channel.getChannel().getGuild().getSelfMember().getUser());
            GenericsEmotes emotes = model.getEmotes();
            channel.reply(model.getEmbedBuilder().build(), message -> {
                List<String> reactions = new ArrayList<>();
                CommandType[] types = CommandType.values();
                reactions.add(emotes.getEmoteEquals("home").getReaction());
                reactions.addAll(Arrays.stream(types)
                        .map(emote -> getCategoryEmote(emotes, emote))
                        .map(GenericEmote::getReaction).collect(Collectors.toList()));

                reactions.remove(reactions.size() - 1);
                MessageModel commandsModel = new MessageModel("embeds/vanilla/help_category", language);
                List<EmbedBuilder> embeds = new ArrayList<>();
                embeds.add(model.getEmbedBuilder());
                for (int i = 0; i < reactions.size() - 1; i++) {
                    commandsModel.addProperty("category", types[i].getCategoryName(language));
                    commandsModel.addProperty("categoryicon", getCategoryEmote(emotes, types[i]).getEmoteUrl());
                    commandsModel.addProperty("description", types[i].getDescription(language));
                    commandsModel.addProperty("commands", getCommands(getPrefix(guild), types[i]));
                    commandsModel.addProperty("selfuser", channel.getChannel().getGuild().getSelfMember().getUser());
                    embeds.add(commandsModel.getEmbedBuilder());
                }
                Objects.requireNonNull(Reactions.getInstance()).registerReaction(new ReactionObject(message.getMessage(), channel.getMember().getIdLong(), reactions.toArray(new String[0])), new ReactionSelector(embeds, true));
            });
            return;
        }

        if (args.length == 1) {
            AbstractCommand command = findCommand(args[0]);
            if (Objects.isNull(command)) {
                //
                return;
            }
            channel.reply(helpCommand(command, channel.getChannel().getGuild()).build());
        }
    }

    private String getCommands(String prefix, CommandType category) {
        return OusuCore.getCommandManager().getCommands((cmd) -> cmd instanceof AbstractCommand)
                .stream()
                .filter(command -> ((AbstractCommand)command).getCategory() == category)
                .map(cmd -> prefix + cmd.getName()).collect(Collectors.joining("\n"));
    }

    private String getPrefix(Guild guild) {
        return OusuCore.getGuildRepository().getById(guild.getIdLong()).map(OusuGuild::getPrefix).orElse("ou!");
    }

    private AbstractCommand findCommand(String name) {
        return (AbstractCommand) OusuCore.getCommandManager().getCommands().stream()
                .filter(command -> command instanceof AbstractCommand &&
                        command.getName().equalsIgnoreCase(name) || containsIgnoreCase(command.getAliases(), name))
                .findFirst().orElse(null);
    }

    private boolean containsIgnoreCase(List<String> aliases, String name) {
        if (Objects.isNull(aliases)) {
            return false;
        }
        return aliases.stream().anyMatch(str -> str.equalsIgnoreCase(name));
    }

    public EmbedBuilder helpCommand(AbstractCommand command, Guild guild) {
        EmbedBuilder embed = new EmbedBuilder();
        Language language = Language.getGuildLanguage(guild);
        String prefix = getPrefix(guild);
        embed.setTitle(String.format("Help <%s>", command.getName()));
        embed.setDescription(String.format("%s %s %n", ":small_orange_diamond:", command.getCommandDescription(language)));
        embed.setColor(new Color(246, 246, 11));
        if (Objects.nonNull(command.getAliases()) && command.getAliases().size() != 0) {
            embed.addField(":mega:", command.getAliases().stream()
                    .map(aliase -> prefix + aliase).collect(Collectors.joining("\n")), true);
        }
        embed.addField("Usage:", prefix + command.getUsage(), true);
        embed.setFooter(prefix + "help to Help!");
        return embed;
    }

    public GenericEmote getCategoryEmote(GenericsEmotes emotes, CommandType category) {
        return emotes.getEmoteEquals(category.name());
    }

}

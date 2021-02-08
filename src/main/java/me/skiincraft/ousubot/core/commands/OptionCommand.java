package me.skiincraft.ousubot.core.commands;

import me.skiincraft.ousubot.core.commands.options.CommandOption;
import me.skiincraft.ousubot.core.commands.options.Options;
import me.skiincraft.ousucore.command.utils.CommandTools;

import java.util.List;
import java.util.regex.Pattern;

public abstract class OptionCommand extends AbstractCommand {

    public OptionCommand(String name, List<String> aliases, String usage) {
        super(name, aliases, usage);
    }

    public abstract void executeWithOptions(String label, String[] args, Options options, CommandTools channel) throws Exception;
    public abstract CommandOption[] getCommandOptions();

    @Override
    public final void execute(String label, String[] args, CommandTools channel) throws Exception {
        Options options = Options.OptionArguments.of(args, getCommandOptions());
        executeWithOptions(label, replaceArguments(args, options), options, channel);
    }

    private String[] replaceArguments(String[] strings, Options arguments){
        if (strings.length == 0) {
            return strings;
        }
        String str = String.join(" ", strings);
        for (Options.OptionArguments option : arguments){
            if (option.containsArgs()) {
                str = str.replaceAll("(?i)" + Pattern.quote(option.getLabel()), "");
                continue;
            }

            str = str.replaceAll("(?i)" + Pattern.quote(option.getLabel() + ((option.getArgs().length == 0) ? "" : " " + String.join(" ", option.getArgs()))), "");
        }
        return (str.trim().isEmpty()) ? new String[0] : str.split(" ");
    }
}

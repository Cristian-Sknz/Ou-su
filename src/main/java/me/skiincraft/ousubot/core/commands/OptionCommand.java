package me.skiincraft.ousubot.core.commands;

import me.skiincraft.ousubot.core.commands.objects.CommandOption;
import me.skiincraft.ousubot.core.commands.objects.OptionArguments;
import me.skiincraft.ousucore.command.utils.CommandTools;

import java.util.List;
import java.util.regex.Pattern;

public abstract class OptionCommand extends AbstractCommand {

    public OptionCommand(String name, List<String> aliases, String usage) {
        super(name, aliases, usage);
    }

    public abstract void executeWithOptions(String label, String[] args, OptionArguments[] options, CommandTools channel) throws Exception;
    public abstract CommandOption[] getCommandOptions();

    @Override
    public final void execute(String label, String[] args, CommandTools channel) throws Exception {
        OptionArguments[] options = OptionArguments.of(args, getCommandOptions());
        executeWithOptions(label, replaceArguments(args, options), options, channel);
    }

    private String[] replaceArguments(String[] strings, OptionArguments[] arguments){
        String str = String.join(" ", strings);
        for (OptionArguments option : arguments){
            if (option.containsArgs()){
                str = str.replaceAll("(?i)" + Pattern.quote("-" + option.getName()), "");
                continue;
            }
            str = str.replaceAll("(?i)" + Pattern.quote("-" + option.getName() + " " + String.join(" ", option.getArgs())), "");
        }
        return str.split(" ");
    }
}

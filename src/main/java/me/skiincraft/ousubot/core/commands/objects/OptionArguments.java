package me.skiincraft.ousubot.core.commands.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionArguments {

    private final CommandOption option;
    private final String[] args;

    public OptionArguments(CommandOption option, String[] args) {
        this.option = option;
        this.args = args;
    }

    public String getName(){
        return option.getName();
    }

    public String[] getArgs() {
        return args;
    }

    public CommandOption getOption() {
        return option;
    }

    public boolean isOption(String name){
        return option.getName().equalsIgnoreCase(name);
    }

    public boolean containsArgs(){
        return args.length != 0;
    }

    public static OptionArguments[] of(String[] args, CommandOption[] options){
        if (options == null || args.length == 0 || options.length == 0){
            return new OptionArguments[0];
        }
        List<OptionArguments> optionArguments = new ArrayList<>();
        for (CommandOption option : options) {
            StringBuilder arguments = new StringBuilder();
            boolean matches = false;
            for (String string : args) {
                if (!matches) {
                    if (string.equalsIgnoreCase("-" + option.getName())) {
                        if (option.isReceiveArgs()) {
                            matches = true;
                            continue;
                        }
                        optionArguments.add(new OptionArguments(option, new String[0]));
                        break;
                    }
                    continue;
                }
                if (Arrays.stream(options).anyMatch(op -> op.getName().equalsIgnoreCase(string))) {
                    break;
                }
                arguments.append(string).append(" ");
            }
            if (matches && arguments.length() != 0) {
                optionArguments.add(new OptionArguments(option, arguments.toString().split(" ")));
            }
        }
        return optionArguments.toArray(new OptionArguments[0]);
    }

}

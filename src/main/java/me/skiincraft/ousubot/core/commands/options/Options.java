package me.skiincraft.ousubot.core.commands.options;

import com.google.common.collect.Iterators;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Options implements Iterable<Options.OptionArguments> {

    private final OptionArguments[] optionArguments;

    public Options(OptionArguments[] optionArguments) {
        this.optionArguments = optionArguments;
    }

    @Nullable
    public OptionArguments get(String option){
        return Arrays.stream(optionArguments).filter(op -> op.getName().equalsIgnoreCase(option) || op.getOption().isAlternate(option)).findFirst().orElse(null);
    }

    public boolean contains(String option){
        return Arrays.stream(optionArguments).anyMatch(op -> op.getName().equalsIgnoreCase(option) || op.getOption().isAlternate(option));
    }


    public OptionArguments[] getOptionArguments() {
        return optionArguments;
    }

    public int size() {
        return optionArguments.length;
    }

    @Override
    public String toString() {
        return "Options{" +
                "optionArguments=" + Arrays.toString(optionArguments) +
                '}';
    }

    @NotNull
    @Override
    public Iterator<OptionArguments> iterator() {
        return Iterators.forArray(optionArguments);
    }

    public static class OptionArguments {

        private final String label;
        private final CommandOption option;
        private final String[] args;

        public OptionArguments(String label, CommandOption option, String[] args) {
            this.label = label;
            this.option = option;
            this.args = args;
        }

        public String getName(){
            return option.getName();
        }

        public String[] getArgs() {
            return args;
        }

        public String getLabel() {
            return label;
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

        public static Options of(String[] args, CommandOption[] options){
            if (options == null || args.length == 0 || options.length == 0){
                return new Options(new OptionArguments[0]);
            }
            List<OptionArguments> optionArguments = new ArrayList<>();
            for (CommandOption option : options) {
                StringBuilder arguments = new StringBuilder();
                String label = "";
                boolean matches = false;
                for (String string : args) {
                    if (!matches) {
                        if (string.equalsIgnoreCase("-" + option.getName()) || option.isAlternate(string)) {
                            if (option.isReceiveArgs()) {
                                label = string;
                                matches = true;
                                continue;
                            }
                            optionArguments.add(new OptionArguments(string, option, new String[0]));
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
                    optionArguments.add(new OptionArguments(label, option, arguments.toString().split(" ")));
                }
            }
            return new Options(optionArguments.toArray(new OptionArguments[0]));
        }

        @Override
        public String toString() {
            return "OptionArguments{" +
                    "option=" + option +
                    ", args=" + Arrays.toString(args) +
                    '}';
        }
    }
}

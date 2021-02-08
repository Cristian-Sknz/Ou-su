package me.skiincraft.ousubot.core.commands.options;

import me.skiincraft.ousucore.language.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandOption {

    private final String name;
    private final String[] alternate;
    private final String commandName;
    private final boolean receiveArgs;

    public CommandOption(String name, String commandName) {
        this.name = name;
        this.alternate = new String[0];
        this.commandName = commandName;
        this.receiveArgs = false;
    }

    public CommandOption(String name, String[] alternate, String commandName) {
        this.name = name;
        this.alternate = alternate;
        this.commandName = commandName;
        this.receiveArgs = false;
    }

    public CommandOption(String name, String commandName, boolean receiveArgs) {
        this.name = name;
        this.alternate = new String[0];
        this.commandName = commandName;
        this.receiveArgs = receiveArgs;
    }

    public CommandOption(String name, String[] alternate, String commandName, boolean receiveArgs) {
        this.name = name;
        this.alternate = alternate;
        this.commandName = commandName;
        this.receiveArgs = receiveArgs;
    }

    public String getName() {
        return name;
    }

    public String[] getAlternate() {
        return alternate;
    }

    public boolean isAlternate(String name){
        return Arrays.stream(alternate).anyMatch(alternate -> alternate.equalsIgnoreCase(name) || name.equalsIgnoreCase("-" + alternate));
    }

    public boolean isReceiveArgs() {
        return receiveArgs;
    }

    public String getDescription(Language language){
        return language.getString(String.format("command.option.%s.%s", commandName.toLowerCase(), getName().toLowerCase()));
    }

    public static CommandOption[] append(CommandOption[] commandOptions, CommandOption... append){
        List<CommandOption> optionList = new ArrayList<>(Arrays.asList(commandOptions));
        optionList.addAll(Arrays.asList(append));
        return optionList.toArray(new CommandOption[0]);
    }

    @Override
    public String toString() {
        return "CommandOption{" +
                "name='" + name + '\'' +
                ", alternate=" + Arrays.toString(alternate) +
                ", command=" + commandName +
                '}';
    }

}

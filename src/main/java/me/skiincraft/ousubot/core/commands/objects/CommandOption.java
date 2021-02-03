package me.skiincraft.ousubot.core.commands.objects;

import me.skiincraft.ousucore.command.CommandExecutor;
import me.skiincraft.ousucore.language.Language;

public class CommandOption {

    private final String name;
    private final CommandExecutor command;
    private final boolean receiveArgs;

    public CommandOption(String name, CommandExecutor command) {
        this.name = name;
        this.command = command;
        this.receiveArgs = false;
    }

    public CommandOption(String name, CommandExecutor command, boolean receiveArgs) {
        this.name = name;
        this.command = command;
        this.receiveArgs = receiveArgs;
    }

    public String getName() {
        return name;
    }

    public boolean isReceiveArgs() {
        return receiveArgs;
    }

    public CommandExecutor getCommand() {
        return command;
    }

    public String getDescription(Language language){
        return language.getString(String.format("command.option.%s.%s", command.getName().toLowerCase(), getName().toLowerCase()));
    }
}

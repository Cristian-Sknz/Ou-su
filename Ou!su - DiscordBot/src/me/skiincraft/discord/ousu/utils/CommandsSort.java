package me.skiincraft.discord.ousu.utils;

import java.util.Comparator;

import me.skiincraft.discord.ousu.manager.Commands;

public class CommandsSort implements Comparator<Commands> {

	@Override
	public int compare(Commands arg0, Commands arg1) {
		return arg0.getCommand().compareTo(arg1.getCommand());
	}

}

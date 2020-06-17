package me.skiincraft.discord.ousu.commands;

import java.util.Arrays;

import me.skiincraft.discord.ousu.abstractcore.CommandCategory;
import me.skiincraft.discord.ousu.abstractcore.Commands;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.language.LanguageManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

public class EmbedCommand extends Commands {

	public EmbedCommand() {
		super("ou!", "embed", "embed <title> <description> (\"_\" for space)", Arrays.asList("broadcast"));
	}

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("HELPMESSAGE_EMBED");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Administracao;
	}

	@Override
	public void action(String[] args, String label, TextChannel channel) {
		if (!hasPermission(getUserId(), Permission.MANAGE_CHANNEL)) {
			noPermissionMessage(Permission.MANAGE_SERVER);
			return;
		}

		if (args.length == 0) {
			sendUsage();
			return;
		}

		if (args.length == 1) {
			reply(TypeEmbed.DefaultEmbed(args[0].replaceAll("_", " "), "").build());
			return;
		}

		if (args.length >= 2) {
			StringBuffer em = new StringBuffer();

			for (int i = 1; i < args.length; i++) {
				em.append(args[i] + " ");
			}
			reply(TypeEmbed.DefaultEmbed(args[0].replaceAll("_", " "), em.toString()).build());
			return;
		}
		return;
	}
}

package me.skiincraft.discord.ousu.commands;

import java.util.Arrays;

import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
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
			noPermissionMessage(Permission.MANAGE_SERVER).queue();
			return;
		}

		if (args.length == 0) {
			sendUsage().queue();
			return;
		}

		if (args.length == 1) {
			sendEmbedMessage(TypeEmbed.DefaultEmbed(args[0].replaceAll("_", " "), "")).queue();
			return;
		}

		if (args.length >= 2) {
			StringBuffer em = new StringBuffer();

			for (int i = 1; i < args.length; i++) {
				em.append(args[i] + " ");
			}

			sendEmbedMessage(TypeEmbed.DefaultEmbed(args[0].replaceAll("_", " "), em.toString())).queue();
			return;
		}
		return;
	}
}

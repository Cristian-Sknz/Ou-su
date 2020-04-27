package me.skiincraft.discord.ousu.commands;

import java.util.Arrays;

import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.utils.DefaultEmbed;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class EmbedCommand extends Commands {

	public EmbedCommand() {
		super("ou!", "embed", "y!embed <title> <description> (utilize _ como espaço no titulo)", Arrays.asList("broadcast"));
	}
	
	@Override
	public String[] helpMessage() {
		return new String[] {"Este comando serve para criar embeds simples."};
	}
	
	@Override
	public CommandCategory categoria() {
		return CommandCategory.Administração;
	}

	@Override
	public void action(String[] args, User user, TextChannel channel) {
		if (!hasPermissionorRole(user, Permission.MANAGE_CHANNEL, "mod")) {
			sendEmbedMessage(new DefaultEmbed(":gear: Permissão Insuficiente", "Voce não tem permissão para executar este comando.")).queue();
			return;
		}
		
		if (args.length == 1) {
			sendUsage().queue();
			return;
		}
		
		if (args.length == 2) {
			sendEmbedMessage(new DefaultEmbed(args[1].replaceAll("_", " "), "")).queue();
			return;
		}
		
		if (args.length >= 3) {
			StringBuffer em = new StringBuffer();
			
			for (int i = 2; i < args.length; i++) {
				em.append(args[i] + " ");
			}
			
			sendEmbedMessage(new DefaultEmbed(args[1].replaceAll("_", " "), em.toString())).queue();
			return;
		}
		return;
	}
}

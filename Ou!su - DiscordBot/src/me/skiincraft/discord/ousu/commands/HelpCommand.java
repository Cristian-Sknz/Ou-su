package me.skiincraft.discord.ousu.commands;

import java.util.ArrayList;
import java.util.List;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class HelpCommand extends Commands {

	public HelpCommand() {
		super("ou!", "help");
	}
	
	public static List<Commands> commands = new ArrayList<Commands>();
	
	@Override
	public String[] helpMessage() {
		return new String[] {"Este comando serve para mostrar", "o menu de comandos disponiveis."};
	}
	
	@Override
	public CommandCategory categoria() {
		return CommandCategory.Ajuda;
	}

	@Override
	public void action(String[] args, User user, TextChannel channel) {
		if (isInsuficient()) {
			sendEmbedMessage(embed(channel.getGuild())).queue();	
			return;
		}
		
		if (args.length == 2) {
			System.out.println(args[1]);
			sendEmbedMessage(emb(args[1], channel.getGuild())).queue();
			return;
		}
		
		return;
	}
	
	public EmbedBuilder emb(String comando, Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();
		for (Commands com : commands) {
			if (comando.equalsIgnoreCase(com.getCommand())) {
				embed.setTitle("Help <" + com.getCommand() + ">");
				if (com.helpMessage() != null) {
				embed.setDescription(String.join("\n", com.helpMessage()));
				} else {
					embed.setDescription("Não foi definido nenhuma descrição para este comando :/");
				}
				
				
				if (com.hasAliases()) {
					String[] alias = com.getAliases().stream().toArray(String[]::new);
					embed.addField("Aliases", String.join("\n", alias), true);	
				}
				
					embed.addField("Usage", com.getUsage().replace("ou!", new SQLAccess(guild).get("prefix")), true);
					return embed;
			}
		}
		return null;
	}
	
	public EmbedBuilder embed(Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();
		
		String a = "";
		String b = "";
		String c = "";
		String d = "";

		for (int i = 0; i < commands.size();i++) {
			Commands comando = commands.get(i);
			String prefix = new SQLAccess(guild).get("prefix");
			
			if (comando.getCategoria() == CommandCategory.Administração) {
				a += prefix + comando.getCommand() +"\n";
			}			
			if (comando.getCategoria() == CommandCategory.Ajuda) {
				b += prefix + comando.getCommand() +"\n";	
			}
			if (comando.getCategoria() == CommandCategory.Osu) {
				c += prefix + comando.getCommand() +"\n";
			}			
			if (comando.getCategoria() == CommandCategory.Sobre) {
				d += prefix + comando.getCommand() +"\n";	
			}
		}
		embed.setDescription("Todos os comandos disponiveis no bot.");
		embed.addField("**"+CommandCategory.Administração.getCategoria()+"**", "`"+a+"`", true);
		embed.addField("**"+CommandCategory.Ajuda.getCategoria()+"**", "`"+b+"`", true);
		embed.addField("**"+CommandCategory.Osu.getCategoria()+"**", "`"+c+"`", true);
		embed.addField("**"+CommandCategory.Sobre.getCategoria()+"**", "`"+d+"`", true);
		
		embed.setTitle("Lista de comandos");
		User user = OusuBot.getOusu().getJda().getUserById("247096601242238991");
		embed.setFooter(user.getName() + "#" + user.getDiscriminator() + " | Ou!su bot ™", user.getAvatarUrl());
		return embed;
	}
}

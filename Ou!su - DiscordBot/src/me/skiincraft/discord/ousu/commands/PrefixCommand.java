package me.skiincraft.discord.ousu.commands;

import java.awt.Color;

import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import me.skiincraft.discord.ousu.utils.DefaultEmbed;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class PrefixCommand extends Commands {

	public PrefixCommand() {
		super("ou!", "prefix");
	}
	
	@Override
	public String[] helpMessage() {
		return new String[] {"Troque o prefixo padrão para outro", "o prefix padrão é ou!"};
	}
	
	@Override
	public CommandCategory categoria() {
		return CommandCategory.Administração;
	}

	@Override
	public void action(String[] args, User user, TextChannel channel) {
		if (!hasPermission(user, Permission.MANAGE_SERVER)){
			noPermissionMessage(Permission.MANAGE_SERVER);
			return;
		}
		
		if (isInsuficient()) {
			setUsage(getCommandFull() + " [args]");
			sendUsage().queue();
			return;
		}
		
		if (args.length >= 1) {
			if (!StringUtils.containsSpecialCharacters(args[1])) {
				sendEmbedMessage(new DefaultEmbed("'❌' Uso incorreto", "É necessario ter um caractere especial \n[!,@,# etc]").construirEmbed()).queue();
				return;
			}
			if (args[1].length() > 3) {
				sendEmbedMessage(new DefaultEmbed("'❌' Uso incorreto", "Somente 3 caracteres são permitidos.").construirEmbed()).queue();
				return;
			}
			
			SQLAccess sql = new SQLAccess(channel.getGuild());
			channel.getGuild();
			
			String oldPrefix = sql.get("prefix");
			String newPrefix = args[1];
			sql.set("prefix", newPrefix);
			
			EmbedBuilder defaultembed = 
					new DefaultEmbed(":gear: Novo prefixo!",
					"Prefixo do servidor foi alterado com sucesso").construirEmbed();
			
			defaultembed.addField("Anterior", oldPrefix, true);
			defaultembed.addField("Novo", newPrefix, true);
			defaultembed.setColor(Color.GRAY);
			
			sendEmbedMessage(defaultembed.build()).queue();
			
			return;
		}
		
	}
	

}

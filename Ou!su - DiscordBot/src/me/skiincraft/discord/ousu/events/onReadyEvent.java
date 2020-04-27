package me.skiincraft.discord.ousu.events;

import java.util.List;

import me.skiincraft.discord.ousu.mysql.SQLAccess;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class onReadyEvent extends ListenerAdapter {

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		SQLAccess guild = new SQLAccess(event.getGuild());
		guild.criar();
		System.out.println();
		System.out.println("Novo servidor adicionado!");
		System.out.println(event.getGuild().getId());
		System.out.println(event.getGuild().getName());
		System.out.println("com " + event.getGuild().getMemberCount() + " membros.");
		System.out.println();
		int guilds = event.getJDA().getGuilds().size();
		event.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(guilds + " Servidores."));
	}
	
	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		SQLAccess guild = new SQLAccess(event.getGuild());
		
		guild.deletar();
		System.out.println();
		System.out.println("Servidor removido!");
		System.out.println(event.getGuild().getId());
		System.out.println(event.getGuild().getName());
		System.out.println("com " + event.getGuild().getMemberCount() + " membros.");
		System.out.println();
		
		int guilds = event.getJDA().getGuilds().size();
		event.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(guilds + " Servidores."));
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getChannel().isNSFW()) {
			event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Desculpe! Eu não atendo pedidos nesse tipo de canal :(").queue();
			return;
		}
		String[] args = event.getMessage().getContentRaw().split(" ");
		if (args[0].startsWith("ou!")) {
			String prefix = new SQLAccess(event.getGuild()).get("prefix");
			if (!prefix.equalsIgnoreCase("ou!")) {
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + " O prefixo neste servidor é: " + prefix).queue();
			}
		}
	}
	
	@Override
	public void onReady(ReadyEvent event) {
		int guilds = event.getGuildTotalCount();
		event.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(guilds + " Servidores."));
		List<Guild> guildas = event.getJDA().getGuilds();
		
		int newGuilds = 0;
		
		for (Guild guild : guildas) {
			SQLAccess sql = new SQLAccess(guild);
			if (!sql.existe()) {
				newGuilds++;	
			}
			sql.criar();
		}
		
		if (newGuilds == 0) {
			System.out.println("Não foi adicionado nenhum servidor novo, desde o ultimo update.");
			return;
		}
		

		
		System.out.println("Foi adicionado " + newGuilds + " novo(s) servidor(es), dede o ultimo update.");
	}

}

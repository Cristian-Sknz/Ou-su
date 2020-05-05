package me.skiincraft.discord.ousu.events;

import java.util.List;

import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static java.lang.System.out;

public class ReadyBotEvent extends ListenerAdapter {

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		SQLAccess guild = new SQLAccess(event.getGuild());
		guild.criar();
		out.println("");
		out.println("Novo servidor adicionado!");
		out.println(event.getGuild().getId());
		out.println(event.getGuild().getName());
		out.println("com " + event.getGuild().getMemberCount() + " membros.");
		out.println();
		
		int guilds = event.getJDA().getGuilds().size();
		event.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(guilds + " Servidores."));
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		SQLAccess guild = new SQLAccess(event.getGuild());

		guild.deletar();
		out.println();
		out.println("Servidor removido!");
		out.println(event.getGuild().getId());
		out.println(event.getGuild().getName());
		out.println("com " + event.getGuild().getMemberCount() + " membros.");
		out.println();

		int guilds = event.getJDA().getGuilds().size();
		event.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(guilds + " Servidores."));
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getChannel().isNSFW()) {
			event.getChannel().sendMessage(
					event.getAuthor().getAsMention() + " Desculpe! Eu não atendo pedidos nesse tipo de canal :(")
					.queue();
			return;
		}
		String[] args = event.getMessage().getContentRaw().split(" ");
		SQLAccess sql = new SQLAccess(event.getGuild());
		if (args[0].startsWith("ou!")) {
			String prefix = sql.get("prefix");
			if (!prefix.equalsIgnoreCase("ou!")) {
				LanguageManager lang = new LanguageManager(Language.valueOf(sql.get("language")));
				event.getChannel()
						.sendMessage(event.getAuthor().getAsMention() + " " + lang.translatedBot("PREFIX") + prefix)
						.queue();
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
			out.println("Não foi adicionado nenhum servidor novo, desde o ultimo update.");
			return;
		}

		out.println("Foi adicionado " + newGuilds + " novo(s) servidor(es), desde o ultimo update.");
	}

}

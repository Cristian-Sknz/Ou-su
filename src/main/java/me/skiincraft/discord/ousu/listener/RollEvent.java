package me.skiincraft.discord.ousu.listener;

import me.skiincraft.discord.core.configuration.GuildDB;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.commands.fun.RollCommand;
import me.skiincraft.discord.ousu.object.Participante;
import me.skiincraft.discord.ousu.object.Personagem;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RollEvent extends ListenerAdapter {
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (!RollCommand.contains(event.getAuthor().getIdLong())) {
			return;
		}
		String arg = event.getMessage().getContentRaw();
		GuildDB db = new GuildDB(event.getGuild());
		if (arg.toLowerCase().startsWith(db.get("prefix"))) {
			return;
		}
		
		Participante p = RollCommand.get(event.getAuthor().getIdLong());
		if (event.getChannel().getIdLong() != p.getChannelId()) {
			return;
		}
		if (!event.getChannel().canTalk()) {
			RollCommand.participando.remove(p);
			return;
		}
		
		if (!p.hasTime()) {
			RollCommand.participando.remove(p);
			return;
		}
		
		if (StringUtils.containsEqualsIgnoreCase(arg, "quit")) {
			RollCommand.participando.remove(p);
			event.getChannel().sendMessage("> Você desistiu deste personagem :sob:").queue();
			return;
		}
		
		Personagem perso = p.getPersonagem();
		String[] args = arg.split(" ");
		
		if (args[0].equalsIgnoreCase(perso.getName().split(" ")[0])) {
			event.getChannel().sendMessage(":white_check_mark: Parabéns, {user}! Personagem {personagem} está correto!"
					.replace("{user}", event.getAuthor().getAsMention())
					.replace("{personagem}", perso.getName())).queue();
			RollCommand.participando.remove(p);
		} else {
			if (p.getTentativa() == 2) {
				event.getChannel().sendMessage("> {user} Você perdeu! Não acertou o nome do personagem."
						.replace("{user}", event.getAuthor().getAsMention())).queue();
				RollCommand.participando.remove(p);
				return;
			}
			p.setTentativa(p.getTentativa() + 1);
			event.getMessage().addReaction("U+274C").queue();
		}
	}
	
}

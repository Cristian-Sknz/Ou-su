package me.skiincraft.discord.ousu.events;

import me.skiincraft.discord.ousu.embedtypes.DefaultEmbed;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OtherEvents extends ListenerAdapter {

	//Verificar se foi colocado somente o prefix
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String arg = e.getMessage().getContentRaw();
		if (e.getAuthor().isBot()) {
			return;
		}
		if (e.getJDA().getSelfUser()
				.getAsMention().equals(e.getAuthor().getAsMention())) {
			return;
		}
		if (e.getChannel().isNSFW()) {
			return;
		}
		SQLAccess sql = new SQLAccess(e.getGuild());
		LanguageManager lang = new LanguageManager(Language.valueOf(sql.get("language")));
		String prefix = sql.get("prefix");
		
		if (prefix.length() == 1) {
			return;
		}
		
		if (arg.equalsIgnoreCase(prefix)) {
			String[] prefixHelp = lang.translatedArrayHelp("PREFIX_HELP");
			e.getChannel().sendMessage(
					new DefaultEmbed(prefixHelp[0], StringUtils.arrayToString2(1, prefixHelp))
					.construirEmbed().build()).queue();
			return;
		}
		return;
	}
	
	
	
}

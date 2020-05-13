package me.skiincraft.discord.ousu.commands;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.skiincraft.api.ousu.exceptions.InvalidUserException;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.imagebuilders.OsuProfileNote;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import me.skiincraft.discord.ousu.mysql.SQLPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MentionCommand extends ListenerAdapter{

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String[] args = event.getMessage().getContentRaw().split(" ");
		List<User> mentions = event.getMessage().getMentionedUsers();
		if (event.getAuthor().isBot()) {
			return;
		}
		if (event.getAuthor().isFake()) {
			return;
		}
		if (event.isWebhookMessage()) {
			return;
		}
		if ((mentions.size() != 2)) {
			return;
		}
		if (args.length <= 1) {
			return;
		}
		if (!args[0].replaceAll("\\D+","").contains(OusuBot.getSelfUser().getAsMention().replaceAll("\\D+",""))) {
			return;
		}
		
		if (mentions.get(0) == mentions.get(1)) {
			return;
		}
		
		SQLPlayer sql = new SQLPlayer(mentions.get(1));
		
		StringBuilder complete = new StringBuilder();
		String data = new SimpleDateFormat("HH:mm:ss").format(new Date());

		complete.append("[" + event.getChannel().getGuild().getName());
		complete.append(":" + event.getChannel().getName());
		complete.append(" | " + data + "]:");

		String userFull = event.getAuthor().getName()+ "#" + event.getAuthor().getDiscriminator();

		System.out.println(complete.toString() + userFull + " executou o mention ou!user");
		
		if (sql.existe()) {
			event.getChannel().sendTyping();
			String nickname = sql.get("osu_account");
			
			try {
			me.skiincraft.api.ousu.users.User u = OusuBot.getOsu().getUser(nickname);
			SQLAccess sql2 = new SQLAccess(event.getGuild());
			
			InputStream drawer = OsuProfileNote.drawImage(u, Language.valueOf(sql2.get("language")));
			String aname = u.getUserID() + "userOsu.png";
			EmbedBuilder embed = UserCommand.embed(u, event.getGuild()).setImage("attachment://" + aname);
			event.getChannel().sendMessage(embed.build()).queue();
			
			event.getChannel().sendFile(drawer, aname)
					.embed(embed.build()).queue();
			} catch (InvalidUserException e) {
				return;
			}
		}
		
		
		
	}

}

package me.skiincraft.discord.ousu.commands;

import java.util.Arrays;

import me.skiincraft.api.ousu.exceptions.InvalidUserException;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.imagebuilders.OsuProfile;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.utils.DefaultEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class UserImageCommand extends Commands {

	public UserImageCommand() {
		super("ou!", "userimage", "ou!userimage <nickname> <Gamemode>", Arrays.asList("osuimage"));
	}

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("OSU_HELPMESSAGE_USERIMAGE");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Osu;
	}

	@Override
	public void action(String[] args, User user, TextChannel channel) {
		if (args.length == 1) {
			sendUsage().queue();
			return;
		}

		if (args.length >= 2) {

			me.skiincraft.api.ousu.users.User osuUser;
			try {
				StringBuffer stringArgs = new StringBuffer();
				for (int i = 1; i < args.length; i++) {
					stringArgs.append(args[i] + " ");
				}
				
				int length = stringArgs.toString().length() - 1;
				
				String usermsg = stringArgs.toString().substring(0, length);
				String lastmsg = args[args.length-1];
				String name = usermsg.replace(" " + lastmsg, "");
				
				if (Gamemode.getGamemode(lastmsg) != null) {
					osuUser = OusuBot.getOsu().getUser(name, Gamemode.getGamemode(lastmsg));
				} else {
					osuUser = OusuBot.getOsu().getUser(usermsg);
				}
				
			} catch (InvalidUserException e) {
				String[] str = getLang().translatedArrayOsuMessages("INEXISTENT_USER");
				StringBuffer buffer = new StringBuffer();
				for (String append : str) {
					if (append != str[0]) {
						buffer.append(append);
					}
				}

				sendEmbedMessage(new DefaultEmbed(str[0], buffer.toString())).queue();
				return;
			}
			channel.sendFile(OsuProfile.drawImage(osuUser), osuUser.getUserID() + "_osu.png")
			.embed(embed(osuUser).build()).queue();
			return;
		}
	}
	
	private EmbedBuilder embed(me.skiincraft.api.ousu.users.User osuUser) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(":frame_photo: " + osuUser.getUserName());
		embed.setImage("attachment://" + osuUser.getUserID() + "_osu.png");
		return embed;
	}
}

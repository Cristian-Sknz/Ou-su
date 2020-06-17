package me.skiincraft.discord.ousu.commands;

import java.io.InputStream;
import java.util.Arrays;

import me.skiincraft.api.ousu.exceptions.InvalidUserException;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.api.ousu.users.User;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.abstractcore.CommandCategory;
import me.skiincraft.discord.ousu.abstractcore.Commands;
import me.skiincraft.discord.ousu.customemoji.OusuEmojis;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.imagebuilders.OsuProfile;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.utils.InputStreamFile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class UserImageCommand extends Commands {

	public UserImageCommand() {
		super("ou!", "userimage", "ou!userimage <nickname> <Gamemode>", Arrays.asList("profileimage", "playerimage"));
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
	public void action(String[] args, String label, TextChannel channel) {
		if (args.length == 0) {
			sendUsage();
			return;
		}

		if (args.length >= 1) {

			User osuUser;
			try {
				StringBuffer stringArgs = new StringBuffer();
				for (int i = 0; i < args.length; i++) {
					stringArgs.append(args[i] + " ");
				}

				int length = stringArgs.length() - 1;

				String usermsg = stringArgs.toString().substring(0, length);
				String lastmsg = args[args.length - 1];
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
						buffer.append(OusuEmojis.getEmoteAsMention("small_red_diamond") + " " + append);
					}
				}
				MessageEmbed embed = TypeEmbed.WarningEmbed(str[0], buffer.toString()).build();
				reply(embed);
				return;
			}
			String aname = osuUser.getUserID() + "_osu";
			InputStream draw = OsuProfile.drawImage(osuUser, getLanguage());
			reply(embed(osuUser, aname).build(), new InputStreamFile(draw, aname, ".png"));
			return;
		}
	}

	private EmbedBuilder embed(me.skiincraft.api.ousu.users.User osuUser, String aname) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(":frame_photo: " + osuUser.getUserName());
		embed.setImage("attachment://" + aname + ".png");
		return embed;
	}
}

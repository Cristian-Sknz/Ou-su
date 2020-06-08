package me.skiincraft.discord.ousu.commands;

import java.io.InputStream;
import java.util.Arrays;

import me.skiincraft.api.ousu.exceptions.InvalidUserException;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.api.ousu.users.User;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.customemoji.OusuEmojis;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.imagebuilders.OsuCard;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import net.dv8tion.jda.api.entities.TextChannel;

public class CardCommand extends Commands {

	public CardCommand() {
		super("ou!", "card", "ou!card <nickname> <gamemode>", Arrays.asList("carduser", "osucard"));
	}

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("OSU_HELPMESSAGE_CARD");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Utilidade;
	}

	@Override
	public void action(String[] args, String label, TextChannel channel) {
		if (args.length == 0) {
			sendUsage().queue();
			return;
		}

		if (args.length >= 1) {
			User osuUser;
			try {
				StringBuffer stringArgs = new StringBuffer();
				for (int i = 0; i < args.length; i++) {
					stringArgs.append(args[i] + " ");
				}

				int length = stringArgs.toString().length() - 1;

				String usermsg = stringArgs.toString().substring(0, length);
				String lastmsg = args[args.length - 1];
				String name = usermsg.replace(" " + lastmsg, "");

				if (Gamemode.getGamemode(lastmsg) != null) {
					osuUser = OusuBot.getOsu().getUser(name, Gamemode.getGamemode(lastmsg));
				} else {
					osuUser = OusuBot.getOsu().getUser(usermsg);
				}
				
				InputStream drawer = OsuCard.drawImage(osuUser);
				
				String aname = osuUser.getUserID() + "userOsu.png";
				//EmbedBuilder embedlocal = embed(osuUser).setImage("attachment://" + aname);
				channel.sendMessage(getUser().getAsMention()).addFile(drawer, aname).queue();
				
			} catch (InvalidUserException e) {
				String[] str = getLang().translatedArrayOsuMessages("INEXISTENT_USER");
				StringBuffer buffer = new StringBuffer();
				for (String append : str) {
					if (append != str[0]) {
						buffer.append(OusuEmojis.getEmoteAsMention("small_red_diamond") + " " + append);
					}
				}

				sendEmbedMessage(TypeEmbed.WarningEmbed(str[0], buffer.toString())).queue();
				return;
			}
		}
	}
}
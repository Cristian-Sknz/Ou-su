package me.skiincraft.discord.ousu.commands;

import java.util.Arrays;

import me.skiincraft.api.ousu.exceptions.InvalidUserException;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.customemoji.EmojiCustom;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.imagebuilders.OsuProfile;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

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
	public void action(String[] args, String label, User user, TextChannel channel) {
		if (args.length == 0) {
			sendUsage().queue();
			return;
		}

		if (args.length >= 1) {

			me.skiincraft.api.ousu.users.User osuUser;
			try {
				StringBuffer stringArgs = new StringBuffer();
				for (int i = 0; i < args.length; i++) {
					stringArgs.append(args[i] + " ");
				}

				int length = stringArgs.toString().length() - 1;

				String usermsg = stringArgs.toString().substring(0, length);
				String lastmsg = args[args.length - 1];
				String name = usermsg.replace(" " + lastmsg, "");

				if (getEvent().getMessage().getMentionedUsers().size() != 0) {
					String userid = getEvent().getMessage().getMentionedUsers().get(0).getAsMention().replaceAll("\\D+",
							"");

					SQLPlayer sql = new SQLPlayer(OusuBot.getJda().getUserById(userid));
					if (sql.existe()) {
						String nic = sql.get("osu_account");
						name = nic;
						usermsg = nic;
					}
				}

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
						buffer.append(EmojiCustom.S_RDiamond.getEmoji() + " " + append);
					}
				}

				sendEmbedMessage(TypeEmbed.WarningEmbed(str[0], buffer.toString())).queue();
				return;
			}

			channel.sendFile(OsuProfile.drawImage(osuUser, getLanguage()), osuUser.getUserID() + "_osu.png")
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

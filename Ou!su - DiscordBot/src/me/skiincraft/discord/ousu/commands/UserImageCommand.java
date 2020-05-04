package me.skiincraft.discord.ousu.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import me.skiincraft.api.ousu.exceptions.InvalidUserException;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.imagebuilders.OsuProfile;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.utils.DefaultEmbed;
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

		if (args.length == 2) {

			me.skiincraft.api.ousu.users.User osuUser;
			try {
				osuUser = OusuBot.getOsu().getUser(args[1], Gamemode.Standard);
			} catch (InvalidUserException e) {
				sendEmbedMessage(new DefaultEmbed("Usuario inexistente", "Este usuario que você solicitou não existe."))
						.queue();
				return;
			}

			channel.sendFile(OsuProfile.drawImage(osuUser), osuUser.getUserID() + "_osu.png").queue();
			return;
		}

		if (args.length >= 3) {
			Gamemode gm = getGamemode(args[2]);
			if (gm == null) {
				gm = Gamemode.Standard;
			}

			me.skiincraft.api.ousu.users.User osuUser;
			try {
				osuUser = OusuBot.getOsu().getUser(args[1], gm);
			} catch (InvalidUserException e) {
				sendEmbedMessage(new DefaultEmbed("Usuario inexistente", "Este usuario que você solicitou não existe."))
						.queue();
				return;
			}

			channel.sendFile(OsuProfile.drawImage(osuUser), osuUser.getUserID() + "_osu.png").queue();
			return;
		}
	}

	public Gamemode getGamemode(String mode) {
		String gm = mode.toLowerCase();
		Map<String, Gamemode> map = new HashMap<>();

		map.put("standard", Gamemode.Standard);
		map.put("catch", Gamemode.Catch_the_Beat);
		map.put("mania", Gamemode.Mania);
		map.put("taiko", Gamemode.Taiko);

		if (map.containsKey(gm)) {
			return map.get(gm);
		}

		return null;
	}

}

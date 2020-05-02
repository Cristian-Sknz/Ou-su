package me.skiincraft.discord.ousu.commands;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.oopsjpeg.osu4j.GameMode;
import com.oopsjpeg.osu4j.exception.OsuAPIException;

import me.skiincraft.discord.ousu.imagebuilders.OsuProfile;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.osu.UserOsu;
import me.skiincraft.discord.ousu.utils.DefaultEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class UserImageCommand extends Commands {

	public UserImageCommand() {
		super("ou!", "userimage", "ou!userimage <nickname> <gamemode>", Arrays.asList("osuimage"));
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

			UserOsu osuUser;
			try {
				osuUser = new UserOsu(args[1], GameMode.STANDARD);
			} catch (MalformedURLException | OsuAPIException e) {
				e.printStackTrace();
				return;
			} catch (IndexOutOfBoundsException e) {
				sendEmbedMessage(new DefaultEmbed("Usuario inexistente", "Este usuario que você solicitou não existe."))
						.queue();
				return;
			}

			channel.sendFile(OsuProfile.drawImage(osuUser), osuUser.getUserid() + "_osu.png").queue();
			return;
		}

		if (args.length >= 3) {
			GameMode gm = getGamemode(args[2]);
			if (gm == null) {
				gm = GameMode.STANDARD;
			}

			UserOsu osuUser;
			try {
				osuUser = new UserOsu(args[1], gm);
			} catch (MalformedURLException | OsuAPIException e) {
				e.printStackTrace();
				return;
			} catch (IndexOutOfBoundsException e) {
				sendEmbedMessage(new DefaultEmbed("Usuario inexistente", "Este usuario que você solicitou não existe."))
						.queue();
				;
				return;
			}

			channel.sendFile(OsuProfile.drawImage(osuUser), osuUser.getUserid() + "_osu.png").queue();
			return;
		}
	}

	public GameMode getGamemode(String gamemode) {
		String gm = gamemode.toLowerCase();
		Map<String, GameMode> map = new HashMap<>();

		map.put("standard", GameMode.STANDARD);
		map.put("catch", GameMode.CATCH_THE_BEAT);
		map.put("mania", GameMode.MANIA);
		map.put("taiko", GameMode.TAIKO);

		if (map.containsKey(gm)) {
			return map.get(gamemode);
		}

		return null;
	}

}

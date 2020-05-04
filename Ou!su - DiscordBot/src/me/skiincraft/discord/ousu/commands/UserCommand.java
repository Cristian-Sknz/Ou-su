package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import me.skiincraft.api.ousu.exceptions.InvalidUserException;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.customemoji.OsuEmoji;
import me.skiincraft.discord.ousu.imagebuilders.OsuProfileNote;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.utils.DefaultEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class UserCommand extends Commands {

	public UserCommand() {
		super("ou!", "user", "ou!user <nickname> <gamemode>", Arrays.asList("osuplayer"));
	}

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("OSU_HELPMESSAGE_USER");
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
			InputStream drawer = OsuProfileNote.drawImage(osuUser);
			String aname = osuUser.getUserID() + "userOsu.png";
			channel.sendFile(drawer, aname).embed(embed(osuUser).setImage("attachment://" + aname).build()).queue();
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

			InputStream drawer = OsuProfileNote.drawImage(osuUser);
			String aname = osuUser.getUserID() + "userOsu.png";
			channel.sendFile(drawer, aname).embed(embed(osuUser).setImage("attachment://" + aname).build()).queue();
			return;
		}
	}

	public EmbedBuilder embed(me.skiincraft.api.ousu.users.User osuUser) {
		EmbedBuilder embed = new EmbedBuilder();

		embed.setAuthor(osuUser.getUserName(), osuUser.getURL(), osuUser.getUserAvatar());

		embed.setColor(Color.gray);
		embed.setTitle("Informações do Jogador");
		embed.setDescription("Você esta visualizando as informações do usuario [" + osuUser.getUserName() + "]("
				+ osuUser.getURL() + ")");

		NumberFormat f = NumberFormat.getNumberInstance();
		embed.addField("Ranking: ", "#" + f.format(osuUser.getRanking()), true);
		embed.addField("Ranking Nacional:", osuUser.getCountryCode() + " #" + f.format(osuUser.getNacionalRanking()),
				true);
		embed.addField("Tempo jogado:", osuUser.getPlayedHours().toString(), true);
		// embed.addBlankField(true);

		String accuracy = new DecimalFormat("#.0").format(osuUser.getAccuracy());

		accuracy += "%";
		String PP = OsuEmoji.PP.getEmojiString();
		embed.addField("Desempenho:",

				"Precisão: `" + accuracy + "`" + "\n" + PP + " " + f.format(osuUser.getPP()), true);

		embed.addField("Pontuação Total:", f.format(osuUser.getTotalScore()) + "", true);
		// embed.addField("Link:", osuUser.getUserUrl(), true);

		// embed.setThumbnail(osuUser.getAvatarURL());
		embed.setFooter("Sknz#4260 | Yagateiro Master",
				"https://osu.ppy.sh/images/flags/" + osuUser.getCountryCode() + ".png");
		return embed;
	}

	public Gamemode getGamemode(String gamemode) {
		String gm = gamemode.toLowerCase();
		Map<String, Gamemode> map = new HashMap<>();

		map.put("standard", Gamemode.Standard);
		map.put("catch", Gamemode.Catch_the_Beat);
		map.put("mania", Gamemode.Mania);
		map.put("taiko", Gamemode.Taiko);

		if (map.containsKey(gm)) {
			return map.get(gamemode);
		}

		return null;
	}

}

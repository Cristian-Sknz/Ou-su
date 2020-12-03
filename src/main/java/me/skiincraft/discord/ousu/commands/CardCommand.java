package me.skiincraft.discord.ousu.commands;

import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.api.ousu.entity.user.User;
import me.skiincraft.api.ousu.exceptions.UserException;
import me.skiincraft.api.ousu.requests.Request;
import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.command.ContentMessage;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.common.ImageBuilder;
import me.skiincraft.discord.ousu.utils.ImageAdapter;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CardCommand extends Comando {

	public CardCommand() {
		super("card", null, "card <username> [gamemode]");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Statistics;
	}

	public void execute(Member member, String[] args, InteractChannel channel) {
		if (args.length == 0) {
			replyUsage(channel.getTextChannel());
			return;
		}

		List<String> l = new ArrayList<>(Arrays.asList(args));
		Gamemode gm = Gamemode.Standard;

		StringBuffer b = new StringBuffer();
		if (args.length >= 2) {
			if (isGamemode(args[args.length-1])) {
				l.remove(args.length-1);
			}
		}

		l.forEach(s -> b.append(s).append(" "));
		l.clear();
		String nickname = b.substring(0, b.length() - 1);
		try {
			Request<User> request = OusuBot.getAPI().getUser(nickname, gm);
			User user = request.get();

			channel.reply(new ContentMessage(member.getAsMention(), new Card(user).draw(), ".png"));
		} catch (UserException e) {
			channel.reply(TypeEmbed.inexistentUser(nickname, getCategory(), getLanguageManager(channel.getTextChannel().getGuild())).build());
		} catch (Exception e){
			channel.reply(TypeEmbed.errorMessage(e, channel.getTextChannel()).build());
		}
	}
	
	public boolean isGamemode(String arg) {
		return Gamemode.getGamemode(arg.toLowerCase()) != null;
	}
	
	public static class Card extends ImageAdapter {
		
		private final User user;
		
		public Card(User user) {
			super(340, 94);
			this.user = user;
		}
		
		private String getAssets() {
			return OusuCore.getAssetsPath().toFile().getAbsolutePath();
		}
		
		public InputStream draw() {
			Font aurea = font("Aurea", 32F);
			Font euphemia = font("Euphemia", 11F);
			setAntialising();
			image(getAssets() + "/osu_images/Card_Overlay.png", 0, 0, size(),
					ImageBuilder.Alignment.Bottom_left);
			
			try {
				image(new URL(user.getUserAvatar()), 48, 44, new Dimension(76, 76), ImageBuilder.Alignment.Center);
			} catch (IOException e) {
				image(getAssets() + "/osu_images/nonexistentuser.png", 48, 44, new Dimension(76, 76),
						ImageBuilder.Alignment.Center);
			}
			
			setColor(Color.WHITE);
			
			getImageBuilder().addCentralizedStringY(user.getUsername(), 91, 28, aurea);
			try {
				image(new URL(user.getUserFlag()), 317, 25, new Dimension(28, 19), ImageBuilder.Alignment.Center);
				image(getAssets() + "/osu_images/modes/" + user.getGamemode().name().toLowerCase() + ".png", 
						293, 26, new Dimension(15, 15), ImageBuilder.Alignment.Center);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			setColor(Color.BLACK);
			NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
			DecimalFormat df = (DecimalFormat) nf;
			
			getImageBuilder().addRightStringY("#"+ nf.format(user.getCountryRanking())+ "", 283, 25, euphemia);
			getImageBuilder().addRightStringY(df.format(user.getAccuracy())+ "%", 323, 52, euphemia.deriveFont(14L));
			getImageBuilder().addRightStringY(nf.format(user.getPlayCount())+ "", 323, 70, euphemia.deriveFont(14L));
			
			return toInput();
		}
	}

}

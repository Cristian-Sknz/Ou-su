package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import me.skiincraft.api.ousu.Request;
import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.api.ousu.entity.user.User;
import me.skiincraft.api.ousu.exceptions.UserException;
import me.skiincraft.discord.core.command.ContentMessage;
import me.skiincraft.discord.core.plugin.Plugin;
import me.skiincraft.discord.core.utils.ImageBuilder.Alignment;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.emojis.OusuEmote;
import me.skiincraft.discord.ousu.imagebuilders.ImageAdapter;
import me.skiincraft.discord.ousu.messages.TypeEmbed;

import net.dv8tion.jda.api.entities.TextChannel;

public class CardCommand extends Comando {

	public CardCommand() {
		super("card", null, "card <user> [gamemode]");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Osu;
	}

	public void execute(net.dv8tion.jda.api.entities.User buser, String[] args, TextChannel channel) {
		if (args.length == 0) {
			replyUsage();
			return;
		}
		
		if (args.length >= 1) {
			List<String> l = new ArrayList<>(Arrays.asList(args));
			Gamemode gm = Gamemode.Standard;
			
			StringBuffer b = new StringBuffer();
			if (args.length >= 2) {
				if (isGamemode(args[args.length-1])) {
					l.remove(args.length-1);
				}
			}
			
			l.forEach(s -> b.append(s + " "));
			l.clear();
			try {
				Request<User> request = OusuBot.getApi().getUser(b.substring(0, b.length()-1), gm);
				User user = request.get();
				InputStream input = new Card(user).draw();
				
				reply(new ContentMessage(buser.getAsMention(), input, ".png"));
			} catch (UserException e) {
				String[] str = getLanguageManager().getStrings("Warning", "INEXISTENT_USER");
				
				StringBuffer buffer = new StringBuffer();
				for (String append : str) {
					if (append != str[0]) {
						buffer.append(OusuEmote.getEmoteAsMention("small_red_diamond") + " " + append);
					}
				}
				
				reply(TypeEmbed.WarningEmbed(str[0], buffer.toString()).build());
			}
			
			
		}
		
	}
	
	public boolean isGamemode(String arg) {
		return Gamemode.getGamemode(arg.toLowerCase()) != null;
	}
	
	public static class Card extends ImageAdapter {
		
		private User user;
		
		public Card(User user) {
			super(340, 94);
			this.user = user;
		}
		
		private String getAssets() {
			Plugin plugin = OusuBot.getMain().getPlugin();
			return plugin.getAssetsPath().getAbsolutePath();
		}
		
		
		public InputStream draw() {
			Font aurea = font("Aurea", Font.PLAIN, 32F);
			Font euphemia = font("Euphemia", Font.PLAIN, 11F);
			setAntialising();
			image(getAssets() + "/osu_images/Card_Overlay.png", 0, 0, size(),
					Alignment.Bottom_left);
			
			try {
				image(new URL(user.getUserAvatar()), 48, 44, new Dimension(76, 76), Alignment.Center);
			} catch (IOException e) {
				image(getAssets() + "/osu_images/nonexistentuser.png", 48, 44, new Dimension(76, 76),
						Alignment.Center);
			}
			
			setColor(Color.WHITE);
			
			getImageBuilder().addCentralizedStringY(user.getUsername(), 91, 28, aurea);
			try {
				image(new URL(user.getUserFlag()), 317, 25, new Dimension(28, 19), Alignment.Center);
				image(getAssets() + "/osu_images/modes/" + user.getGamemode().name().toLowerCase() + ".png", 
						293, 26, new Dimension(15, 15), Alignment.Center);
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

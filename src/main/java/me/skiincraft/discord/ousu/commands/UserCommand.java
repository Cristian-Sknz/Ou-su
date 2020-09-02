package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
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

import javax.imageio.ImageIO;

import me.skiincraft.api.ousu.Request;
import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.discord.core.command.ContentMessage;
import me.skiincraft.discord.core.configuration.Language;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.plugin.Plugin;
import me.skiincraft.discord.core.reactions.ReactionObject;
import me.skiincraft.discord.core.textfont.CustomFont;
import me.skiincraft.discord.core.utils.ImageUtils;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.emojis.OusuEmote;
import me.skiincraft.discord.ousu.htmlpage.JSoupGetters;
import me.skiincraft.discord.ousu.imagebuilders.ImageAdapter;
import me.skiincraft.discord.ousu.osu.UserStatistics;
import me.skiincraft.discord.ousu.reactions.HistoryLists;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class UserCommand extends Comando {

	public UserCommand() {
		super("user", Arrays.asList("usuario", "profile", "player", "jogador"), "user <name> [gamemode]");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Osu;
	}

	public void execute(User buser, String[] args, TextChannel channel) {
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
			
			String nickname = b.substring(0, b.length() - 1);

			Request<me.skiincraft.api.ousu.entity.user.User> request = OusuBot.getApi().getUser(nickname, gm);
			me.skiincraft.api.ousu.entity.user.User user = request.get();
			InputStream draw = new UserScore(user).draw(getLanguageManager().getLanguage());
			final EmbedBuilder embedlocal = embed(user);
			ContentMessage content = new ContentMessage(embedlocal.build(), draw, "png").setInputName("user_ousu");
			reply(content, message ->{
				try {
					List<EmbedBuilder> reactions = new ArrayList<>();
					reactions.add(embedlocal.setImage("attachment://" + content.getInputName() + content.getInputExtension()));
					reactions.add(embed2(JSoupGetters.inputType(user, getLanguageManager()), embedlocal)
							.setImage("attachment://" + content.getInputName() + content.getInputExtension()));
					
					HistoryLists.addToReaction(buser, message, new ReactionObject(reactions, 0));
					message.addReaction("U+1F4CE").queue();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			return;
		}
	}
	
	public EmbedBuilder embed(me.skiincraft.api.ousu.entity.user.User user) {
		EmbedBuilder embed = new EmbedBuilder();
		NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
		DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("pt", "BR"));
		LanguageManager lang = getLanguageManager();
		df.applyPattern("#.0");
		
		String accuracy = df.format(user.getAccuracy());
		String pp = OusuEmote.getEmoteAsMentionEquals("pp");
		
		String code = ":flag_" + user.getCountryCode().toLowerCase() + ": " + user.getCountryCode();
		
		embed.setAuthor(user.getUsername(), user.getURL(), user.getUserAvatar());
		embed.setTitle(lang.getString("Embeds", "USER_COMMAND_PLAYERSTATS"));
		
		embed.setDescription(":map: " + lang.getString("Embeds", "RANKING") + ": #" + nf.format(user.getRanking()) + "\n");
		embed.appendDescription(":clock3: " + lang.getString("Embeds", "PLAYED_TIME") + ": " + user.getPlayedHours().toString() + "\n");
		embed.appendDescription(OusuEmote.getEmoteAsMention("cursor") + " " + lang.getString("Embeds", "TOTAL_SCORE") + ": " + user.getTotalScore());
		
		embed.addField(lang.getString("Embeds", "PERFORMANCE"),
				":pen_ballpoint: " + lang.getString("Embeds", "ACCURACY") + "`" + (accuracy += "%") + "`" + "\n" + pp + " " + nf.format((long) user.getPP()), true);
		
		embed.addField(lang.getString("Embeds", "NATIONAL_RANKING"),
				code + " #" + nf.format(user.getCountryRanking()), true);
		
		embed.addField(lang.getString("Embeds", "JOIN_DATE"), user.getJoinDate(), true);
	
		try {
			embed.setColor(ImageUtils.getPredominatColor(ImageIO.read(new URL(user.getUserAvatar()))));
			embed.setThumbnail(user.getUserAvatar());
			embed.setAuthor(user.getUsername(), user.getURL(), user.getUserAvatar());
		} catch (NullPointerException | IOException e) {
			embed.setColor(Color.YELLOW);
			embed.setThumbnail("https://i.imgur.com/tG1btnR.png");
			embed.setAuthor(user.getUsername(), user.getURL(), "https://i.imgur.com/tG1btnR.png");
		}
		return embed;
	}
	
	public EmbedBuilder embed2(UserStatistics osuUser, EmbedBuilder embedBuilder) {
		LanguageManager lang = getLanguageManager();
		EmbedBuilder embed = new EmbedBuilder(embedBuilder);
		embed.clearFields();
		
		embed.setDescription(":map: " + lang.getString("Embeds", "USERID") + ": " + osuUser.getUser().getUserId() + "\n");
		embed.appendDescription(":calendar: " + lang.getString("Embeds", "JOINED") + ": " + osuUser.getFirstlogin() + "\n");
		embed.appendDescription(":clock3: " + lang.getString("Embeds", "LASTACTIVE") + ": " + osuUser.getLastActive());

		embed.addField(lang.getString("Embeds", "INPUTS"), osuUser.getInputEmotes(), true);
		embed.addField("Level", osuUser.getUser().getLevel() + "", true);
		//embed.addField(lang.getString("Embeds", "LASTPP"), osuUser.getLastPpCapture() + "", true);

		embed.setFooter(lang.getString("Default", "FOOTER_DEFAULT"),
				"https://osu.ppy.sh/images/flags/" + osuUser.getUser().getCountryCode() + ".png");
		return embed;
	}
	
	public boolean isGamemode(String arg) {
		return Gamemode.getGamemode(arg.toLowerCase()) != null;
	}
	
	public static class UserScore extends ImageAdapter {

		private me.skiincraft.api.ousu.entity.user.User user;
		
		public UserScore(me.skiincraft.api.ousu.entity.user.User user) {
			super(900, 250);
			this.user = user;
		}
		
		private String getAssets() {
			Plugin plugin = OusuBot.getMain().getPlugin();
			return plugin.getAssetsPath().getAbsolutePath();
		}
		
		private void scoreCalculates(int level, int x) {
			String l = level + "";
			Font cf = new CustomFont().getFont("ARLRDBD", Font.PLAIN, 34F);
			// new Font("Arial", Font.PLAIN, 34)
			if (l.length() == 5) {
				getImageBuilder().addCentralizedString(l, x, 211, cf);
			} else {
				//new Font("Arial", Font.PLAIN, 38)
				getImageBuilder().addCentralizedString(l, x, 211, cf.deriveFont(38F));
			}
		}
		
		public InputStream draw(Language language) {
			setAntialising();
			if (language.getLanguageCode().equalsIgnoreCase("en")) {
				image(getAssets() + "/osu_images/notes/LayerEN.png", 0, 0, getImageBuilder().getSize(), null);
			}
			if (language.getLanguageCode().equalsIgnoreCase("pt")) {
				image(getAssets() + "/osu_images/notes/Layer.png", 0, 0, getImageBuilder().getSize(), null);
			}
			setColor(new Color( 138, 0, 103));
			
			scoreCalculates(user.getSSh(), 150);
			scoreCalculates(user.getSS(), 294);
			scoreCalculates(user.getSh(), 444);
			scoreCalculates(user.getS(), 595);
			scoreCalculates(user.getA(), 739);
			
			return toInput();
		}
	}

}

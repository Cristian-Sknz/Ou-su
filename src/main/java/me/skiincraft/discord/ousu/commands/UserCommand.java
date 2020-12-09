package me.skiincraft.discord.ousu.commands;

import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.api.ousu.entity.objects.PlayedTime;
import me.skiincraft.api.ousu.entity.user.User;
import me.skiincraft.api.ousu.exceptions.UserException;
import me.skiincraft.api.ousu.requests.Request;
import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.command.ContentMessage;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.common.CustomFont;
import me.skiincraft.discord.core.common.reactions.ReactionObject;
import me.skiincraft.discord.core.common.reactions.Reactions;
import me.skiincraft.discord.core.common.reactions.custom.ReactionPage;
import me.skiincraft.discord.core.configuration.Language;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.crawler.WebCrawler;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import me.skiincraft.discord.ousu.utils.ImageAdapter;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import me.skiincraft.discord.ousu.osu.UserStatistics;
import me.skiincraft.discord.ousu.utils.ImageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserCommand extends Comando {

	public UserCommand() {
		super("user", Arrays.asList("usuario", "profile", "player", "jogador"), "user <username> [gamemode]");
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
		LanguageManager lang = getLanguageManager(channel.getTextChannel().getGuild());
		try {
			Request<User> request = OusuBot.getAPI().getUser(nickname, gm);
			User user = request.get();
			InputStream draw = new UserScore(user).draw(lang.getLanguage());
			final EmbedBuilder embedlocal = embed(user, lang);
			ContentMessage content = new ContentMessage(embedlocal.build(), draw, "png").setInputName("user_ousu");
			channel.reply(content, message -> {
				try {
					List<EmbedBuilder> reactions = new ArrayList<>();
					reactions.add(embedlocal.setImage("attachment://" + content.getInputName() + content.getInputExtension()));
					reactions.add(embed2(user, Objects.requireNonNull(WebCrawler.getOtherStatistics(null, lang.getLanguage(), user.getUserId())), embedlocal, lang)
							.setImage("attachment://" + content.getInputName() + content.getInputExtension()));

					Objects.requireNonNull(Reactions.getInstance()).registerReaction(new ReactionObject(message, member.getIdLong(),
							new String[]{"U+1F4CE"}), new ReactionPage(reactions, true));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (UserException e){
			channel.reply(TypeEmbed.inexistentUser(nickname, getCategory(), lang).build());
		} catch (Exception e){
			channel.reply(TypeEmbed.errorMessage(e, channel.getTextChannel()).build());
		}
	}


	
	public EmbedBuilder embed(me.skiincraft.api.ousu.entity.user.User user, LanguageManager lang) {
		EmbedBuilder embed = new EmbedBuilder();
		NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
		DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("pt", "BR"));
		df.applyPattern("#.0");
		
		String accuracy = df.format(user.getAccuracy());
		String pp = GenericsEmotes.getEmoteAsMentionEquals("pp");
		
		String code = ":flag_" + user.getCountryCode().toLowerCase() + ": " + user.getCountryCode();
		
		embed.setAuthor(user.getUsername(), user.getURL(), user.getUserAvatar());
		embed.setTitle(lang.getString("Embeds", "USER_COMMAND_PLAYERSTATS"));
		
		embed.setDescription(":map: " + lang.getString("Embeds", "RANKING") + ": #" + nf.format(user.getRanking()) + "º\n");
		embed.appendDescription(":clock3: " + lang.getString("Embeds", "PLAYED_TIME") + ": " + convert(user.getPlayedHours()) + "\n");
		embed.appendDescription(":cyclone: " + lang.getString("Embeds", "ACCURACY") + " `"  + accuracy + "%" + "`");
		embed.addField(lang.getString("Embeds", "NATIONAL_RANKING"),
				code + " #" + nf.format(user.getCountryRanking())+ "º", true);

		embed.addField(lang.getString("Embeds", "PERFORMANCE"),
				pp + " " + nf.format((long) user.getPP()), true);

		
		embed.addField(lang.getString("Embeds", "TOTAL_SCORE"), nf.format(user.getTotalScore()), true);
		embed.setFooter(lang.getString("Embeds", "JOIN_DATE"));
		embed.setTimestamp(user.getJoinDate());
		//
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
	
	public EmbedBuilder embed2(User user, UserStatistics osuUser, EmbedBuilder embedBuilder, LanguageManager lang) {
		EmbedBuilder embed = new EmbedBuilder(embedBuilder);
		embed.clearFields();
		
		embed.setDescription(":map: " + lang.getString("Embeds", "USERID") + ": " + user.getUserId() + "\n");
		embed.appendDescription(":calendar: " + lang.getString("Embeds", "JOINED") + ": " + osuUser.getFirstlogin() + "\n");
		embed.appendDescription(":clock3: " + lang.getString("Embeds", "LASTACTIVE") + ": " + osuUser.getLastActive());

		embed.addField(lang.getString("Embeds", "INPUTS"), osuUser.getInputEmotes(), true);
		embed.addField("Level", user.getLevel() + "", true);
		//embed.addField(lang.getString("Embeds", "LASTPP"), osuUser.getLastPpCapture() + "", true);

		embed.setFooter(lang.getString("Default", "FOOTER_DEFAULT"),
				"https://osu.ppy.sh/images/flags/" + user.getCountryCode() + ".png");
		return embed;
	}

	public String convert(PlayedTime playedTime){
		if (playedTime.getDays() == 0 && playedTime.getHours() == 0){
			return playedTime.getMinutes() + " minute(s)";
		}

		if (playedTime.getDays() == 0){
			return playedTime.getHours() + " hour(s)";
		}

		return TimeUnit.DAYS.toHours(playedTime.getDays()) + playedTime.getHours() + " hour(s)";
	}
	
	public boolean isGamemode(String arg) {
		return Gamemode.getGamemode(arg.toLowerCase()) != null;
	}
	
	public static class UserScore extends ImageAdapter {

		private final me.skiincraft.api.ousu.entity.user.User user;
		
		public UserScore(me.skiincraft.api.ousu.entity.user.User user) {
			super(900, 250);
			this.user = user;
		}
		
		private String getAssets() {
			return OusuCore.getAssetsPath().toFile().getAbsolutePath();
		}
		
		private void scoreCalculates(int level, int x) {
			String l = level + "";
			Font cf = CustomFont.getFont("ARLRDBD", Font.PLAIN, 34F);
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
